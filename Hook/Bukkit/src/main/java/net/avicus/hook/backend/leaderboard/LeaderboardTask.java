package net.avicus.hook.backend.leaderboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.avicus.hook.Hook;
import net.avicus.magma.database.Database;
import net.avicus.magma.database.model.impl.LeaderboardEntry;
import net.avicus.magma.database.model.impl.LeaderboardEntry.Period;
import net.avicus.magma.database.model.impl.ObjectiveCompletion;
import net.avicus.magma.database.model.impl.ObjectiveType;
import net.avicus.magma.database.model.impl.Punishment.Type;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.database.table.impl.LeaderboardTable;
import net.avicus.magma.database.table.impl.ObjectiveTable;
import net.avicus.magma.database.table.impl.ObjectiveTypeTable;
import net.avicus.magma.database.table.impl.UserTable;
import net.avicus.quest.model.ModelIterator;
import net.avicus.quest.query.Filter;
import net.avicus.quest.query.Operator;
import net.avicus.quest.query.Row;
import net.avicus.quest.query.RowIterator;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/**
 * Handles accumulating kills, deaths, objectives and more and
 * populating the leaderboard table in the database.
 * <p>
 * Todo:
 */
public class LeaderboardTask extends Thread {

  private static final long TEN_SECONDS = 1000 * 10;
  private static final PeriodFormatter PERIOD_FORMAT = new PeriodFormatterBuilder()
      .printZeroAlways()
      .minimumPrintedDigits(2) // gives the '01'
      .appendHours()
      .appendSeparator(":")
      .appendMinutes()
      .appendSeparator(":")
      .appendSeconds()
      .toFormatter();

  private final Database database;
  private final Logger log;
  private final LeaderboardTable leaderboard;
  private final UserTable users;
  private final ObjectiveTable objectives;
  private final ObjectiveTypeTable objectiveTypes;

  private long lastIntervalLog = 0;
  private Map<Integer, String> objectiveNames;  // objective type id -> objective type name
  private Map<Period, Map<Integer, LeaderboardEntry>> entries;  // period -> user id -> entry

  public LeaderboardTask(Database database) {
    this.database = database;
    this.log = Hook.plugin().getLogger();
    this.leaderboard = database.getLeaderboard();
    this.users = database.getUsers();
    this.objectives = database.getObjectiveCompletions();
    this.objectiveTypes = database.getObjectiveTypes();
  }

  private LeaderboardEntry getOrCreateEntry(Map<Integer, LeaderboardEntry> data, Period period,
      int userId) {
    // We assume the entries map is already populated, for efficiency
    LeaderboardEntry entry = data.get(userId);

    if (entry == null) {
      entry = new LeaderboardEntry(userId, period);
      data.put(userId, entry);
    }

    return entry;
  }

  /**
   * Log a message if and only if it has been an amount of time since
   * the last interval log.
   *
   * @param interval The time to wait.
   * @param level The level.
   * @param message The message.
   */
  private void intervalLog(long interval, Level level, String message) {
    long now = System.currentTimeMillis();

    if (now - this.lastIntervalLog > interval) {
      this.log.log(level, message);
      this.lastIntervalLog = now;
    }
  }

  private void populateObjectiveNames() {
    this.objectiveNames = new HashMap<>();
    for (ObjectiveType type : this.objectiveTypes.select().execute()) {
      this.objectiveNames.put(type.getId(), type.getName());
    }
  }

  private void incrementObjectiveByTypeId(LeaderboardEntry entry, ObjectiveCompletion objective) {
    String name = this.objectiveNames.get(objective.getObjectiveTypeId());

    if (name == null) {
      this.log.severe("Unknown objective type ID: " + objective.getObjectiveTypeId());
      return;
    }

    switch (name) {
      case "monument":
        entry.incrementMonuments();
        break;
      case "wool":
        entry.incrementWools();
        break;
      case "hill":
        entry.incrementHills();
        break;
      case "flag":
        entry.incrementFlags();
        break;
      case "score":
        entry.incrementScore();
        break;
    }
  }

