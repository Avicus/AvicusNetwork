package net.avicus.atlas.module.items;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.avicus.atlas.event.group.PlayerChangedGroupEvent;
import net.avicus.atlas.event.player.PlayerSpawnBeginEvent;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.modifiers.AnyCheck;
import net.avicus.atlas.module.checks.variable.LocationVariable;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.util.AtlasTask;
import net.avicus.grave.event.PlayerDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class KeepListener implements Listener {

  private final Match match;
  private final Check keepCheck;
  private final Map<Player, ItemStack[]> savedInventories;

  public KeepListener(Match match, Optional<Check> keepItems, Optional<Check> keepArmor) {
    this.match = match;
    if (keepItems.isPresent()) {
      if (keepArmor.isPresent()) {
        this.keepCheck = new AnyCheck(keepItems.get(), keepArmor.get());
      } else {
        this.keepCheck = keepItems.get();
      }
    } else {
      this.keepCheck = keepArmor.get();
    }
    this.savedInventories = new HashMap<>();
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onPlayerDeath(PlayerDeathEvent event) {
    Inventory inventory = event.getPlayer().getInventory();
    ItemStack[] save = new ItemStack[inventory.getSize()];

    CheckContext context = new CheckContext(this.match);
    context.add(new PlayerVariable(event.getPlayer()));
    context.add(new LocationVariable(event.getLocation()));

    for (int i = 0; i < inventory.getContents().length; i++) {
      ItemStack stack = inventory.getContents()[i];
      if (stack == null) {
        continue;
      }

      boolean keep = this.keepCheck.test(context.duplicate()).passes();

      if (keep) {
        event.getDrops().remove(stack);
        save[i] = stack;
      }
    }

    this.savedInventories.put(event.getPlayer(), save);
  }

  @EventHandler
  public void onPlayerSpawn(PlayerSpawnBeginEvent event) {
    ItemStack[] saved = this.savedInventories.get(event.getPlayer());

    if (saved == null) {
      return;
    }

    new AtlasTask() {
      @Override
      public void run() {
        Inventory inventory = event.getPlayer().getInventory();

        // copy existing inventory
        ItemStack[] spawnInventory = inventory.getContents().clone();

        // overwrite with saved
        inventory.setContents(saved);

        for (int i = 0; i < spawnInventory.length; i++) {
          ItemStack item = spawnInventory[i];
          if (item == null) {
            continue;
          }

          ItemStack savedItem = inventory.getItem(i);

          if (savedItem == null) {
            inventory.setItem(i, item);
          } else if (item.getType() == savedItem.getType() && item.getData() == savedItem
              .getData()) {
            int newAmount = item.getAmount() + savedItem.getAmount();
            if (newAmount > item.getMaxStackSize()) {
              inventory.addItem(item);
            } else {
              savedItem.setAmount(newAmount);
            }
          } else {
            inventory.addItem(item);
          }
        }

        event.getPlayer().updateInventory();
        savedInventories.remove(event.getPlayer());
      }
    }.now();
  }

  @EventHandler
  public void onPlayerChangeTeam(PlayerChangedGroupEvent event) {
    this.savedInventories.remove(event.getPlayer());
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    this.savedInventories.remove(event.getPlayer());
  }
}
