package net.avicus.atlas.module.doublekill;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.ToString;
import net.avicus.atlas.event.player.PlayerSpawnBeginEvent;
import net.avicus.atlas.module.Module;
import net.avicus.grave.event.PlayerDeathEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@ToString
public class DoubleKillModule implements Module {

  private final List<UUID> respawning = new ArrayList<>();

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    this.respawning.add(event.getPlayer().getUniqueId());
  }

  @EventHandler
  public void onPlayerRespawn(PlayerSpawnBeginEvent event) {
    this.respawning.remove(event.getPlayer().getUniqueId());
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    this.respawning.remove(event.getPlayer().getUniqueId());
  }

  @EventHandler(ignoreCancelled = true)
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    if (this.respawning.contains(event.getDamager().getUniqueId())) {
      event.setCancelled(true);
    }
  }
}
