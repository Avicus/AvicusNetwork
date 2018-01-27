package net.avicus.hook.listener;

import net.avicus.atlas.event.player.PlayerSpawnBeginEvent;
import net.avicus.hook.gadgets.backpack.BackpackMenu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AtlasListener implements Listener {

  @EventHandler
  public void onPlayerSpawn(PlayerSpawnBeginEvent event) {
    if (event.getGroup().isObserving()) {
      event.getPlayer().getInventory()
          .setItem(7, BackpackMenu.createBackpackOpener(event.getPlayer()));
    }
  }
}
