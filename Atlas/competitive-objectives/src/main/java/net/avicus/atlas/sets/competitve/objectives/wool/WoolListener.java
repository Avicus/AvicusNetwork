package net.avicus.atlas.sets.competitve.objectives.wool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.avicus.atlas.event.group.PlayerChangedGroupEvent;
import net.avicus.atlas.event.world.BlockChangeByPlayerEvent;
import net.avicus.atlas.event.world.BlockChangeEvent;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.groups.Spectators;
import net.avicus.atlas.module.objectives.ObjectivesModule;
import net.avicus.atlas.module.objectives.locatable.LocatableUpdateDistanceEvent;
import net.avicus.atlas.module.shop.PlayerEarnPointEvent;
import net.avicus.atlas.sets.competitve.objectives.wool.event.WoolPickupEvent;
import net.avicus.atlas.sets.competitve.objectives.wool.event.WoolPlaceEvent;
import net.avicus.atlas.util.AtlasTask;
import net.avicus.atlas.util.Events;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.grave.event.PlayerDeathByPlayerEvent;
import net.avicus.grave.event.PlayerDeathEvent;
import net.avicus.magma.util.distance.DistanceCalculationMetric;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import tc.oc.tracker.event.PlayerCoarseMoveEvent;

public class WoolListener implements Listener {

  private final ObjectivesModule module;
  private final List<WoolObjective> wools;

  public WoolListener(ObjectivesModule module, List<WoolObjective> wools) {
    this.module = module;
    this.wools = wools;
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    for (WoolObjective wool : this.wools) {
      wool.setTouchedRecently(event.getPlayer(), false);
    }
  }

