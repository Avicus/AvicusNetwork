package net.avicus.atlas.module.checks.modifiers;

import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.CheckResult;

/**
 * A deny check will only pass if the child check fails.
 */
@ToString
public class DenyCheck implements Check {

  @Getter
  private final Check child;

  public DenyCheck(Check child) {
    this.child = child;
  }

  @Override
  public CheckResult test(CheckContext context) {
    CheckResult result = child.test(context);

    // allow if deny
    if (result == CheckResult.DENY) {
      return CheckResult.ALLOW;
    }

    // otherwise ignore
    return CheckResult.IGNORE;
  }
}
