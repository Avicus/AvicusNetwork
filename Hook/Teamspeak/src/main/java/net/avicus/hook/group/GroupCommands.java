package net.avicus.hook.group;

import com.github.theholywaffle.teamspeak3.api.CommandFuture;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerGroup;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import net.avicus.hook.Hook;
import net.avicus.hook.Main;
import net.avicus.hook.wrapper.HookClient;
import net.avicus.magma.database.Database;

public class GroupCommands {

  private static Database database = Main.getHook().getDatabase();
  private static Hook hook = Main.getHook();

  // args - user groupId duration
  public static void assignTempRank(HookClient sender, List<String> arguments) throws Exception {
    if (!sender.hasPermission("ranks.temp")) {
      throw new RuntimeException("You do not have permission to execute this command!");
    }

    if (!(arguments.size() == 3)) {
      throw new RuntimeException(
          "Invalid number of arguments! Syntax: [user] [groupID] [duration]");
    }

    int id = Integer.valueOf(arguments.get(1));

    int seconds = Hook.getPeriodFormatter().parsePeriod(arguments.get(2)).toStandardSeconds()
        .getSeconds();

    Main.getExecutor().execute(() -> {
      CommandFuture<List<Client>> targetFuture = hook.getApiAsync()
          .getClientsByName(arguments.get(0));
      targetFuture.onFailure((e) -> sender.message(e.getMessage()));
      targetFuture.onSuccess((client) -> {
        if (client.isEmpty()) {
          sender.message("Client not found!");
          return;
        }

        CommandFuture<List<ServerGroup>> groupFuture = hook.getApiAsync().getServerGroups();
        groupFuture.onSuccess((l) -> {
          Optional<ServerGroup> group = l.stream().filter((g) -> g.getId() == id).findFirst();
          if (!group.isPresent()) {
            sender.message("Group not found matching ID: + " + id);
            return;
          }

          ServerGroup assign = group.get();

          if (!sender.hasPermission("ranks.assign." + id) && !sender
              .hasPermission("ranks.assign.*")) {
            sender.message("You do not have permission to execute this command!");
            return;
          }

          hook.getApi().addClientToServerGroup(assign.getId(), client.get(0).getDatabaseId());

          Main.getExecutor().schedule(() -> {
            hook.getApi()
                .removeClientFromServerGroup(assign.getId(), client.get(0).getDatabaseId());
          }, seconds, TimeUnit.SECONDS);
        });
      });
    });
  }
}
