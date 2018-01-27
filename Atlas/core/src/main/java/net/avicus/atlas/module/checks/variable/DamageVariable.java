package net.avicus.atlas.module.checks.variable;

import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.module.checks.Variable;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * The damage variable that contains the type of damage inflicted on a player.
 */
@ToString
public class DamageVariable implements Variable {

  @Getter
  private final DamageCause cause;

  public DamageVariable(DamageCause cause) {
    this.cause = cause;
  }
}
