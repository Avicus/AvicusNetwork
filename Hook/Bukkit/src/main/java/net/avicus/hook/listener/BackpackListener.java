package net.avicus.hook.listener;

import static net.avicus.hook.gadgets.backpack.BackpackMenu.isBackpackOpener;

import net.avicus.hook.gadgets.backpack.BackpackMenu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class BackpackListener implements Listener {

  @EventHandler(priority = EventPriority.LOWEST)
  public void onBackpackOpen(PlayerInteractEvent event) {
    if (event.getAction() != Action.RIGHT_CLICK_BLOCK
        && event.getAction() != Action.RIGHT_CLICK_AIR) {
      return;
    }

    if (!isBackpackOpener(event.getItem())) {
      return;
    }

    event.setCancelled(true);

    BackpackMenu menu = new BackpackMenu(event.getPlayer());
    menu.open();
  }
}
