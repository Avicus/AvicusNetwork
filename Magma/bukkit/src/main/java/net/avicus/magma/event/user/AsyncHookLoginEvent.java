package net.avicus.magma.event.user;

import lombok.Getter;
import lombok.Setter;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.magma.database.model.impl.User;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

/**
 * Called while a user is joining the server, asynchronously.
 */
public class AsyncHookLoginEvent extends Event implements Cancellable {

  private static final HandlerList handlers = new HandlerList();
  @Getter
  private final AsyncPlayerPreLoginEvent loginEvent;
  @Getter
  private final User user;
  @Getter
  @Setter
  Localizable kickMessage;
  @Getter
  private boolean newUser;
  @Getter
  @Setter
  private boolean cancelled;

  public AsyncHookLoginEvent(AsyncPlayerPreLoginEvent loginEvent, User user, boolean newUser,
      Localizable kickMessage) {
    this.loginEvent = loginEvent;
    this.user = user;
    this.newUser = newUser;
    this.kickMessage = kickMessage;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}
