package net.avicus.hook.wrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.avicus.hook.Main;

@FunctionalInterface
public interface ConfirmableTeamSpeakCommand extends TeamSpeakCommand {

  List<Integer> pending = new ArrayList<>();
  HashMap<Integer, ScheduledFuture> unPendTasks = new HashMap<>();

  default boolean doPendingChecks(HookClient sender) {
    int add = sender.getClient().getId();

    if (pending.contains(add)) {
      pending.remove((Integer) add);
      unPendTasks.get(add).cancel(true);
      unPendTasks.remove(add);
      return true;
    }

    sender.message("Please run the command again to confirm. (Expires in 30 seconds)");
    pending.add(add);

    unPendTasks.put(add, Main.getExecutor().schedule(() -> {
      if (pending.contains(add)) {
        sender.message("Command timed out!");
        pending.remove((Integer) add);
        unPendTasks.remove(add);
      }
    }, 30, TimeUnit.SECONDS));

    return false;
  }
}
