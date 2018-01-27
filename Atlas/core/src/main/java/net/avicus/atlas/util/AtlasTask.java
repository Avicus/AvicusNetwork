package net.avicus.atlas.util;

import java.util.function.Supplier;
import net.avicus.atlas.Atlas;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class AtlasTask extends BukkitRunnable {

  private final Runnable runnable;
  private BukkitTask task;

  public AtlasTask() {
    this.runnable = null;
  }

  public AtlasTask(Runnable runnable) {
    this.runnable = runnable;
  }

  public static AtlasTask of(Runnable runnable) {
    return new AtlasTask(runnable);
  }

  public static AtlasTask of(Supplier<Runnable> runnable) {
    return new AtlasTask(runnable.get());
  }

  @Override
  public void run() {
    if (this.runnable != null) {
      this.runnable.run();
    } else {
      throw new RuntimeException("AtlasTask not implemented!");
    }
  }

  public AtlasTask now() {
    this.createBukkitRunnable().runTask(Atlas.get());
    return this;
  }

  public AtlasTask nowAsync() {
    this.task = this.createBukkitRunnable().runTaskAsynchronously(Atlas.get());
    return this;
  }

  public AtlasTask later(int ticksDelay) {
    this.task = this.createBukkitRunnable().runTaskLater(Atlas.get(), ticksDelay);
    return this;
  }

  public AtlasTask laterAsync(int ticksDelay) {
    this.task = this.createBukkitRunnable().runTaskLaterAsynchronously(Atlas.get(), ticksDelay);
    return this;
  }

  public AtlasTask repeat(int ticksDelay, int ticksInterval) {
    this.task = this.createBukkitRunnable().runTaskTimer(Atlas.get(), ticksDelay, ticksInterval);
    return this;
  }

  public AtlasTask repeatAsync(int ticksDelay, int ticksInterval) {
    this.task = this.createBukkitRunnable()
        .runTaskTimerAsynchronously(Atlas.get(), ticksDelay, ticksInterval);
    return this;
  }

  public boolean cancel0() {
    if (this.task == null) {
      return false;
    }
    this.task.cancel();
    this.task = null;
    return true;
  }

  private BukkitRunnable createBukkitRunnable() {
    if (this.task != null) {
      throw new IllegalStateException(
          "Cannot restart AtlasTask (existing: " + this.task.getTaskId() + ")");
    }

    return this;
  }
}
