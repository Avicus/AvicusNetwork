package net.avicus.atlas.module.checks.types;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.match.registry.WeakReference;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.CheckResult;
import net.avicus.atlas.module.checks.variable.GroupVariable;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.objectives.IntegerObjective;
import net.avicus.atlas.module.objectives.ObjectivesModule;
import net.avicus.compendium.number.NumberComparator;

/**
 * A score check checks the score of a {@link Competitor} that is either supplied or inferred in
 * comparison with the supplied value.
 */
@ToString
public class ScoreCheck implements Check {

  @Getter
  private final int value;
  @Getter
  private final Optional<WeakReference<Competitor>> competitor;
  @Getter
  private final NumberComparator comparator;

  public ScoreCheck(int value, Optional<WeakReference<Competitor>> competitor,
      NumberComparator comparator) {
    this.value = value;
    this.competitor = competitor;
    this.comparator = comparator;
  }

  @Override
  public CheckResult test(CheckContext context) {
    ObjectivesModule module = context.getMatch().getModule(ObjectivesModule.class).orElse(null);
    if (module == null) {
      return CheckResult.IGNORE;
    }

    List<IntegerObjective> integerObjectives = module.getObjectivesByType(IntegerObjective.class);

    if (integerObjectives.isEmpty()) {
      return CheckResult.IGNORE;
    }

    AtomicBoolean result = new AtomicBoolean();

    // First check if the supplier is checking against a certain comp, if not, get from context.
    if (this.competitor.isPresent()) {
      this.competitor.get().ifPresent(c -> {
        integerObjectives.stream().filter(s -> s.canComplete(c)).forEach(s -> {
          result.compareAndSet(false, comparator.perform(value, s.getPoints(c)));
        });
      });
    } else {
      context.getFirst(GroupVariable.class).ifPresent(g -> {
        if (g.getGroup() instanceof Competitor) {
          integerObjectives.stream().filter(s -> s.canComplete((Competitor) g.getGroup()))
              .forEach(s -> {
                result.compareAndSet(false,
                    comparator.perform(value, s.getPoints((Competitor) g.getGroup())));
              });
        }
      });
    }

    return CheckResult.valueOf(result.get());
  }
}
