package net.avicus.atlas.event.objective;

import lombok.Getter;
import net.avicus.atlas.module.objectives.Objective;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * An event that is the superclass to all events that are related to objectives being touched.
 */
public class ObjectiveTouchEvent extends ObjectiveEvent {

  /**
   * Event handlers.
   */
  private static final HandlerList handlers = new HandlerList();
  /**
   * Player who touched the objective.
   */
  @Getter
  private final Player player;

  /**
   * Constructor.
   *
   * @param objective objective that was touched
   * @param player player who touched the objective
   */
  public ObjectiveTouchEvent(Objective objective, Player player) {
    super(objective);
    this.player = player;
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
