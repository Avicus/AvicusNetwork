package net.avicus.magma.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Simple event used to check if other plugins should allow something to happen.
 * If the event is not canceled, the action will take place.
 */
public class CheckPrerequisitesEvent extends Event implements Cancellable {

  private static final HandlerList handlers = new HandlerList();
  @Getter
  private final String actionId;
  @Getter
  private final Object[] data;
  @Getter
  @Setter
  private boolean cancelled;

  public CheckPrerequisitesEvent(String actionId, Object... data) {
    this.actionId = actionId;
    this.data = data;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}
