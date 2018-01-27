package net.avicus.atlas.event;

import lombok.Getter;
import net.avicus.atlas.map.rotation.Rotation;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * An event that is called when the {@link Rotation} has reached its end.
 */
public class RotationEndEvent extends Event {

  /**
   * Event handlers.
   */
  private static final HandlerList handlers = new HandlerList();
  /**
   * Rotation that is ending.
   */
  @Getter
  private final Rotation rotation;

  /**
   * Constructor.
   *
   * @param rotation rotation that is ending
   */
  public RotationEndEvent(Rotation rotation) {
    this.rotation = rotation;
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
