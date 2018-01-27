package net.avicus.magma.util;

import java.util.function.Supplier;
import net.avicus.compendium.utils.Task;
import net.avicus.magma.Magma;
import org.bukkit.plugin.Plugin;

public class MagmaTask extends Task {

  private final Runnable runnable;

  public MagmaTask(Runnable runnable) {
    this.runnable = runnable;
  }

  @Deprecated
  public MagmaTask() {
    this.runnable = null;
  }

  public static MagmaTask of(Runnable runnable) {
    return new MagmaTask(runnable);
  }

  public static MagmaTask of(Supplier<Runnable> runnable) {
    return new MagmaTask(runnable.get());
  }

  @Override
  public Plugin getPlugin() {
    return Magma.get();
  }

  public void run() throws Exception {
    if (this.runnable != null) {
      this.runnable.run();
    } else {
      throw new RuntimeException("HookTask not implemented!");
    }
  }
}