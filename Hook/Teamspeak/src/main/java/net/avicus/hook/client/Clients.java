package net.avicus.hook.client;

import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerGroup;
import com.google.common.base.Strings;
import com.lambdaworks.redis.RedisCommandTimeoutException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import lombok.Getter;
import net.avicus.hook.Hook;
import net.avicus.hook.HookConfig;
import net.avicus.hook.Main;
import net.avicus.hook.group.Groups;
import net.avicus.hook.wrapper.HookClient;
import net.avicus.magma.database.model.impl.TeamSpeakUser;
import net.avicus.magma.database.model.impl.User;

public class Clients {

  // Mapping of TS client IDs to HookClient's
  @Getter
  private static final HashMap<Integer, HookClient> clients = new HashMap<>();
  // Mapping of client ids to scheduled tasks that will kick them.
  private static final HashMap<Integer, ScheduledFuture> kickSchedules = new HashMap<>();
  private static Hook hook = Main.getHook();
  private static Logger logger;

  public static void init() {
    if (!HookConfig.Clients.isEnabled()) {
      return;
    }

    logger = Main.getLogger("Clients");

    logger.info(Strings.repeat("-", 30));
    logger.info("Handling users already online...");
    // Handle already online clients
    hook.getApi().getClients().forEach(Clients::handleLogin);
    logger.info(Strings.repeat("-", 30));
    logger.info("Done");

    hook.getApiAsync().registerEvent(TS3EventType.SERVER);
    hook.getApiAsync().addTS3Listeners(new TS3EventAdapter() {
      @Override
      public void onClientJoin(ClientJoinEvent e) {
        handleLogin(hook.getApi().getClientInfo(e.getClientId()));
      }

      @Override
      public void onClientLeave(ClientLeaveEvent e) {
        handleLogout(e);
      }
    });
  }

  private static void handleLogin(Client client) {
    int id = client.getId();

    logger.info("Login: " + id);

    for (String s : HookConfig.Messages.getWelcomeText()) {
      messageClient(id, s);
    }

    // Check if registered.
    CompletableFuture.supplyAsync(() -> {
      try {
        Optional<TeamSpeakUser> registered = hook.getDatabase().getTeamSpeakUsers()
            .findByClient(client.getDatabaseId());
        if (registered.isPresent()) {
          logger.info("User authenticated: " + client.getDatabaseId());
          for (String s : HookConfig.Messages.getRegisteredText()) {
            messageClient(id, s);
          }

          clients
              .put(id, new HookClient(client.getUniqueIdentifier(), registered.get().getUserId()));
        } else {
          logger.info("User is not authenticated: " + client.getDatabaseId());
          // Kick client if not registered after delay.
          ScheduledFuture future = Main.getExecutor().schedule(() -> {
            logger.info("Kicking un-authenticated client: " + client.getDatabaseId());
            hook.getApi().kickClientFromServer(HookConfig.Messages.getRegisteredKick(), id);
            kickSchedules.remove(id);
          }, HookConfig.Clients.getUnregisteredDelay(), TimeUnit.SECONDS);
          kickSchedules.put(id, future);

          // Remove client from all groups related to DB until they register.
          for (ServerGroup group : hook.getApi()
              .getServerGroupsByClientId(client.getDatabaseId())) {
            // This means a user has a group on TS that does not match any DB ranks, we ignore these.
            if (!Groups.getRankFromServerGroup(group.getId()).isPresent()) {
              continue;
            }

            logger.info(
                "Removing client from group: " + client.getDatabaseId() + " - " + group.getName());
            hook.getApi().removeClientFromServerGroup(group.getId(), client.getDatabaseId());
          }

          String regAuth = UUID.randomUUID().toString().substring(7);

          // Set redis value for bukkit
          hook.getRedis().set("ts-reg-a." + regAuth, client.getNickname());

          for (String s : HookConfig.Messages.getRegisterText()) {
            messageClient(id, MessageFormat.format(s, regAuth));
          }

          try {
            final CountDownLatch latch = new CountDownLatch(1);
            // Check every 5 seconds to see if the client has registered
            ScheduledFuture redisTask = Main.getExecutor().scheduleAtFixedRate(() -> {
              try {
                // User has left
                if (!kickSchedules.containsKey(id)) {
                  latch.countDown();
                  return;
                }

                String res = hook.getRedis().get("ts-reg-v." + regAuth);

                if (res != null) {
                  logger.info("Client Registered: " + client.getDatabaseId());

                  if (kickSchedules.containsKey(id)) {
                    kickSchedules.get(id).cancel(true);
                    kickSchedules.remove(id);
                  }

                  hook.getRedis().del("ts-reg-v." + regAuth);

                  User user = hook.getDatabase().getUsers().findById(Integer.valueOf(res)).get();

                  // Inform client
                  messageClient(id,
                      "You have successfully registered your client with " + user.getName());

                  for (String s : HookConfig.Messages.getRegisteredText()) {
                    messageClient(id, s);
                  }

                  logger.info("Inserting client into DB... " + client.getDatabaseId());
                  // Insert into DB
                  hook.getDatabase().getTeamSpeakUsers()
                      .insert(new TeamSpeakUser(user.getId(), client.getDatabaseId())).execute();
                  logger.info("Done " + client.getDatabaseId());

                  clients.put(id, new HookClient(client.getUniqueIdentifier(), user.getId()));

                  // Cancel repetition
                  latch.countDown();
                }
              } catch (Exception e) {
                e.printStackTrace();
                messageClient(id, "Error: (Please report this) " + e.getMessage());
                hook.getApi().kickClientFromServer("Please try logging in again!", id);
              }
            }, 0, 5, TimeUnit.SECONDS);
            latch.await();
            redisTask.cancel(true);
          } catch (InterruptedException e) {
            e.printStackTrace();
            messageClient(id, "Error: (Please report this) " + e.getMessage());
            hook.getApi().kickClientFromServer("Please try logging in again!", id);
          }
        }
      } catch (Exception e) {
        if (e instanceof RedisCommandTimeoutException) {
          hook.getRedis().reset();
        } else {
          e.printStackTrace();
          messageClient(id, "Error: (Please report this) " + e.getMessage());
        }
        hook.getApi().kickClientFromServer("Please try logging in again!", id);
      }

      return true;
    }, Main.getExecutor());
  }

  private static void handleLogout(ClientLeaveEvent event) {
    logger.info("Logout: " + event.getClientId());
    clients.remove(event.getClientId());

    if (kickSchedules.containsKey(event.getClientId())) {
      kickSchedules.get(event.getClientId()).cancel(true);
      kickSchedules.remove(event.getClientId());
    }
  }

  public static void messageClient(int client, String message) {
    if (HookConfig.Messages.isEnabled()) {
      logger.info("Message Sent to " + client + " -> " + message);
      hook.getApiAsync().sendPrivateMessage(client, message);
    }
  }

  public static HookClient getClientByUid(String uid) {
    for (HookClient client : getClients().values()) {
      if (client.getClientUid().equals(uid)) {
        return client;
      }
    }
    return null;
  }

  public static void messageClient(String client, String message) {
    if (HookConfig.Messages.isEnabled()) {
      logger.info("Message Sent to " + client + " -> " + message);
      hook.getApiAsync().sendPrivateMessage(hook.getApi().getClientByUId(client).getId(), message);
    }
  }
}
