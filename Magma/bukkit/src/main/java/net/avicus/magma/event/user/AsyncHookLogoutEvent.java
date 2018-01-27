package net.avicus.magma.event.user;

import lombok.Getter;
import net.avicus.magma.database.model.impl.User;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called after a player logs out, asynchronously.
 */
public class AsyncHookLogoutEvent extends Event {

  private static final HandlerList handlers = new HandlerList();
  @Getter
  private final User user;

  public AsyncHookLogoutEvent(User user) {
    this.user = user;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}
