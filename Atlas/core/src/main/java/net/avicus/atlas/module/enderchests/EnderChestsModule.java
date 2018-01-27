package net.avicus.atlas.module.enderchests;

import java.util.Optional;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.Module;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.LocationVariable;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.util.EnderChestStore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

/**
 * This module is responsible for storing ender chests across matches of the same map.
 */
@ToString(exclude = "match")
public class EnderChestsModule implements Module {

  private final Match match;
  private final boolean exclusive;
  private final Optional<Check> openCheck;

  public EnderChestsModule(Match match, boolean exclusive, Optional<Check> openCheck) {
    this.match = match;
    this.exclusive = exclusive;
    this.openCheck = openCheck;
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onClick(PlayerInteractEvent event) {
    Player p = event.getPlayer();
    if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
      if (event.getClickedBlock().getType() == Material.ENDER_CHEST) {
        if (this.openCheck.isPresent()) {
          CheckContext context = new CheckContext(this.match);
          context.add(new PlayerVariable(event.getPlayer()));
          context.add(new LocationVariable(event.getClickedBlock().getLocation()));
          if (this.openCheck.get().test(context).fails()) {
            event.setCancelled(true);
            return;
          }
        }
        if (!exclusive) {
          event.setCancelled(true);
          Inventory inv = Bukkit.createInventory(p, InventoryType.ENDER_CHEST);
          EnderChestStore.getChest(p.getUniqueId()).forEach(inv::setItem);
          p.openInventory(inv);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onClose(InventoryCloseEvent event) {
    if (exclusive) {
      return;
    }

    HumanEntity p = event.getPlayer();
    if (!(p instanceof Player)) {
      return;
    }

    if (event.getInventory().getType() == InventoryType.ENDER_CHEST
        && event.getInventory().getContents().length != 0) {
      EnderChestStore.store(p.getUniqueId(), event.getInventory());
      p.getEnderChest().clear();
    }
  }
}
