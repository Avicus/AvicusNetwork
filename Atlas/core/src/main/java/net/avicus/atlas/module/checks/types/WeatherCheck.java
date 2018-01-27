package net.avicus.atlas.module.checks.types;

import lombok.ToString;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.CheckResult;
import org.bukkit.WeatherType;

/**
 * A weather check checks the weather in the match world.
 */
@ToString
public class WeatherCheck implements Check {

  private final WeatherType type;

  public WeatherCheck(WeatherType type) {
    this.type = type;
  }

  @Override
  public CheckResult test(CheckContext context) {
    switch (this.type) {
      case DOWNFALL:
        return CheckResult.valueOf(context.getMatch().getWorld().hasStorm());
      case CLEAR:
        return CheckResult.valueOf(!context.getMatch().getWorld().hasStorm());
    }
    return CheckResult.IGNORE;
  }
}
