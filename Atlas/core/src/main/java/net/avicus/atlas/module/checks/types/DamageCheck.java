package net.avicus.atlas.module.checks.types;

import java.util.Optional;
import lombok.ToString;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.CheckResult;
import net.avicus.atlas.module.checks.variable.DamageVariable;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * A damage check checks the type of damage a player is receiving.
 */
@ToString
public class DamageCheck implements Check {

  private final DamageCause cause;

  public DamageCheck(DamageCause cause) {
    this.cause = cause;
  }

  @Override
  public CheckResult test(CheckContext context) {
    Optional<DamageVariable> damage = context.getFirst(DamageVariable.class);
    if (damage.isPresent()) {
      return CheckResult.valueOf(damage.get().getCause() == this.cause);
    }
    return CheckResult.IGNORE;
  }
}
