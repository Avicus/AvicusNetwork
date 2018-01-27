package net.avicus.atlas.module.checks.modifiers;

import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.CheckResult;

/**
 * A not check invets the result of it's child check, unless it is ignored.
 */
@ToString
public class NotCheck implements Check {

  @Getter
  private final Check child;

  public NotCheck(Check child) {
    this.child = child;
  }

  @Override
  public CheckResult test(CheckContext context) {
    CheckResult result = child.test(context);

    // ignore if ignore
    if (result == CheckResult.IGNORE) {
      return CheckResult.IGNORE;
    }

    // otherwise invert
    return result == CheckResult.ALLOW ? CheckResult.DENY : CheckResult.ALLOW;
  }
}
