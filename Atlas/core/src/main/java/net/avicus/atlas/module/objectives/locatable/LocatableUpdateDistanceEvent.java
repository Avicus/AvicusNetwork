package net.avicus.atlas.module.objectives.locatable;

import net.avicus.atlas.event.objective.ObjectiveStateChangeEvent;
import net.avicus.atlas.module.objectives.Objective;
import org.bukkit.event.HandlerList;

public class LocatableUpdateDistanceEvent extends ObjectiveStateChangeEvent {

  /**
   * Event handlers.
   */
  private static final HandlerList handlers = new HandlerList();

  public LocatableUpdateDistanceEvent(Objective objective) {
    super(objective);
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
