package net.avicus.atlas.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * An event that is fired one tick after a player logs into the server.
 */
public class PlayerJoinDelayedEvent extends PlayerEvent {

  /**
   * Event handlers.
   */
  private static final HandlerList handlers = new HandlerList();

  /**
   * Constructor.
   *
   * @param player player that is joining
   */
  public PlayerJoinDelayedEvent(Player player) {
    super(player);
  }

  /**
   * Get the handlers of the event.
   *
   * @return the handlers of the event
   */
  public static HandlerList getHandlerList() {
    return handlers;
  }

  /**
   * Get the handlers of the event.
   *
   * @return the handlers of the event
   */
  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}
