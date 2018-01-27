package net.avicus.atlas.module.loadouts.type;

import javax.annotation.Nullable;
import lombok.ToString;
import net.avicus.atlas.module.loadouts.Loadout;
import net.avicus.compendium.number.PreparedNumberAction;
import org.bukkit.entity.Player;

@ToString(callSuper = true)
public class HealthLoadout extends Loadout {

  private final PreparedNumberAction health;
  private final PreparedNumberAction maxHealth;
  private final PreparedNumberAction healthScale;

  public HealthLoadout(boolean force, @Nullable Loadout parent, PreparedNumberAction health,
      PreparedNumberAction maxHealth, PreparedNumberAction healthScale) {
    super(force, parent);
    this.health = health;
    this.maxHealth = maxHealth;
    this.healthScale = healthScale;
  }

  @Override
  public void give(Player player, boolean force) {
    // Health
    if (this.maxHealth != null) {
      player.setMaxHealth(this.maxHealth.perform(player.getMaxHealth()));
    }
    if (this.health != null) {
      player.setHealth(Math.max(0.1, this.health.perform(player.getHealth())));
    }
    if (this.healthScale != null) {
      player.setHealthScale(this.healthScale.perform(player.getHealthScale()));
    }
  }
}