  @EventHandler
  public void onPlayerChangeTeam(PlayerChangedGroupEvent event) {
    for (WoolObjective wool : this.wools) {
      wool.setTouchedRecently(event.getPlayer(), false);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPickupItem(PlayerPickupItemEvent event) {
    ItemStack item = event.getItem().getItemStack();
    if (item == null) {
      return;
    }

    Group group = this.module.getMatch().getRequiredModule(GroupsModule.class)
        .getGroup(event.getPlayer());

    for (WoolObjective wool : this.wools) {
      if (!wool.getMatcher().matches(item.getData())) {
        continue;
      }

      if (!wool.canPickup(group)) {
        event.getItem().remove();
        event.setCancelled(true);
        break;
      }

      if (wool.canComplete(event.getPlayer()) && wool.isTouchRelevant(event.getPlayer())) {
        wool.setTouchedRecently(event.getPlayer(), true);

        WoolPickupEvent call = new WoolPickupEvent(wool, event.getPlayer());
        Events.call(call);
        Events.call(new PlayerEarnPointEvent(event.getPlayer(), "wool-pickup"));
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onInventoryClick(InventoryClickEvent event) {
    ItemStack item = event.getCurrentItem();
    if (item == null) {
      return;
    }

    InventoryHolder holder = event.getInventory().getHolder();
    Block block;

    if (holder instanceof Chest) {
      block = ((Chest) holder).getBlock();
    } else if (holder instanceof DoubleChest) {
      block = ((DoubleChest) holder).getLocation().getBlock();
    } else {
      return;
    }

    Player player = (Player) event.getWhoClicked();
    Competitor competitor = this.module.getMatch().getRequiredModule(GroupsModule.class)
        .getCompetitorOf(player).orElse(null);

    if (competitor == null) {
      return;
    }

    boolean can = false;

    for (WoolObjective wool : this.wools) {
      if (can) {
        break;
      }

      if (wool.getSource().isPresent() && !wool.getSource().get().contains(block)) {
        continue;
      }

      if (!wool.getMatcher().matches(item.getData())) {
        continue;
      }

      if (!wool.canComplete(competitor)) {
        event.setCancelled(true);
        continue;
      } else {
        can = true;
        event.setCancelled(false);
      }

      if (wool.isPlaced()) {
        event.getWhoClicked().sendMessage(Messages.ERROR_OBJECTIVE_PLACED_WOOL.with(ChatColor.RED));
        event.setCancelled(true);
        continue;
      }

      if (wool.isTouchRelevant((Player) event.getWhoClicked())) {
        wool.setTouchedRecently((Player) event.getWhoClicked(), true);

        WoolPickupEvent call = new WoolPickupEvent(wool, player);
        Events.call(call);
        Events.call(new PlayerEarnPointEvent(player, "wool-pickup"));
        break;
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onWoolPickup(WoolPickupEvent event) {
    WoolObjective wool = (WoolObjective) event.getObjective();
    GroupsModule groups = module.getMatch().getRequiredModule(GroupsModule.class);
    Group playerGroup = groups.getGroup(event.getPlayer());
    Spectators spectators = groups.getSpectators();

    List<Player> toMessage = new ArrayList<>();
    toMessage.addAll(playerGroup.getPlayers());
    toMessage.addAll(spectators.getPlayers());

    Localizable woolText = wool.getName().toText(wool.getChatColor());
    Localizable who = new UnlocalizedText(event.getPlayer().getName(), playerGroup.getChatColor());
    Localizable broadcast = Messages.GENERIC_OBJECTIVE_PICKUP.with(woolText, who);
    broadcast = Messages.UI_IMPORTANT.with(TextStyle.ofBold(), broadcast);

    for (Player player : toMessage) {
      player.sendMessage(broadcast);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void blockChangeByPlayer(BlockChangeByPlayerEvent event) {
    this.onBlockChange(event);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onBlockChange(BlockChangeEvent event) {
    Block block = event.getBlock();

    for (WoolObjective wool : this.wools) {
      if (wool.getSource().isPresent() && wool.getSource().get().contains(block)) {
        event.setCancelled(true);
        break;
      }

      if (!wool.getDestination().contains(block)) {
        continue;
      }

      if (event.isToAir() || wool.isCompleted()) {
        event.setCancelled(true);
        break;
      } else {
        event.setCancelled(true);

        if (!(event instanceof BlockChangeByPlayerEvent)) {
          break;
        }

        Player player = ((BlockChangeByPlayerEvent) event).getPlayer();
        Competitor competitor = this.module.getMatch().getRequiredModule(GroupsModule.class)
            .getCompetitorOf(player).orElse(null);

        if (competitor == null) {
          return;
        }

        if (!wool.canComplete(competitor)) {
          player.sendMessage(Messages.ERROR_OBJECTIVE_OTHER_WOOL.with(ChatColor.RED));
          break;
        }

        if (!wool.getMatcher().matches(event.getNewState())) {
          player.sendMessage(Messages.ERROR_OBJECTIVE_BAD_WOOL.with(ChatColor.RED));
          break;
        }

        wool.place(player);

        WoolPlaceEvent call = new WoolPlaceEvent(wool, player);
        Events.call(call);
        Events.call(new PlayerEarnPointEvent(player, "wool-place"));

        event.setCancelled(false);
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onInventoryClose(InventoryCloseEvent event) {
    if (!(event.getInventory().getHolder() instanceof BlockState)) {
      return;
    }

    Player player = (Player) event.getPlayer();

    if (this.module.getMatch().getRequiredModule(GroupsModule.class).isObservingOrDead(player)) {
      return;
    }

    Block block = ((BlockState) event.getInventory().getHolder()).getBlock();
    Inventory inventory = event.getInventory();

    // Only refill if no one is looking
    if (inventory.getViewers().size() > 1) {
      return;
    }

    for (WoolObjective wool : this.wools) {
      if (!wool.isRefillable(block)) {
        continue;
      }

      Map<Integer, ItemStack> refill = wool.getRefill(block).get();

      AtlasTask refillTask = AtlasTask.of(() -> {
        int refilled = inventory.getContents().length;
        for (Integer slot : refill.keySet()) {
          if (refilled > wool.getMaxRefill()) {
            break;
          }
          if (inventory.getItem(slot) == null) {
            inventory.setItem(slot, refill.get(slot).clone());
            refilled++;
          }
        }
      });
      if (wool.getRefillDelay().isPresent()) {
        refillTask.later((int) wool.getRefillDelay().get().getStandardSeconds() * 20);
      } else {
        refillTask.now();
      }
    }
  }

  @EventHandler
  public void onPrepareItemCraft(PrepareItemCraftEvent event) {
    ItemStack result = event.getRecipe().getResult();
    InventoryHolder holder = event.getInventory().getHolder();

    if (!(holder instanceof Player) || result == null) {
      return;
    }

    Player player = (Player) holder;

    for (WoolObjective wool : this.wools) {
      if (wool.isCraftable()) {
        continue;
      }

      if (wool.getMatcher().matches(result.getData())) {
        Localizable name = wool.getName().toText();

        player.sendMessage(Messages.ERROR_CANNOT_CRAFT.with(ChatColor.RED, name));
        event.getInventory().setResult(null);
        break;
      }
    }
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onMove(PlayerCoarseMoveEvent event) {
    this.wools.forEach(w -> {
      if (w.getDistanceCalculationMetricType(event.getPlayer())
          == DistanceCalculationMetric.Type.PLAYER && w
          .updateDistance(event.getPlayer(), event.getTo())) {
        Events.call(new LocatableUpdateDistanceEvent(w));
      }
    });
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onKill(PlayerDeathByPlayerEvent event) {
    this.wools.forEach(w -> {
      if (w.getDistanceCalculationMetricType(event.getCause())
          == DistanceCalculationMetric.Type.KILL && w
          .updateDistance(event.getCause(), event.getLocation())) {
        Events.call(new LocatableUpdateDistanceEvent(w));
      }
    });
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onBlock(BlockChangeByPlayerEvent event) {
    this.wools.forEach(w -> {
      if (w.getDistanceCalculationMetricType(event.getPlayer())
          == DistanceCalculationMetric.Type.BLOCK && w.getMatcher()
          .matches(event.getBlock().getState()) && w
          .updateDistance(event.getPlayer(), event.getBlock().getLocation())) {
        Events.call(new LocatableUpdateDistanceEvent(w));
      }
    });
  }


  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onWoolPlace(BlockChangeByPlayerEvent event) {
    this.wools.forEach(w -> {
      if (w.getDistanceCalculationMetricType(event.getPlayer())
          == DistanceCalculationMetric.Type.BLOCK && w.getMatcher()
          .matches(event.getBlock().getState()) && w
          .updateDistance(event.getPlayer(), event.getBlock().getLocation())) {
        Events.call(new LocatableUpdateDistanceEvent(w));
      }
    });
  }
}
