package net.avicus.atlas.module.checks.types;

import java.util.Optional;
import lombok.ToString;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.CheckResult;
import net.avicus.atlas.module.checks.variable.VictimVariable;

/**
 * A victim check is a wrapper check that contains player information about the victim of an attack.
 */
@ToString
public class VictimCheck implements Check {

  private final Check check;

  public VictimCheck(Check check) {
    this.check = check;
  }

  @Override
  public CheckResult test(CheckContext context) {
    Optional<VictimVariable> victim = context.getFirst(VictimVariable.class);

    if (!victim.isPresent()) {
      return CheckResult.IGNORE;
    }

    return this.check.test(victim.get());
  }
}