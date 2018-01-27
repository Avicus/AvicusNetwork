package net.avicus.atlas.module.items;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.avicus.atlas.event.world.BlockChangeByPlayerEvent;
import net.avicus.atlas.event.world.BlockChangeEvent;
import net.avicus.atlas.event.world.EntityChangeEvent;
import net.avicus.atlas.event.world.EntityChangeEvent.Action;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.EntityVariable;
import net.avicus.atlas.module.checks.variable.ItemVariable;
import net.avicus.atlas.module.checks.variable.LocationVariable;
import net.avicus.atlas.module.checks.variable.MaterialVariable;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.grave.event.EntityDeathEvent;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Boat;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class RemoveDropsListener implements Listener {

  private final Match match;
  private final Check removeDrops;

  public RemoveDropsListener(Match match, Check removeDrops) {
    this.match = match;
    this.removeDrops = removeDrops;
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerDeath(EntityDeathEvent event) {
    Iterator<ItemStack> iterator = event.getDrops().iterator();
    CheckContext parent = new CheckContext(this.match);
    if (event.getEntity() instanceof Player) {
      parent.add(new PlayerVariable((Player) event.getEntity()));
    } else {
      parent.add(new EntityVariable(event.getEntity()));
    }
    parent.add(new LocationVariable(event.getEntity().getLocation()));

    while (iterator.hasNext()) {
      ItemStack item = iterator.next();

      CheckContext context = parent.duplicate();

      context.add(new MaterialVariable(item.getData()));
      context.add(new ItemVariable(this.match, item));

      boolean remove = this.removeDrops.test(context).passes();

      if (remove) {
        iterator.remove();
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerDropItem(PlayerDropItemEvent event) {
    if (this.match.getRequiredModule(GroupsModule.class).isObservingOrDead(event.getPlayer())) {
      return;
    }

    CheckContext context = new CheckContext(this.match);
    context.add(new PlayerVariable(event.getPlayer()));
    context.add(new LocationVariable(event.getPlayer().getLocation()));
    context.add(new MaterialVariable(event.getItemDrop().getItemStack().getData()));
    context.add(new ItemVariable(this.match, event.getItemDrop().getItemStack()));

    boolean remove = this.removeDrops.test(context).passes();

    if (remove) {
      event.getItemDrop().remove();
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void blockChangeByPlayer(BlockChangeByPlayerEvent event) {
    this.onBlockChange(event);
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onBlockChange(BlockChangeEvent event) {
    if (!event.isToAir()) {
      return;
    }

    List<ItemStack> drops = new ArrayList<>(event.getBlock().getDrops());
    Iterator<ItemStack> iterator = drops.iterator();

    CheckContext parent = new CheckContext(this.match);
    if (event instanceof BlockChangeByPlayerEvent) {
      parent.add(new PlayerVariable(((BlockChangeByPlayerEvent) event).getPlayer()));
    }
    parent.add(new LocationVariable(event.getBlock().getLocation()));

    while (iterator.hasNext()) {
      ItemStack item = iterator.next();

      CheckContext context = parent.duplicate();

      context.add(new MaterialVariable(event.getBlock().getState().getData()));
      context.add(new ItemVariable(this.match, item));

      boolean remove = this.removeDrops.test(context).passes();

      if (remove) {
        iterator.remove();
      }
    }

    // Don't manually spawn drops, nothing changed
    if (drops.size() == event.getBlock().getDrops().size()) {
      return;
    }

    // cancel block drop
    event.setCancelled(true);

    BlockState newState = event.getNewState();

    // but set the type
    event.getBlock().setType(newState.getType());
    event.getBlock().setData(newState.getRawData());

    for (ItemStack item : drops) {
      event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), item);
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onEntityChange(EntityChangeEvent event) {
    if (event.getAction() != Action.BREAK) {
      return;
    }

    ItemStack item = null;

    if (event.getEntity() instanceof ItemFrame) {
      item = ((ItemFrame) event.getEntity()).getItem();
    } else if (event.getEntity() instanceof Minecart) {
      item = new ItemStack(Material.MINECART);
    } else if (event.getEntity() instanceof Boat) {
      item = new ItemStack(Material.BOAT);
    }

    if (item == null) {
      return;
    }

    CheckContext context = new CheckContext(this.match);
    if (event.getWhoChanged() instanceof Player) {
      context.add(new PlayerVariable((Player) event.getWhoChanged()));
    }
    context.add(new LocationVariable(event.getEntity().getLocation()));
    context.add(new MaterialVariable(item.getData()));
    context.add(new ItemVariable(this.match, item));

    boolean remove = this.removeDrops.test(context).passes();

    if (remove) {
      // hanging break
      if (event.getCause() instanceof EntityDamageByEntityEvent && event
          .getEntity() instanceof ItemFrame) {
        EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event.getCause();
        ItemFrame frame = (ItemFrame) event.getEntity();
        damageEvent.setCancelled(true);
        frame.setItem(null);
      } else {
        event.getEntity().remove();
      }
    }
  }
}
