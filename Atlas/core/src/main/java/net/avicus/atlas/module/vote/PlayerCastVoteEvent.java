package net.avicus.atlas.module.vote;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Event called when a player receives MVP in a match.
 */
public class PlayerCastVoteEvent extends PlayerEvent {

  /**
   * Event handlers.
   */
  private static final HandlerList handlers = new HandlerList();

  public PlayerCastVoteEvent(Player who) {
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
