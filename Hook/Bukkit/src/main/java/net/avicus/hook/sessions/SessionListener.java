package net.avicus.hook.sessions;

import net.avicus.hook.Hook;
import net.avicus.magma.event.user.AsyncHookLoginEvent;
import net.avicus.magma.event.user.AsyncHookLogoutEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class SessionListener implements Listener {

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onAsyncHookLogin(AsyncHookLoginEvent event) {
    String ip = event.getLoginEvent().getAddress().getHostAddress();
    Sessions.create(event.getUser(), ip, Hook.server());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onAsyncHookLogout(AsyncHookLogoutEvent event) {
    Sessions.end(event.getUser());
  }
}
