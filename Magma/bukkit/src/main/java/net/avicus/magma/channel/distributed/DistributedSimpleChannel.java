package net.avicus.magma.channel.distributed;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.avicus.magma.Magma;
import net.avicus.magma.channel.SimpleChannel;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.network.user.Users;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;

/**
 * An abstract distributed simple template channel.
 */
public abstract class DistributedSimpleChannel extends SimpleChannel implements DistributedChannel {

  /**
   * Construct a new abstract distributed template channel with no permissions required to send or
   * read messages.
   *
   * @param id the channel id
   */
  protected DistributedSimpleChannel(String id) {
    super(id);
  }

  /**
   * Construct a new abstract distributed template channel with a single permission required to send
   * or read messages.
   *
   * @param id the channel id
   * @param permission the permission required to send and read messages
   */
  protected DistributedSimpleChannel(String id, String permission) {
    super(id, permission);
  }

  /**
   * Construct a new abstract distributed template channel with a permission required to send and
   * read messages.
   *
   * @param id the channel id
   * @param sendPermission the permission required to send messages to this channel
   * @param readPermission the permission required to read messages sent to this channel
   */
  protected DistributedSimpleChannel(String id, String sendPermission, String readPermission) {
    super(id, sendPermission, readPermission);
  }

  @Override
  public boolean send(CommandSender source, BaseComponent... components) {
    if (!this.canSend(source)) {
      return false;
    }

    this.dualSend(Magma.get().localServer(), Users.user(source), components, ImmutableMap.of());
    return true;
  }

  @Override
  public void distributedRead(Server server, User source, BaseComponent[] components,
      Map<String, String> context) {
    this.send(this.format(server, source, components, context));
  }

  /**
   * Send a local-only message.
   *
   * @param source the source
   * @param components the message
   */
  public void localSend(CommandSender source, BaseComponent... components) {
    this.send(
        this.format(Magma.get().localServer(), Users.user(source), components, ImmutableMap.of()));
  }

  /**
   * Send a simple local-only message.
   * <p>
   * <p>A simple message has no source displayed.</p>
   *
   * @param source the source
   * @param components the message
   */
  public void simpleLocalSend(CommandSender source, BaseComponent... components) {
    this.send(this.format(Magma.get().localServer(), Users.user(source), components,
        ImmutableMap.of("simple", "true")));
  }

  /**
   * Send a message to the local server, and to other servers consuming this channel.
   *
   * @param server the server
   * @param source the source
   * @param components the message
   * @param context the context
   */
  protected void dualSend(Server server, User source, BaseComponent[] components,
      Map<String, String> context) {
    this.send(this.format(server, source, components, context));
    this.distributedWrite(server, source, components, context);
  }

  /**
   * Format a message for sending.
   *
   * @param server the server
   * @param source the source
   * @param components the message
   * @param context the context
   * @return the formatted message
   */
  protected abstract BaseComponent format(Server server, User source, BaseComponent[] components,
      Map<String, String> context);
}
