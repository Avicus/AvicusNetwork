package net.avicus.atlas.module.projectiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.ToString;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.event.group.PlayerChangedGroupEvent;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.Module;
import net.avicus.atlas.util.AtlasTask;
import net.avicus.atlas.util.Messages;
import net.avicus.magma.item.ItemTag;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import org.joda.time.Instant;
import org.spigotmc.event.entity.EntityDismountEvent;
import tc.oc.tracker.Trackers;
import tc.oc.tracker.trackers.ExplosiveTracker;

@ToString(exclude = "match")
public class ProjectilesModule implements Module {

  public static final String DAMAGE_METADATA_TAG = "atlas.projectile-damage";
  public static final String UUID_METADATA_TAG = "atlas.projectile-shortuuid";
  public static final ItemTag.String PROJECTILE_ID_TAG = new ItemTag.String("atlas.projectile-id",
      "");

  private final Match match;
  private final List<CustomProjectile> projectiles;

  public ProjectilesModule(Match match) {
    this.match = match;
    this.projectiles = new ArrayList<>();
  }

  /**
   * Applies an identifier to the lore of an item stack based on the custom projectile.
   */
  public static void applyProjectileFormat(ItemStack itemStack, CustomProjectile projectile) {
    PROJECTILE_ID_TAG.set(itemStack, projectile.getShortUuid());
  }

  private void resetUsage(Player player) {
    for (CustomProjectile projectile : this.projectiles) {
      projectile.clearLastUsage(player);
    }
  }

  @EventHandler
  public void onChangeGroup(PlayerChangedGroupEvent event) {
    // Reset when they change groups
    resetUsage(event.getPlayer());
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    // Reset on leave
    resetUsage(event.getPlayer());
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onShootBow(EntityShootBowEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }

    CustomProjectile custom = scanProjectileFormat(event.getBow()).orElse(null);
    if (custom == null) {
      return;
    }

    Entity projectile = event.getProjectile();
    projectile.remove();

    Player player = (Player) event.getEntity();

    // Cooldown timer
    if (custom.hasCooldown()) {
      // Cooldown not reached?
      if (!custom.canUse(player)) {
        player.sendMessage(Messages.ERROR_COOLDOWN.with(ChatColor.RED));
        event.setCancelled(true);
        return;
      }

      // Used right now
      custom.setLastUsage(player, Instant.now());
    }

    // Spawn the entity
    Entity entity = custom.spawnEntity(player);

    // Custom damage
    double damage = event.getForce() * custom.getDamage();
    entity.setMetadata(DAMAGE_METADATA_TAG, new FixedMetadataValue(Atlas.get(), damage));
    entity
        .setMetadata(UUID_METADATA_TAG, new FixedMetadataValue(Atlas.get(), custom.getShortUuid()));

    // Velocity
    entity.setVelocity(projectile.getVelocity());

    // Mountable
    if (custom.isMount()) {
      entity.setPassenger(event.getEntity());
    }

    // Tnt Owner
    if (entity instanceof TNTPrimed) {
      Trackers.getTracker(ExplosiveTracker.class).setOwner((TNTPrimed) entity, player);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onUseCustomProjectile(PlayerInteractEvent event) {
    if (event.getAction() != Action.RIGHT_CLICK_AIR
        && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    if (event.getItem() == null || event.getItem().getType() == Material.BOW) {
      return;
    }

    CustomProjectile custom = scanProjectileFormat(event.getItem()).orElse(null);

    // Not a projectile, or not throwable
    if (custom == null || !custom.isThrowable()) {
      return;
    }

    // Spawn the entity
    Entity entity = custom.spawnEntity(event.getPlayer());

    // Velocity
    Vector velocity = event.getPlayer().getLocation().getDirection().normalize()
        .multiply(custom.getVelocity());
    entity.setVelocity(velocity);

    // Custom damage
    double damage = custom.getDamage();
    entity.setMetadata(DAMAGE_METADATA_TAG, new FixedMetadataValue(Atlas.get(), damage));

    entity
        .setMetadata(UUID_METADATA_TAG, new FixedMetadataValue(Atlas.get(), custom.getShortUuid()));

    // Mountable
    if (custom.isMount()) {
      entity.setPassenger(event.getPlayer());
    }

    // Tnt Owner
    if (entity instanceof TNTPrimed) {
      Trackers.getTracker(ExplosiveTracker.class).setOwner((TNTPrimed) entity, event.getPlayer());
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    // Resolve custom damage
    if (event.getDamager() instanceof Projectile && event.getDamager()
        .hasMetadata(DAMAGE_METADATA_TAG)) {
      double damage = event.getDamager().getMetadata(DAMAGE_METADATA_TAG).get(0).asDouble();
      event.setDamage(damage);

      // Loadout
      Optional<CustomProjectile> projectile = getProjectile(
          event.getDamager().getMetadata(UUID_METADATA_TAG).get(0).asString());

      if (!(event.getEntity() instanceof Player)) {
        return;
      }

      if (!projectile.isPresent() || !projectile.get().getLoadout().isPresent()) {
        return;
      }

      projectile.get().getLoadout().get().ifPresent(l -> l.apply((Player) event.getEntity()));
    }
  }

  @EventHandler
  public void onDismount(EntityDismountEvent event) {
    Entity entity = event.getDismounted();

    if (!entity.hasMetadata(UUID_METADATA_TAG)) {
      return;
    }

    Optional<CustomProjectile> projectile = getProjectile(
        entity.getMetadata(UUID_METADATA_TAG).get(0).asString());

    if (!projectile.isPresent()) {
      return;
    }

    if (projectile.get().isSticky()) {
      AtlasTask.of(() -> entity.setPassenger(event.getEntity())).now();
    }
  }

  /**
   * Registers a custom projectile for scanning and such.
   */
  public void registerProjectile(CustomProjectile projectile) {
    this.projectiles.add(projectile);
  }

  /**
   * Resolves a custom projectile by a short UUID.
   */
  public Optional<CustomProjectile> getProjectile(String shortUuid) {
    for (CustomProjectile projectile : this.projectiles) {
      if (projectile.getShortUuid().equals(shortUuid)) {
        return Optional.of(projectile);
      }
    }
    return Optional.empty();
  }

  /**
   * Scans an item for a custom projectile.
   */
  public Optional<CustomProjectile> scanProjectileFormat(ItemStack itemStack) {
    if (PROJECTILE_ID_TAG.has(itemStack)) {
      return getProjectile(PROJECTILE_ID_TAG.get(itemStack));
    }

    return Optional.empty();
  }
}
