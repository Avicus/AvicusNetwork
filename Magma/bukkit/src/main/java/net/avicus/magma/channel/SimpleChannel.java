package net.avicus.magma.channel;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A simple channel.
 */
public class SimpleChannel implements Channel {

  /**
   * The channel id.
   */
  protected final String id;
  /**
   * The permission required to send to this channel.
   */
  protected final String sendPermission;
  /**
   * The permission required to read messages sent to this channel.
   */
  protected final String readPermission;

  /**
   * Construct a new simple channel with no permissions required to send or read messages.
   *
   * @param id the channel id
   */
  public SimpleChannel(String id) {
    this(id, null, null);
  }

  /**
   * Construct a new simple channel with a single permission required to send or read messages.
   *
   * @param id the channel id
   * @param permission the permission required to send and read messages
   */
  public SimpleChannel(String id, String permission) {
    this(id, permission, permission);
  }

  /**
   * Construct a new simple channel with a permission required to send and read messages.
   *
   * @param id the channel id
   * @param sendPermission the permission required to send messages to this channel
   * @param readPermission the permission required to read messages sent to this channel
   */
  public SimpleChannel(String id, String sendPermission, String readPermission) {
    this.id = id;
    this.sendPermission = sendPermission;
    this.readPermission = readPermission;
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public String getSendPermission() {
    return this.sendPermission;
  }

  @Override
  public String getReadPermission() {
    return this.readPermission;
  }

  @Override
  public boolean send(CommandSender source, BaseComponent... components) {
    if (!this.canSend(source)) {
      return false;
    }

    this.send(components);
    return true;
  }

  /**
   * Send a message to all players with permission to read.
   *
   * @param components the message
   */
  protected final void send(BaseComponent... components) {
    final Server server = Bukkit.getServer();
    server.getOnlinePlayers().stream()
        .filter(this::canRead)
        .forEach(player -> {
          this.preSend(player);
          player.sendMessage(components);
        });
    server.getConsoleSender().sendMessage(components);
  }

  protected void preSend(final Player viewer) {
  }
}
