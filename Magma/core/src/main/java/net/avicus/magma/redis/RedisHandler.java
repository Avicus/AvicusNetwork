package net.avicus.magma.redis;

import com.google.gson.JsonObject;

public interface RedisHandler {

  /**
   * The channels that this handler actively uses.
   */
  String[] channels();

  /**
   * Check if a message being sent to a channel matches this handler.
   *
   * @param channel The channel.
   */
  default boolean matches(String channel) {
    for (String check : channels()) {
      if (check.equals(channel)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Handles a message being sent to a channel that this handler matched.
   */
  void handle(JsonObject json);
}
