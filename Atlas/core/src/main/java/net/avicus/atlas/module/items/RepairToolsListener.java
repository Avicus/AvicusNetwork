package net.avicus.atlas.module.items;

import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.ItemVariable;
import net.avicus.atlas.module.checks.variable.LocationVariable;
import net.avicus.atlas.module.checks.variable.MaterialVariable;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.util.VersionUtil;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class RepairToolsListener implements Listener {

  private final Match match;
  private final Check repairDrops;

  public RepairToolsListener(Match match, Check repairDrops) {
    this.match = match;
    this.repairDrops = repairDrops;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerPickupItem(PlayerPickupItemEvent event) {
    if (match.getRequiredModule(GroupsModule.class).isObservingOrDead(event.getPlayer())) {
      return;
    }

    ItemStack drop = event.getItem().getItemStack();

    CheckContext context = new CheckContext(this.match);
    context.add(new PlayerVariable(event.getPlayer()));
    context.add(new ItemVariable(this.match, drop));
    context.add(new MaterialVariable(drop.getData()));
    context.add(new LocationVariable(event.getItem().getLocation()));

    if (this.repairDrops.test(context).fails()) {
      return;
    }

    ItemStack repair = null;

    for (ItemStack item : event.getPlayer().getInventory().getContents()) {
      if (isRepairable(drop, item)) {
        repair = item;

        // stop at this item if it is damaged
        if (repair.getDurability() > 0) {
          break;
        }
      }

    }

    if (repair != null) {
      // repair
      int remaining = drop.getType().getMaxDurability() - drop.getDurability() + 1;
      repair.setDurability((short) Math.max(repair.getDurability() - remaining, 0));

      String soundName = "ITEM_PICKUP";

      // 1.8-1.9 Support
      if (VersionUtil.isCombatUpdate()) {
        soundName = "ENTITY_ITEM_PICKUP";
        // 1.9 Version
        // event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.5F, 1.0F);
      }

      event.getPlayer()
          .playSound(event.getPlayer().getLocation(), Sound.valueOf(soundName), 0.5F, 1.0F);

      // remove and cancel pickup
      event.getItem().remove();
      event.setCancelled(true);
    }
  }

  private boolean isRepairable(ItemStack drop, ItemStack item) {
    return item != null && drop.getType() == item.getType() && drop.getEnchantments()
        .equals(item.getEnchantments());
  }
}
