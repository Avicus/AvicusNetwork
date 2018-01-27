package net.avicus.atlas.module.shop;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Event called when a player earns a point in a match.
 */
public class PlayerEarnPointEvent extends PlayerEvent {

  /**
   * Event handlers.
   */
  private static final HandlerList handlers = new HandlerList();

  /**
   * ID of the action that the player performed.
   */
  @Getter
  private final String action;

  /**
   * Constructor.
   *
   * @param who Player who performed the event.
   * @param action ID of the action that the player performed.
   */
  public PlayerEarnPointEvent(Player who, String action) {
    super(who);
    this.action = action;
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
