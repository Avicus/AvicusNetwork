package net.avicus.atlas.module.checks.types;

import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.CheckResult;
import net.avicus.atlas.module.states.StatesModule;
import net.avicus.compendium.number.NumberComparator;
import org.joda.time.Duration;

/**
 * A time check checks the match time in comparison with the supplied value.
 */
@ToString
public class TimeCheck implements Check {

  @Getter
  private final Duration value;
  @Getter
  private final NumberComparator comparator;

  public TimeCheck(Duration value, NumberComparator comparator) {
    this.value = value;
    this.comparator = comparator;
  }

  @Override
  public CheckResult test(CheckContext context) {
    long time = context.getMatch().getRequiredModule(StatesModule.class).getTotalPlayingDuration()
        .getStandardSeconds();
    long compare = this.value.getStandardSeconds();

    return CheckResult.valueOf(this.comparator.perform(time, compare));
  }
}
