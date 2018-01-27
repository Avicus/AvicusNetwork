package net.avicus.atlas.module.checks.types;

import java.util.Optional;
import lombok.ToString;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.CheckResult;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.module.stats.StatsModule;
import net.avicus.atlas.module.stats.action.damage.PlayerKillAction;
import net.avicus.atlas.module.stats.action.lifetime.type.PlayerLifetime;
import org.apache.commons.lang.math.Range;

/**
 * A kill streak check checks the player's current kill count based on a range.
 */
@ToString
public class KillStreakCheck implements Check {

  private final Range range;
  private final Scope scope;

  public KillStreakCheck(Range range, Scope scope) {
    this.range = range;
    this.scope = scope;
  }

  @Override
  public CheckResult test(CheckContext context) {
    Optional<StatsModule> statsModule = context.getMatch().getModule(StatsModule.class);

    if (!statsModule.isPresent()) {
      return CheckResult.IGNORE;
    }

    Optional<PlayerVariable> optional = context.getFirst(PlayerVariable.class);

    if (!optional.isPresent()) {
      return CheckResult.IGNORE;
    }

    int kills = 0;
    switch (this.scope) {
      case LIFE:
        PlayerLifetime lifetime = statsModule.get().getStore().getLifetimeStore()
            .getCurrentLifetime(optional.get().getPlayer(), false);
        if (lifetime != null) {
          kills = (int) lifetime.getActions().stream().filter(a -> a instanceof PlayerKillAction)
              .count();
        }
        break;
      case MATCH:
        kills = (int) statsModule.get().getStore().getLifetimeStore().getPlayerLifetimes()
            .get(optional.get().getPlayer().getUniqueId()).stream()
            .flatMap(l -> l.getActions().stream().filter(a -> a instanceof PlayerKillAction))
            .count();
    }

    return CheckResult.valueOf(this.range.containsInteger(kills));
  }

  public enum Scope {
    LIFE, MATCH
  }
}
