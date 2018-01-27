package net.avicus.hook.utils;

import java.util.function.Supplier;
import net.avicus.compendium.utils.Task;
import net.avicus.hook.Hook;
import org.bukkit.plugin.Plugin;

public class HookTask extends Task {

  private final Runnable runnable;

  public HookTask(Runnable runnable) {
    this.runnable = runnable;
  }

  @Deprecated
  public HookTask() {
    this.runnable = null;
  }

  public static HookTask of(Runnable runnable) {
    return new HookTask(runnable);
  }

  public static HookTask of(Supplier<Runnable> runnable) {
    return new HookTask(runnable.get());
  }

  @Override
  public Plugin getPlugin() {
    return Hook.plugin();
  }

  public void run() throws Exception {
    if (this.runnable != null) {
      this.runnable.run();
    } else {
      throw new RuntimeException("HookTask not implemented!");
    }
  }
}