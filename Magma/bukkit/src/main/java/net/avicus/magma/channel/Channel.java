package net.avicus.magma.channel;

import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * A channel.
 */
public interface Channel {

  /**
   * Gets the id of the channel.
   *
   * @return the id of the channel
   */
  String getId();

  /**
   * If the source can send messages to this channel.
   *
   * @param source the source
   * @return {@code true} if the source can send messages to this channel, {@code false} otherwise
   */
  default boolean canSend(CommandSender source) {
    @Nullable final String permission = this.getSendPermission();
    return permission == null || source.hasPermission(permission);
  }

  /**
   * Gets the permission required to send messages to this channel.
   *
   * @return the permission
   */
  @Nullable
  String getSendPermission();

  /**
   * If the viewer can read messages sent to this channel.
   *
   * @param viewer the viewer
   * @return {@code true} if the viewer can read messages sent to this channel, {@code false}
   * otherwise
   */
  default boolean canRead(CommandSender viewer) {
    @Nullable final String permission = this.getReadPermission();
    return permission == null || viewer.hasPermission(permission);
  }

  /**
   * Gets the permission required to read messages in this channel.
   *
   * @return the permission
   */
  @Nullable
  String getReadPermission();

  /**
   * Send a message to this channel.
   *
   * @param source the source
   * @param message the message
   * @return {@code true} if the message was sent, {@code false} otherwise
   */
  default boolean send(CommandSender source, String message) {
    return this.send(source, message, false);
  }

  /**
   * Send a message to this channel.
   *
   * @param source the source
   * @param message the message
   * @param translateLegacy if the {@code &amp;} code should be translated into the legacy code
   * @return {@code true} if the message was sent, {@code false} otherwise
   */
  default boolean send(CommandSender source, String message, boolean translateLegacy) {
    return this.send(source, TextComponent.fromLegacyText(
        translateLegacy ? ChatColor.translateAlternateColorCodes('&', message) : message));
  }

  /**
   * Send a message to this channel.
   *
   * @param source the source
   * @param message the message
   * @param translateLegacy if the {@code &amp;} code should be translated into the legacy code
   * @param keepFormatting if formatting should be kept in the message.
   * @return {@code true} if the message was sent, {@code false} otherwise
   */
  default boolean send(CommandSender source, String message, boolean translateLegacy,
      boolean keepFormatting) {
    if (keepFormatting) {
      message = message.replaceAll(Pattern.quote("&m"), "").replaceAll(Pattern.quote("&n"), "")
          .replaceAll(Pattern.quote("&l"), "").replaceAll(Pattern.quote("&k"), "")
          .replaceAll(Pattern.quote("&o"), "");
    }
    return this.send(source, message, translateLegacy);
  }

  /**
   * Send a message to this channel.
   *
   * @param source the source
   * @param component the message
   * @return {@code true} if the message was sent, {@code false} otherwise
   */
  default boolean send(CommandSender source, BaseComponent component) {
    return this.send(source, new BaseComponent[]{component});
  }

  /**
   * Send a message to this channel.
   *
   * @param source the source
   * @param components the message
   * @return {@code true} if the message was sent, {@code false} otherwise
   */
  boolean send(CommandSender source, BaseComponent... components);
}
