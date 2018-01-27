package net.avicus.magma.alerts;

import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.event.user.AsyncHookLogoutEvent;
import net.avicus.magma.network.user.Users;
import net.avicus.magma.util.MagmaTask;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class AlertsListener implements Listener {

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    MagmaTask.of(() -> {
      User user = Users.user(event.getPlayer());
      Alerts.reload(user);
      if (Alerts.get(user, true).size() > 0) {
        Alerts.notify(event.getPlayer());
      }
    }).nowAsync();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onAsyncLogout(AsyncHookLogoutEvent event) {
    Alerts.unload(event.getUser());
  }
}
