package net.avicus.hook.gadgets.types.arrowtrails;

import net.avicus.hook.Hook;
import net.avicus.hook.utils.HookTask;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class ArrowTrailTask extends HookTask implements Listener {

  private static final String TRAIL_META = "projectile_trail_types";
  private static final String CRITICAL_META = "arrow_is_critical";
  private final ArrowTrailManager manager;

  public ArrowTrailTask(ArrowTrailManager manager) {
    this.manager = manager;
  }

  public void start() {
    repeat(0, 10);
  }

  @Override
  public void run() {
    if (Bukkit.getOnlinePlayers().isEmpty()) {
      return;
    }

    World world = Bukkit.getOnlinePlayers().iterator().next().getWorld();

    world.getEntitiesByClass(Projectile.class)
        .stream()
        .filter(projectile -> projectile.hasMetadata(TRAIL_META))
        .forEach(projectile -> {
          if (projectile.isDead() || projectile.isOnGround()) {
            projectile.removeMetadata(TRAIL_META, Hook.plugin());
          } else {
            ArrowTrailType type = ArrowTrailType
                .valueOf(projectile.getMetadata(TRAIL_META).get(0).asString());
            this.manager.play(type, projectile.getLocation());
          }
        });

  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onProjectileLaunch(EntityShootBowEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }

    this.manager.getTrail(event.getEntity().getUniqueId()).ifPresent(c -> {
      final Projectile projectile = (Projectile) event.getProjectile();
      projectile.setMetadata(TRAIL_META,
          new FixedMetadataValue(Hook.plugin(), c.getGadget().getType().name()));

      if (projectile instanceof Arrow) {
        final Arrow arrow = (Arrow) projectile;
        arrow.setMetadata(CRITICAL_META, new FixedMetadataValue(Hook.plugin(), arrow.isCritical()));
        arrow.setCritical(false);
      }
    });
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onProjectileHit(ProjectileHitEvent event) {
    final Projectile projectile = event.getEntity();
    projectile.removeMetadata(TRAIL_META, Hook.plugin());
    if (projectile instanceof Arrow) {
      final Arrow arrow = (Arrow) projectile;
      if (arrow.hasMetadata(CRITICAL_META)) {
        arrow.setCritical(arrow.getMetadata(CRITICAL_META).get(0).asBoolean());
      }
    }
  }
}
