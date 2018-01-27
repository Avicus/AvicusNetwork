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
import net.avicus.magma.Magma;
import net.avicus.magma.database.Database;
import net.avicus.magma.database.model.impl.ExperienceLeaderboardEntry;
import net.avicus.magma.database.model.impl.ExperienceTransaction;
import net.avicus.magma.database.model.impl.PrestigeLevel;
import net.avicus.magma.database.model.impl.Punishment.Type;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.database.table.impl.ExperienceLeaderboardTable;
import net.avicus.magma.database.table.impl.ExperienceTransactionTable;
import net.avicus.magma.database.table.impl.PrestigeLevelTable;
import net.avicus.magma.database.table.impl.UserTable;
import net.avicus.magma.util.MapGenre;
import net.avicus.quest.model.ModelIterator;
import net.avicus.quest.query.Filter;
import net.avicus.quest.query.Operator;
import net.avicus.quest.query.RowIterator;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/**
 * Handles accumulating XP for each genre and
 * populating the leaderboard table in the database.
 */
public class XPLeaderboardTask extends Thread {

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
  private final ExperienceLeaderboardTable leaderboard;
  private final UserTable users;
  private final PrestigeLevelTable levels;
  private final ExperienceTransactionTable xpTransactions;

  private long lastIntervalLog = 0;
  private Map<ExperienceLeaderboardEntry.Period, Map<Integer, ExperienceLeaderboardEntry>> entries;  // period -> user id -> entry

  public XPLeaderboardTask(Database database) {
    this.database = database;
    this.log = Logger.getLogger("XP Task");
    this.log.setParent(Hook.plugin().getLogger());
    this.leaderboard = database.getXpLeaderBoard();
    this.users = database.getUsers();
    this.levels = database.getPrestigeLevels();
    this.xpTransactions = database.getXpTransations();
  }

  private ExperienceLeaderboardEntry getOrCreateEntry(Map<Integer, ExperienceLeaderboardEntry> data,
      ExperienceLeaderboardEntry.Period period, int userId) {
    // We assume the entries map is already populated, for efficiency
    ExperienceLeaderboardEntry entry = data.get(userId);

    if (entry == null) {
      entry = new ExperienceLeaderboardEntry(userId, period);
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

  private void incrementGenre(ExperienceLeaderboardEntry entry, MapGenre genre, int amount) {
    // Legacy
    if (genre == null) {
      return;
    }

    switch (genre) {
      case NEBULA:
        entry.incrementNebulaXP(amount);
        break;
      case ARCADE:
        entry.incrementArcadeXP(amount);
        break;
      case CTF:
        entry.incrementCTFXP(amount);
        break;
      case ELIMINATION:
        entry.incrementEliminationXP(amount);
        break;
      case KOTH:
        entry.incrementKOTHXP(amount);
        break;
      case SKY_WARS:
        entry.incrementSWXP(amount);
        break;
      case TDM:
        entry.incrementTDMXP(amount);
        break;
      case WALLS:
        entry.incrementWallsXP(amount);
        break;
    }
  }

  /**
   * Runs the leaderboard task. This updates the `experience_leaderboard_entries` table
   * with up-to-date stats in a process describe below.
   * <p>
   * First, players are grouped by IDs. The first section is 1 to "userGrouping",
   * the next is from "userGrouping" to "userGrouping" * 2, etc.
   * <p>
   * Within each group, each leaderboard period is iterated (weekly, monthly,
   * overall).
   * <p>
   * Finally, each genre statistic is retrieved for all the users in the selected
   * group and in the selected period.
   */
  @Override
  public void run() {
    Instant start = Instant.now();
    this.log.info("================");
    this.log.info("= Task started =");
    this.log.info("================");

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
      for (ExperienceLeaderboardEntry.Period period : ExperienceLeaderboardEntry.Period.values()) {
        Map<Integer, ExperienceLeaderboardEntry> data = new HashMap<>();
        this.entries.put(period, data);

        // Create filter
        Filter filter = new Filter(userFilter)
            .and("season_id", Magma.get().getCurrentSeason().getId());

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
          filter = new Filter("created_at", earliest, Operator.GREATER_OR_EQUAL).and(filter);
        }

        this.log.info("Period " + period + " = " + filter.build());

        // XP Transaction
        {
          ModelIterator<ExperienceTransaction> xpSelect = this.xpTransactions.select().where(filter)
              .executeIterator();

          int count = 1;
          this.log.info("Streaming transactions");
          while (xpSelect.hasNext()) {
            ExperienceTransaction transaction = xpSelect.next();

            int userId = transaction.getUserId();

            if (bannedUsers.contains(userId)) {
              continue;
            }

            ExperienceLeaderboardEntry entry = getOrCreateEntry(data, period, userId);
            incrementGenre(entry, MapGenre.of(transaction.getGenre()), transaction.getAmount());

            entry.incrementTotalXP(transaction.getAmount());

            intervalLog(TEN_SECONDS, Level.INFO, "... (" + count + ")");
            count++;
          }
          this.log.info("Done streaming transactions");
        }

        // Prestige Levels
        {
          filter = new Filter(userFilter).and("season_id", Magma.get().getCurrentSeason().getId());

          ModelIterator<PrestigeLevel> levelSelect = this.levels.select().where(filter)
              .executeIterator();

          int count = 1;
          this.log.info("Streaming levels");
          while (levelSelect.hasNext()) {
            PrestigeLevel level = levelSelect.next();

            int userId = level.getUserId();

            if (bannedUsers.contains(userId)) {
              continue;
            }

            ExperienceLeaderboardEntry entry = getOrCreateEntry(data, period, userId);

            entry.incrementPrestigeLevel();

            intervalLog(TEN_SECONDS, Level.INFO, "... (" + count + ")");
            count++;
          }
          this.log.info("Done streaming levels");
        }

        // Level Calculation
        {
          int count = 1;
          this.log.info("Calculating levels");
          for (ExperienceLeaderboardEntry entry : this.entries.get(period).values()) {
            double totalXP = entry.getXpTotal();
            entry.setLevel(totalXP / net.avicus.magma.module.prestige.PrestigeLevel.MAX.getXp());

            intervalLog(TEN_SECONDS, Level.INFO, "... (" + count + ")");
            count++;
          }
          this.log.info("Done calculating levels");
        }

        // Hopefully, entries is now full of data, ready to be inserted.
        if (!this.entries.get(period).isEmpty()) {
          Filter deleteFilter = new Filter("period", period.ordinal()).and(userFilter);
          this.log.info("Delete old entries for group in " + period);
          this.leaderboard.delete().where(deleteFilter).execute();

          this.log.info("Inserting new entries for group in " + period);
          Collection<ExperienceLeaderboardEntry> insert = this.entries.get(period).values();
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
