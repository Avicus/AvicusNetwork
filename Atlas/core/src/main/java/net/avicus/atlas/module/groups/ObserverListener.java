package net.avicus.atlas.module.groups;

import com.google.common.collect.Sets;
import java.util.Set;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.event.group.PlayerChangedGroupEvent;
import net.avicus.atlas.event.match.MatchStateChangeEvent;
import net.avicus.atlas.event.player.PlayerSpawnBeginEvent;
import net.avicus.atlas.event.world.BlockChangeByPlayerEvent;
import net.avicus.atlas.event.world.EntityChangeEvent;
import net.avicus.atlas.util.VersionUtil;
import net.avicus.compendium.menu.inventory.InventoryMenuAdapter;
import net.avicus.grave.event.PlayerDeathEvent;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.PlayerInventory;

public class ObserverListener implements Listener {

  private static final Set<Material> TOOL_TYPES =
      Sets.newHashSet(Material.COMPASS, Material.WOOD_AXE);

  private final GroupsModule module;

  public ObserverListener(GroupsModule module) {
    this.module = module;
  }

  private void recalculatePerms(Player player, boolean add) {
    player.addAttachment(Atlas.get(), "hook.tp", add);
    player.addAttachment(Atlas.get(), "worldedit.navigation.thru.tool", add);
    player.addAttachment(Atlas.get(), "worldedit.navigation.thru.command", add);
    player.addAttachment(Atlas.get(), "worldedit.navigation.jumpto.tool", add);
    player.addAttachment(Atlas.get(), "worldedit.navigation.jumpto.command", add);
  }

  private boolean notPlaying(Entity entity) {
    if (!(entity instanceof Player)) {
      throw new RuntimeException("Can't check observer on non-player.");
    }
    return ((Player) entity).isOnline() && this.module.isObservingOrDead((Player) entity);
  }

  private boolean holdingTool(Player player) {
    return player.getItemInHand() != null && TOOL_TYPES.contains(player.getItemInHand().getType());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerChangeGroup(PlayerChangedGroupEvent event) {
    boolean fromObserver =
        event.getGroupFrom().isPresent() && event.getGroupFrom().get().isObserving();
    boolean toObserver = event.getGroup().isObserving();

    // Ignore if observer state is the same
    if (fromObserver == toObserver) {
      return;
    }

    recalculatePerms(event.getPlayer(), toObserver);
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onMatchStateChange(MatchStateChangeEvent event) {
    for (Player player : event.getMatch().getPlayers()) {
      recalculatePerms(player, this.module.getGroup(player).isObserving());
    }
  }

  @EventHandler
  public void onPlayerSpawn(PlayerSpawnBeginEvent event) {
    boolean observing = event.getGroup().isObserving();

    // Spigot
    event.getPlayer().spigot().setCollidesWithEntities(!observing);
    // Todo?
    if (!VersionUtil.isCombatUpdate()) {
      event.getPlayer().spigot().setAffectsSpawning(!observing);
    }

    if (observing) {
      event.getPlayer().setGameMode(GameMode.SURVIVAL);

      event.getPlayer().setAllowFlight(true);
      event.getPlayer().setFlying(true);

      // todo: twice is necessary for some reason? still the case?
      event.getPlayer().setAllowFlight(true);
      event.getPlayer().setFlying(true);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onFoodLevelChange(FoodLevelChangeEvent event) {
    if (notPlaying(event.getEntity())) {
      Player player = (Player) event.getEntity();
      player.setSaturation(20);
      event.setFoodLevel(20);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPlayerInteract(final PlayerInteractEvent event) {
    if (notPlaying(event.getPlayer()) && !holdingTool(event.getPlayer())) {
      event.setCancelled(true);
      event.setUseItemInHand(Event.Result.DENY);
      event.setUseInteractedBlock(Event.Result.DENY);
      // Right clicking armor
      event.getPlayer().updateInventory();
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerInteract(final PlayerInteractAtEntityEvent event) {
    if (notPlaying(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void disallowLilyPads(final InventoryClickEvent event) {
    if (!(event.getClickedInventory() instanceof PlayerInventory)) {
      return;
    }

    Player player = (Player) event.getWhoClicked();

    if (event.getCursor() == null) {
      return;
    }

    if (notPlaying(player) && event.getCursor().getType() == Material.WATER_LILY) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerDamage(EntityDamageEvent event) {
    if (event.getEntity() instanceof Player && notPlaying(event.getEntity())) {
      event.setDamage(0);
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerDeath(PlayerDeathEvent event) {
    if (notPlaying(event.getEntity())) {
      event.getPlayer().setHealth(20);
    }
  }

  @EventHandler
  public void onEntityTarget(EntityTargetLivingEntityEvent event) {
    if (event.getTarget() instanceof Player && notPlaying(event.getTarget())) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    if (event.getDamager() instanceof Player && notPlaying(event.getDamager())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onBlockChange(BlockChangeByPlayerEvent event) {
    if (notPlaying(event.getPlayer()) && !holdingTool(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onBlockChange(BlockDamageEvent event) {
    if (notPlaying(event.getPlayer()) && !holdingTool(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onEntityChange(EntityChangeEvent event) {
    if (event.getWhoChanged() instanceof Player) {
      if (notPlaying(event.getWhoChanged())) {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    if (event.getWhoClicked() instanceof Player && notPlaying(event.getWhoClicked())) {
      if (event.getInventory().getType() != InventoryType.PLAYER && !(event.getInventory().getHolder() instanceof InventoryMenuAdapter)) {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void onPlayerDropItem(PlayerPickupItemEvent event) {
    if (notPlaying(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onPlayerDropItem(PlayerDropItemEvent event) {
    if (notPlaying(event.getPlayer())) {
      event.getItemDrop().remove();
      event.getPlayer().getInventory().addItem(event.getItemDrop().getItemStack());
    }
  }

  @EventHandler
  public void onVehicleDamage(VehicleDamageEvent event) {
    if (event.getAttacker() instanceof Player && notPlaying(event.getAttacker())) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onVehicleEnter(VehicleEnterEvent event) {
    if (event.getEntered() instanceof Player && notPlaying(event.getEntered())) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onVehicleExit(VehicleExitEvent event) {
    if (event.getExited() instanceof Player && notPlaying(event.getExited())) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onEntityCombustEvent(EntityCombustByBlockEvent event) {
    if (event.getEntity() instanceof Player && notPlaying(event.getEntity())) {
      event.getEntity().setFireTicks(0);
    }
  }

  @EventHandler
  public void onLaunch(ProjectileLaunchEvent event) {
    if (event.getEntity().getShooter() instanceof Player && notPlaying(
        (Entity) event.getEntity().getShooter())) {
      event.setCancelled(true);
    }
  }
}
