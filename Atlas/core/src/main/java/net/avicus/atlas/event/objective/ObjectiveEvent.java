package net.avicus.atlas.event.objective;

import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.module.objectives.Objective;
import org.bukkit.event.Event;

/**
 * An event that is the superclass to all events that are related to objectives.
 */
@ToString
public abstract class ObjectiveEvent extends Event {

  /**
   * The objective involved in the event.
   */
  @Getter
  private final Objective objective;

  /**
   * Constructor,
   *
   * @param objective objective involved in the event
   */
  protected ObjectiveEvent(Objective objective) {
    this.objective = objective;
  }
}
