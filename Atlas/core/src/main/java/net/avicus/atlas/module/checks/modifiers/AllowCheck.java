package net.avicus.atlas.module.checks.modifiers;

import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.CheckResult;

/**
 * An allow check will only pass if the child check is not ignored and passes.
 */
@ToString
public class AllowCheck implements Check {

  @Getter
  private final Check child;

  public AllowCheck(Check child) {
    this.child = child;
  }

  @Override
  public CheckResult test(CheckContext context) {
    CheckResult result = child.test(context);

    // allow if allow
    if (result == CheckResult.ALLOW) {
      return CheckResult.ALLOW;
    }

    // otherwise ignore
    return CheckResult.IGNORE;
  }
}
