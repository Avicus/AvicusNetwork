package net.avicus.atlas.module.spawns;

import net.avicus.atlas.event.group.PlayerChangedGroupEvent;
import net.avicus.atlas.event.match.MatchOpenEvent;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.groups.Spectators;
import net.avicus.atlas.util.Players;
import net.avicus.grave.event.PlayerDeathEvent;
import net.avicus.magma.util.NMSUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;

public class SpawnListener implements Listener {

  private final SpawnsModule module;

  public SpawnListener(SpawnsModule module) {
    this.module = module;
  }

  @EventHandler
  public void onEntityDamage(EntityDamageEvent event) {
    if (event.getEntity().hasMetadata(RespawnTask.METADATA_TAG)) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerJoinTeam(PlayerChangedGroupEvent event) {
    module.stopRespawnTask(event.getPlayer(), false);

    if (event.isSpawnTriggered()) {
      module.spawn(event.getGroup(), event.getPlayer(), true, event.isTeleportTriggered());
    }

    module.setDead(event.getPlayer(), false);
    module.getMatch().getRequiredModule(GroupsModule.class).refreshObservers();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onMatchOpen(MatchOpenEvent event) {
    Spectators spectators = this.module.getMatch().getRequiredModule(GroupsModule.class)
        .getSpectators();
    Bukkit.getOnlinePlayers().forEach((player) -> {
      module.stopRespawnTask(player);
      module.spawn(spectators, player, true, true);
    });
  }

  @EventHandler
  public void onPlayerLeave(PlayerQuitEvent event) {
    // bring out your dead (https://www.youtube.com/watch?v=grbSQ6O6kbs)
    this.module.stopRespawnTask(event.getPlayer());
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerInteract(PlayerInteractEvent event) {
    if (!this.module.isRespawning(event.getPlayer())) {
      return;
    }

    if (event.getAction() != Action.LEFT_CLICK_AIR
        && event.getAction() != Action.LEFT_CLICK_BLOCK) {
      return;
    }

    this.module.queueAutoRespawn(event.getPlayer());
    event.setCancelled(true);
  }

  @EventHandler
  public void onPlayerInteractEntity(EntityDamageByEntityEvent event) {
    if (!(event.getDamager() instanceof Player)) {
      return;
    }

    Player damager = (Player) event.getDamager();

    if (!this.module.isRespawning(damager)) {
      return;
    }

    this.module.queueAutoRespawn(damager);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerDeath(final PlayerDeathEvent event) {
    Players.reset(event.getPlayer());
    if (!event.getPlayer().hasPotionEffect(PotionEffectType.INVISIBILITY)) {
      NMSUtils.playDeathAnimation(
          event.getPlayer()); // Have to do this because it doesn't play when we set health to 20.
    }
    this.module.startRespawnTask(event.getPlayer());
    this.module.setDead(event.getPlayer(), true);
  }
}
