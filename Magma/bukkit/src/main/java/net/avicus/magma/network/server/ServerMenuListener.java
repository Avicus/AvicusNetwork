package net.avicus.magma.network.server;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ServerMenuListener implements Listener {

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    if (event.getAction() != Action.RIGHT_CLICK_AIR
        && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    if (!Servers.isMenuOpener(event.getItem())) {
      return;
    }

    event.setCancelled(true);

    Player player = event.getPlayer();

    ServerMenu menu = ServerMenu.fromConfig(player);
    menu.open();
  }
}
