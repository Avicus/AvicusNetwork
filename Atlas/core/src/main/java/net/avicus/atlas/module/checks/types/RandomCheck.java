package net.avicus.atlas.module.checks.types;

import java.util.Random;
import lombok.ToString;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.CheckResult;

/**
 * A random check randomly passes based on the supplied amount of randomness.
 */
@ToString
public class RandomCheck implements Check {

  private final static Random random = new Random();
  private final double value;

  public RandomCheck(double value) {
    this.value = value;
  }

  @Override
  public CheckResult test(CheckContext context) {
    return CheckResult.valueOf(random.nextDouble() <= this.value);
  }
}