  /**
   * Runs the leaderboard task. This updates the `leaderboard_entries` table
   * with up-to-date stats in a process describe below.
   * <p>
   * First, players are grouped by IDs. The first section is 1 to "userGrouping",
   * the next is from "userGrouping" to "userGrouping" * 2, etc.
   * <p>
   * Within each group, each leaderboard period is iterated (weekly, monthly,
   * overall).
   * <p>
   * Finally, each statistic is retrieved for all the users in the selected
   * group and in the selected period (kills, deaths, objectives).
   */
  @Override
  public void run() {
    Instant start = Instant.now();
    this.log.info("================");
    this.log.info("= Task started =");
    this.log.info("================");

    populateObjectiveNames();

    // Storing stats for every player ever at once would use way too much
    // memory, so we perform grouped queries by user id. (ex. all users less
    // than 50,000, then 50,000 to 100,000, etc.)
    int userGrouping = 1000;

    // To do so, we must find the greatest user id, so we know where to stop.
    int maxUserId = -1;

    List<User> maxUser = this.users.select().order("id", "DESC").limit(1).execute();
    if (!maxUser.isEmpty()) {
      maxUserId = maxUser.get(0).getId();
    }

    this.log
        .info(String.format("Grouping users by %s up to %s", userGrouping + "", maxUserId + ""));

    // Start at 1, increment by grouping until we reach the end.
    for (int low = 1; low <= maxUserId + userGrouping; low += userGrouping) {
      // Clear entries
      this.entries = new HashMap<>();

      int high = low + userGrouping;

      // Perform a check to see if any users even exist in this range
      Filter testFilter = new Filter("id", low, Operator.GREATER_OR_EQUAL)
          .and("id", high, Operator.LESS);
      List<User> userCheck = this.users.select().where(testFilter).limit(1).execute();
      if (userCheck.isEmpty()) {
        this.log
            .warning(String.format("No users found with an ID in [%s, %s)", low + "", high + ""));
        continue;
      } else {
        this.log.info(String.format("Found users in [%s, %s)", low + "", high + ""));
      }

      // user id between low and high
      Filter userFilter = new Filter("user_id", low, Operator.GREATER_OR_EQUAL)
          .and("user_id", high, Operator.LESS);

      // Go through each period
      for (Period period : Period.values()) {
        Map<Integer, LeaderboardEntry> data = new HashMap<>();
        this.entries.put(period, data);

        // Create filter
        Filter filter = userFilter;

        List<Integer> bannedUsers = new ArrayList<>();
        Filter banFilter = new Filter("user_id", low, Operator.GREATER_OR_EQUAL)
            .and("user_id", high, Operator.LESS)
            .and("type", Type.BAN.toString())
            .and("appealed", 0);

        {
          RowIterator punishments = this.database.select("punishments").where(banFilter)
              .executeIterator();
          int count = 1;
          this.log.info("Streaming bans");
          while (punishments.hasNext()) {
            bannedUsers.add(punishments.next().getInteger("user_id"));
            intervalLog(TEN_SECONDS, Level.INFO, "... (" + count + ")");
            count++;
          }
          this.log.info("Done streaming bans");
        }

        Date earliest = null;
        if (period.getDuration() != null) {
          earliest = start.minus(period.getDuration()).toDate();
          filter = new Filter("created_at", earliest, Operator.GREATER_OR_EQUAL).and(userFilter);
        }

        this.log.info("Period " + period + " = " + filter.build());

        // Deaths
        {
          Filter causeFilter = new Filter("user_id", low, Operator.GREATER_OR_EQUAL)
              .and("user_id", high, Operator.LESS);
          causeFilter.and("user_hidden", false);
          Filter periodFilter = causeFilter;
          if (earliest != null) {
            periodFilter = new Filter("created_at", earliest, Operator.GREATER_OR_EQUAL)
                .and(causeFilter);
          }
          RowIterator deathSelect = this.database.select("deaths").where(periodFilter)
              .executeIterator();

          int count = 1;
          this.log.info("Streaming deaths");
          while (deathSelect.hasNext()) {
            Row row = deathSelect.next();

            int userId = row.getInteger("user_id");

            if (bannedUsers.contains(userId)) {
              continue;
            }

            LeaderboardEntry userEntry = getOrCreateEntry(data, period, userId);
            userEntry.incrementDeaths();

            intervalLog(TEN_SECONDS, Level.INFO, "... (" + count + ")");
            count++;
          }
          this.log.info("Done streaming deaths");
        }

        // Kills
        {
          Filter causeFilter = new Filter("cause", low, Operator.GREATER_OR_EQUAL)
              .and("cause", high, Operator.LESS);
          causeFilter.and("cause_hidden", false);
          Filter periodFilter = causeFilter;
          if (earliest != null) {
            periodFilter = new Filter("created_at", earliest, Operator.GREATER_OR_EQUAL)
                .and(causeFilter);
          }
          RowIterator killsSelect = this.database.select("deaths").where(periodFilter)
              .executeIterator();

          int count = 1;
          this.log.info("Streaming kills");
          while (killsSelect.hasNext()) {
            Row kill = killsSelect.next();

            int cause = kill.getInteger("cause");

            if (bannedUsers.contains(cause)) {
              continue;
            }

            LeaderboardEntry userEntry = getOrCreateEntry(data, period, cause);
            userEntry.incrementKills();

            intervalLog(TEN_SECONDS, Level.INFO, "... (" + count + ")");
            count++;
          }
          this.log.info("Done streaming kills");
        }

        // KD Ratio
        {
          this.log.info("Updating KD ratios");
          data.values().forEach(LeaderboardEntry::updateKdRatio);
          this.log.info("Done updating KD ratios");
        }

        // Objectives
        {
          ModelIterator<ObjectiveCompletion> objectiveSelect = this.objectives.select()
              .where(filter).executeIterator();

          int count = 1;
          this.log.info("Streaming objectives");
          while (objectiveSelect.hasNext()) {
            ObjectiveCompletion objective = objectiveSelect.next();

            int userId = objective.getUserId();

            if (bannedUsers.contains(userId)) {
              continue;
            }

            LeaderboardEntry entry = getOrCreateEntry(data, period, userId);
            incrementObjectiveByTypeId(entry, objective);

            intervalLog(TEN_SECONDS, Level.INFO, "... (" + count + ")");
            count++;
          }
          this.log.info("Done streaming objectives");
        }

        // Sessions
        {
          RowIterator sessionSelect = this.database.select("sessions")
              .columns("user_id", "duration").where(filter).executeIterator();

          int count = 1;
          this.log.info("Streaming sessions");
          while (sessionSelect.hasNext()) {
            Row row = sessionSelect.next();

            int userId = row.getInteger("user_id");
            Integer seconds = row.get(Integer.class, "duration");

            if (seconds == null || bannedUsers.contains(userId)) {
              continue;
            }

            LeaderboardEntry entry = getOrCreateEntry(data, period, userId);
            entry.incrementTimeOnline(seconds);

            intervalLog(TEN_SECONDS, Level.INFO, "... (" + count + ")");
            count++;
          }
          this.log.info("Done streaming sessions");
        }

        // Hopefully, entries is now full of data, ready to be inserted.
        if (!this.entries.get(period).isEmpty()) {
          Filter deleteFilter = new Filter("period", period.ordinal()).and(userFilter);
          this.log.info("Delete old entries for group in " + period);
          this.leaderboard.delete().where(deleteFilter).execute();

          this.log.info("Inserting new entries for group in " + period);
          Collection<LeaderboardEntry> insert = this.entries.get(period).values();
          this.leaderboard.multiInsert(insert).execute();
        }
      }

    }

    Instant end = Instant.now();

    Duration duration = new Duration(start, end);

    this.log.info("============================");
    this.log.info("= Task completed: " + PERIOD_FORMAT.print(duration.toPeriod()) + " =");
    this.log.info("============================");
  }
}
