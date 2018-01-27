package net.avicus.hook;

import com.google.common.collect.Maps;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import lombok.Getter;
import net.avicus.hook.wrapper.DiscordUser;
import net.avicus.hook.wrapper.HookRole;
import net.avicus.magma.NetworkIdentification;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.database.table.impl.UserTable;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.GuildController;
import net.dv8tion.jda.core.utils.PermissionUtil;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class UserManagementService extends ListenerAdapter {

  private static final PeriodFormatter PERIOD_FORMAT = new PeriodFormatterBuilder()
      .printZeroAlways()
      .minimumPrintedDigits(2) // gives the '01'
      .appendHours()
      .appendSeparator(":")
      .appendMinutes()
      .appendSeparator(":")
      .appendSeconds()
      .toFormatter();

  private final Hook hook;
  @Getter
  private final HashMap<Long, DiscordUser> userMap = Maps.newHashMap();
  private final Logger logger = Main.getLogger("UserService");
  private final HashMap<Long, Thread> threads = Maps.newHashMap();
  private UserTable table;
  private long lastIntervalLog = 0;

  public UserManagementService(Hook hook) {
    this.hook = hook;
  }

  public void init() {
    logger.info("User service starting up...");
    this.table = hook.getDatabase().getUsers();

    net.dv8tion.jda.core.entities.User self = hook.getJda().getSelfUser();

    Main.getExecutor().scheduleAtFixedRate(() -> {
      Instant start = Instant.now();
      logger.info("Beginning user refresh.");

      logger.info("Refreshing guilds...");
      hook.refreshGuilds();
      logger.info("Finished guild refresh");

      logger.info("Gathering user lists...");
      HashMap<net.dv8tion.jda.core.entities.User, Long> idsByUser = Maps.newHashMap();
      try {
        for (Member member : hook.getMainGuild().getMembers()) {
          net.dv8tion.jda.core.entities.User user = member.getUser();
          idsByUser.put(user, user.getIdLong());
        }
        for (Member member : hook.getTmGuild().getMembers()) {
          net.dv8tion.jda.core.entities.User user = member.getUser();
          idsByUser.put(user, user.getIdLong());
        }
      } catch (Exception e) {
        hook.sendStatusException("Failed to gather user lists.", e);
        return;
      }

      this.userMap.keySet().forEach(l -> {
        if (!idsByUser.containsValue(l)) {
          this.userMap.remove(l);
        }
      });

      Map<net.dv8tion.jda.core.entities.User, Long> idsFiltered =
          idsByUser.entrySet().stream().filter(e -> !threads.containsKey(e.getValue()))
              .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

      logger.info("Refreshing " + idsFiltered.size() + " users.");
      int total = idsFiltered.size();
      AtomicInteger place = new AtomicInteger();
      idsFiltered.entrySet().forEach(u -> {
        try {
          net.dv8tion.jda.core.entities.User user = u.getKey();
          if (user.equals(self)) {
            return;
          }
          handleUser(user, false);
          incrementLog("Progress: " + place.get() + "/" + total + " users handled.");
          place.incrementAndGet();
        } catch (Exception e) {
          if (e instanceof ErrorResponseException && e
              .getCause() instanceof SocketTimeoutException) {
            return;
          }
          hook.sendStatusException("Failed to refresh user", e);
        }
      });
      Instant end = Instant.now();
      Duration duration = new Duration(start, end);
      logger.info("Finished user refresh in " + PERIOD_FORMAT.print(duration.toPeriod()));
    }, 0, 10, TimeUnit.MINUTES);
  }

  private void incrementLog(String message) {
    long now = System.currentTimeMillis();

    if (now - this.lastIntervalLog > 30 * 1000) {
      logger.info(message);
      this.lastIntervalLog = now;
    }
  }

  public void handleUser(net.dv8tion.jda.core.entities.User u, boolean ignore) {
    handleUser(u, u.getMutualGuilds().contains(hook.getMainGuild()),
        u.getMutualGuilds().contains(hook.getTmGuild()), ignore);
  }

  private void handleUser(net.dv8tion.jda.core.entities.User user, boolean inMain, boolean inTm,
      boolean ignore) {
    DiscordUser discordUser;

    if (userMap.containsKey(user.getIdLong())) {
      discordUser = userMap.get(user.getIdLong());
    } else {
      discordUser = new DiscordUser(user);
      userMap.put(user.getIdLong(), discordUser);
    }

    Guild main = hook.getMainGuild();
    Guild tm = hook.getTmGuild();

    Optional<net.avicus.magma.database.model.impl.User> dbUser = table
        .findByDiscord(user.getIdLong());
    if (dbUser.isPresent()) {
      discordUser.setUser(dbUser.get());
      discordUser.loadRanks();
      boolean banned = BanListener.isBanned(dbUser.get());
      if (inMain) {
        if (banned) {
          main.getController().kick(user.getId()).complete();
        } else {
          discordUser.updateRoles(main);
        }
      }
      if (inTm) {
        if (banned) {
          tm.getController().kick(user.getId()).complete();
        } else {
          discordUser.updateRoles(tm);
        }
      }
    } else {
      try {
        Member mm = null;
        Member tmm = null;
        if (inMain) {
          mm = main.getMember(user);
        }
        if (inTm) {
          tmm = tm.getMember(user);
        }

        final Member mainMember = mm;
        final Member tmMember = tmm;

        GuildController mainController = main.getController();
        GuildController tmController = tm.getController();
        RoleManagementService mainManagementService = hook.getRoleManager(main);

        // Registration token
        byte[] bytesOfMessage = discordUser.getDiscordUser().getId().getBytes("UTF-8");

        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] theDigest = md.digest(bytesOfMessage);
        theDigest = Base64.getEncoder().encode(theDigest);
        String token = new String(theDigest);
        hook.getRedis()
            .set("discord-reg-a." + token, Long.toString(discordUser.getDiscordUser().getIdLong()));

        // Remove user from all DB roles
        try {
          if (inTm && tmMember != null) {
            tmController.removeRolesFromMember(tmMember, tm.getRoles().stream()
                .filter(r -> PermissionUtil.canInteract(tm.getSelfMember(), r))
                .collect(Collectors.toSet())).submit();
          }
          if (inMain && mainMember != null) {
            mainController.removeRolesFromMember(mainMember,
                mainManagementService.getRankRelations().values().stream()
                    .map(HookRole::getRole)
                    .collect(Collectors.toList())).submit();
          }
        } catch (Exception e) {
          hook.sendStatusException("Failed to remove roles from user!", e);
          threads.remove(user.getIdLong());
          return;
        }

        if (discordUser.shouldMessage() || ignore) {
          // Send messages
          if (discordUser.message("**OH NO!** You are not linked to an Avicus user!")) {
            discordUser.message(
                "You will not get any ranks from Avicus and cannot talk in text and/or voice channels.");
            discordUser.message(
                "Visit " + NetworkIdentification.URL + "/users/discord-auth?token=" + token
                    + " to register.");
            discordUser.message(
                "Make sure you are logged in on the account that you wish to receive ranks for!");
          }
        }

        if (!threads.containsKey(user.getIdLong())) {
          Thread thread = new Thread(() -> {
            try {
              final CountDownLatch latch = new CountDownLatch(1);
              // Check every 45 seconds to see if the user has registered
              ScheduledFuture redisTask = Main.getExecutor().scheduleAtFixedRate(() -> {
                try {
                  // Kicked
                  if (!(discordUser.getDiscordUser().getMutualGuilds().contains(main) || discordUser
                      .getDiscordUser().getMutualGuilds().contains(tm))) {
                    latch.countDown();
                    return;
                  }

                  String res = hook.getRedis().get("discord-reg-v." + token);

                  if (res != null) {
                    hook.getRedis().del("discord-reg-v." + token);

                    User found = hook.getDatabase().getUsers().findById(Integer.valueOf(res)).get();

                    // Inform client
                    discordUser.message(
                        "You have successfully registered your client with " + found.getName());

                    logger.info(
                        discordUser.getDiscordUser().getName() + "has successfully registered!");

                    discordUser.setUser(found);
                    discordUser.loadRanks();
                    if (inTm) {
                      discordUser.updateRoles(tm);
                    }
                    if (inMain) {
                      discordUser.updateRoles(main);
                    }

                    // Cancel repetition
                    latch.countDown();
                  }
                } catch (Exception e) {
                  hook.sendStatusException("Registration error", e);
                }
              }, 0, 45, TimeUnit.SECONDS);
              latch.await();
              redisTask.cancel(true);
              threads.remove(user.getIdLong());
            } catch (InterruptedException e) {
              hook.sendStatusException("Registration error", e);
              discordUser.message("Something went wrong, message a developer! :(");
              threads.remove(user.getIdLong());
            }
          });
          threads.put(user.getIdLong(), thread);
          thread.start();
        }
        Main.getExecutor().schedule(() -> threads.remove(user.getIdLong()), 12, TimeUnit.HOURS);
      } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
        hook.sendStatusException("Failed to generate token", e);
        threads.remove(user.getIdLong());
      }
    }
  }

  public Optional<User> getUser(Long l) {
    DiscordUser found = this.userMap.get(l);
    return found == null ? Optional.empty() : Optional.ofNullable(found.getUser());
  }

  public Optional<DiscordUser> getUser(User user) {
    return this.userMap.values().stream()
        .filter(d -> d.getUser() != null && d.getUser().getId() == user.getId()).findFirst();
  }
}
