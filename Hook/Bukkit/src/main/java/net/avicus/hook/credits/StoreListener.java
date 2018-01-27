package net.avicus.hook.credits;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import net.avicus.hook.utils.HookTask;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class StoreListener implements Listener {

  private final Map<UUID, GadgetStore> storeCache = Maps.newConcurrentMap();

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    if (event.getAction() != Action.RIGHT_CLICK_AIR
        && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    if (!Credits.isGadgetStoreOpener(event.getItem())) {
      return;
    }

    GadgetStore store = storeCache.get(event.getPlayer().getUniqueId());
    if (store == null) {
      GadgetStore newStore = new GadgetStore(event.getPlayer());
      storeCache.put(event.getPlayer().getUniqueId(), newStore);
      newStore.open();
    } else {
      store.open();
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void onJoin(PlayerJoinEvent event) {
    HookTask.of(() -> {
      if (event.getPlayer().isOnline()) {
        storeCache.put(event.getPlayer().getUniqueId(), new GadgetStore(event.getPlayer()));
      }
    }).nowAsync();
  }
}
