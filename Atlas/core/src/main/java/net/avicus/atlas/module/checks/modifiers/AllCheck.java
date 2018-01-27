package net.avicus.atlas.module.checks.modifiers;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.CheckResult;

/**
 * An all check is a check that will only pass if all of the children pass.
 */
@ToString
public class AllCheck implements Check {

  @Getter
  private final List<Check> children;

  public AllCheck(List<Check> children) {
    this.children = children;
  }

  @Override
  public CheckResult test(CheckContext context) {
    Map<CheckResult, Integer> results = Check.test(this.children, context);
    int total = results.values().stream().mapToInt(Integer::intValue).sum();

    // allow if all allow
    if (results.get(CheckResult.ALLOW) == total) {
      return CheckResult.ALLOW;
    }

    // deny if at least one denies
    if (results.get(CheckResult.DENY) > 0) {
      return CheckResult.DENY;
    }

    // otherwise ignore
    return CheckResult.IGNORE;
  }
}
