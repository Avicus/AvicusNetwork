package net.avicus.hook.friends;

import net.avicus.magma.event.user.AsyncHookLoginEvent;
import net.avicus.magma.event.user.AsyncHookLogoutEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class FriendsListener implements Listener {

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onAsyncHookLogin(AsyncHookLoginEvent event) {
    Friends.reload(event.getUser());
    Friends.join(event.getUser());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onAsyncHookLogin(AsyncHookLogoutEvent event) {
    Friends.unload(event.getUser());
    Friends.leave(event.getUser());
  }
}
