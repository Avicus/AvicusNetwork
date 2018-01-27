package net.avicus.magma.channel.distributed;

import java.util.Map;
import net.avicus.magma.Magma;
import net.avicus.magma.channel.Channel;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.database.model.impl.User;
import net.md_5.bungee.api.chat.BaseComponent;

/**
 * A distributed channel.
 */
public interface DistributedChannel extends Channel {

  /**
   * Invoked when a distributed message is being sent to this channel.
   *
   * @param server the server
   * @param source the source
   * @param components the message
   * @param context the context
   */
  void distributedRead(Server server, User source, BaseComponent[] components,
      Map<String, String> context);

  /**
   * Send a distributed message is being sent to this channel.
   *
   * @param server the server
   * @param source the source
   * @param components the message
   * @param context the context
   */
  default void distributedWrite(Server server, User source, BaseComponent[] components,
      Map<String, String> context) {
    Magma.get().getRedis()
        .publish(new DistributedChannelRedisMessage(this, server, source, components, context));
  }
}
