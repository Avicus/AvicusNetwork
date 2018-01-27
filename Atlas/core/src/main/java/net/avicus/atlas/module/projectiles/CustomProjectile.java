package net.avicus.atlas.module.projectiles;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.match.registry.WeakReference;
import net.avicus.atlas.module.loadouts.Loadout;
import net.avicus.compendium.inventory.SingleMaterialMatcher;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;
import org.joda.time.Duration;
import org.joda.time.Instant;

/**
 * A custom projectile with various features.
 */
@ToString
public class CustomProjectile {

  private final UUID uuid;
  // TODO: Implement
  private final String name;
  @Getter
  private final boolean throwable;
  @Getter
  private final EntityType type;
  @Getter
  private final double damage;
  @Getter
  private final double velocity;
  // TODO: Implement
  @Getter
  private final Optional<WeakReference<Loadout>> loadout;
  @Getter
  private final Optional<Duration> cooldown;
  @Getter
  private final boolean mount;
  @Getter
  private final boolean sticky;
  @Getter
  private final Optional<SingleMaterialMatcher> block;

  @Getter
  private final Collection<PotionEffect> effects;

  private final Map<Player, Instant> lastUsage;

  public CustomProjectile(UUID uuid,
      String name,
      boolean throwable,
      EntityType type,
      double damage,
      double velocity,
      Optional<WeakReference<Loadout>> loadout,
      Optional<Duration> cooldown,
      boolean mount,
      boolean sticky,
      Optional<SingleMaterialMatcher> block,
      Collection<PotionEffect> effects) {
    this.uuid = uuid;
    this.name = name;
    this.throwable = throwable;
    this.type = type;
    this.damage = damage;
    this.velocity = velocity;
    this.loadout = loadout;
    this.cooldown = cooldown;
    this.mount = mount;
    this.sticky = sticky;
    this.block = block;
    this.effects = effects;
    this.lastUsage = new HashMap<>();
  }

  public Entity spawnEntity(Player player) {
    Entity entity;

    if (Projectile.class.isAssignableFrom(this.type.getEntityClass())) {
      // Launch-able projectiles carry a "damager"
      entity = player
          .launchProjectile((Class<? extends Projectile>) this.type.getEntityClass(), new Vector());
      if (entity.getType() == EntityType.SPLASH_POTION && !this.effects.isEmpty()) {
        Collection<PotionEffect> meta = ((ThrownPotion) entity).getEffects();
        meta.clear();
        meta.addAll(this.effects);
      }
    } else if (this.type == EntityType.FALLING_BLOCK && this.block.isPresent()) {
      entity = player.getWorld()
          .spawnFallingBlock(player.getEyeLocation(), this.block.get().getMaterial(),
              this.block.get().getData().orElse((byte) 0));
    } else {
      entity = player.getWorld().spawnEntity(player.getEyeLocation(), this.type);
    }

    return entity;
  }

  /**
   * Sets when a player last used this projectile (cooldowns).
   */
  public void setLastUsage(Player player, Instant instant) {
    this.lastUsage.put(player, instant);
  }

  /**
   * Clears the last usage of this projectile by a player.
   */
  public void clearLastUsage(Player player) {
    this.lastUsage.remove(player);
  }

  /**
   * Checks if the cooldown for this projectile has elapsed for a player.
   */
  public boolean canUse(Player player) {
    if (!this.cooldown.isPresent()) {
      return true;
    }

    Instant lastUsage = this.lastUsage.get(player);

    if (lastUsage == null) {
      return true;
    }

    Instant now = Instant.now();
    Duration elapsed = new Duration(lastUsage, now);
    return elapsed.isLongerThan(this.cooldown.get());
  }

  public boolean hasCooldown() {
    return this.cooldown.isPresent();
  }

  /**
   * Used for a prettier display in item lore than a giant UUID.
   *
   * @return The (mostly) unique identifier.
   */
  public String getShortUuid() {
    return this.uuid.toString().substring(0, 5);
  }
}
