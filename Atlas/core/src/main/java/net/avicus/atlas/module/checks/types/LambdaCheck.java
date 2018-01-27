package net.avicus.atlas.module.checks.types;

import java.util.function.Function;
import lombok.ToString;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.CheckResult;

/**
 * A lambda check is just a simple check that is written from the factory.
 */
@ToString
public class LambdaCheck implements Check {

  private final Function<CheckContext, CheckResult> check;

  public LambdaCheck(Function<CheckContext, CheckResult> check) {
    this.check = check;
  }

  @Override
  public CheckResult test(CheckContext context) {
    return this.check.apply(context);
  }
}
