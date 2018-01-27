package net.avicus.magma.util;

import com.google.gson.JsonObject;
import net.avicus.magma.Magma;
import net.avicus.magma.redis.RedisHandler;
import net.avicus.magma.redis.RedisMessage;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Parses json asynchronously, handles messages on main thread.
 */
public abstract class AsyncRedisHandler<T extends RedisMessage> implements RedisHandler {

  private final String[] channels;

  protected AsyncRedisHandler(String[] channels) {
    this.channels = channels;
  }

  public final void handle(JsonObject json) {
    if (!Magma.get().isEnabled()) {
      return;
    }

    new BukkitRunnable() {
      @Override
      public void run() {
        T message = readAsync(json);
        if (message != null) {
          new BukkitRunnable() {
            @Override
            public void run() {
              handle(message);
            }
          }.runTask(Magma.get());
        }
      }
    }.runTaskAsynchronously(Magma.get());
  }

  /**
   * Asynchronously parses json.
   */
  public abstract T readAsync(JsonObject json);

  /**
   * Synchronously handles message.
   */
  public abstract void handle(T message);

  @Override
  public String[] channels() {
    return this.channels;
  }
}
