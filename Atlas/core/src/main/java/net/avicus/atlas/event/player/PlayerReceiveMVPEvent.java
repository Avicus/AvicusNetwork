package net.avicus.atlas.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Event called when a player receives MVP in a match.
 */
public class PlayerReceiveMVPEvent extends PlayerEvent {

  /**
   * Event handlers.
   */
  private static final HandlerList handlers = new HandlerList();

  public PlayerReceiveMVPEvent(Player who) {
    super(who);
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
