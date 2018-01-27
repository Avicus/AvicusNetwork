package net.avicus.atlas.module.checks.modifiers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.CheckResult;

/**
 * An any check will pass if any of it's children pass.
 */
@ToString
public class AnyCheck implements Check {

  @Getter
  private final List<Check> children;

  public AnyCheck(List<Check> children) {
    this.children = children;
  }

  public AnyCheck(Check... children) {
    this(Arrays.asList(children));
  }

  @Override
  public CheckResult test(CheckContext context) {
    Map<CheckResult, Integer> results = Check.test(this.children, context);

    // allow if one child allows
    if (results.get(CheckResult.ALLOW) > 0) {
      return CheckResult.ALLOW;
    }

    // deny if none allow, more than one deny
    else if (results.get(CheckResult.ALLOW) == 0 && results.get(CheckResult.DENY) > 0) {
      return CheckResult.DENY;
    }

    // otherwise ignore
    return CheckResult.IGNORE;
  }
}
