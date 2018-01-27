package net.avicus.magma.channel;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import org.bukkit.entity.Player;

/**
 * A channel manager.
 */
public class ChannelManager {

  /**
   * A map of registered channels.
   */
  private final Map<String, Channel> registeredChannels = Maps.newHashMap();
  /**
   * A map of player ids to their active channel.
   */
  private final Map<UUID, Channel> activeChannels = Maps.newHashMap();

  /**
   * Register a channel.
   *
   * @param channel the channel
   */
  public void register(Channel channel) {
    this.registeredChannels.put(channel.getId(), channel);
  }

  /**
   * Unregister a channel.
   *
   * @param channel the channel
   */
  public void unregister(Channel channel) {
    this.registeredChannels.remove(channel.getId());
  }

  /**
   * Gets a registered channel by its id.
   *
   * @param id the channel id
   * @return the channel
   */
  @Nullable
  public Channel getChannel(String id) {
    return this.registeredChannels.get(id);
  }

  /**
   * Gets a registered channel by its id.
   *
   * @param id the channel id
   * @param channelClass the channel class
   * @return the channel
   * @throws IllegalStateException if the channel is not an instance of the provided class
   */
  @Nullable
  public <C extends Channel> C getChannel(String id, Class<C> channelClass) {
    @Nullable Channel channel = this.registeredChannels.get(id);
    if (channel != null) {
      checkState(channelClass.isInstance(channel), "Registered channel %s is not an instance of %s",
          id, channelClass.getName());
    }

    return (C) this.registeredChannels.get(id);
  }

  /**
   * Gets the active channel for the specified player.
   *
   * @param player the player
   * @return the active channel
   */
  @Nullable
  public Channel getActiveChannel(Player player) {
    return this.getActiveChannel(player.getUniqueId());
  }

  /**
   * Gets the active channel for the specified player id.
   *
   * @param uniqueId the player id
   * @return the active channel
   */
  @Nullable
  public Channel getActiveChannel(UUID uniqueId) {
    return this.activeChannels.get(uniqueId);
  }

  /**
   * Sets the active channel for the specified player.
   *
   * @param player the player
   * @param channel the channel
   */
  public void setActiveChannel(Player player, @Nullable Channel channel) {
    this.setActiveChannel(player.getUniqueId(), channel);
  }

  /**
   * Sets the active channel for the specified player id.
   *
   * @param uniqueId the player id
   * @param channel the channel
   */
  public void setActiveChannel(UUID uniqueId, @Nullable Channel channel) {
    this.activeChannels.put(uniqueId, channel);
  }
}
