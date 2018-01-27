package net.avicus.atlas.module.checks.types;

import java.util.Optional;
import lombok.ToString;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.CheckResult;
import net.avicus.atlas.module.checks.variable.MaterialVariable;
import net.avicus.compendium.inventory.MaterialMatcher;

/**
 * A material check checks the type of material involved in an event.
 */
@ToString
public class MaterialCheck implements Check {

  private final MaterialMatcher matcher;

  public MaterialCheck(MaterialMatcher matcher) {
    this.matcher = matcher;
  }

  @Override
  public CheckResult test(CheckContext context) {
    Optional<MaterialVariable> optional = context.getFirst(MaterialVariable.class);

    if (!optional.isPresent()) {
      return CheckResult.IGNORE;
    }

    boolean matches = this.matcher.matches(optional.get().getData());
    return CheckResult.valueOf(matches);
  }
}
