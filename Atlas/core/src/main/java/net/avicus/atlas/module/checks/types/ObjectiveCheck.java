package net.avicus.atlas.module.checks.types;

import java.util.Optional;
import lombok.ToString;
import net.avicus.atlas.match.registry.WeakReference;
import net.avicus.atlas.module.bridge.ObjectivesModuleBridge;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.CheckResult;
import net.avicus.atlas.module.groups.teams.Team;
import net.avicus.atlas.module.objectives.GlobalObjective;
import net.avicus.atlas.module.objectives.Objective;
import net.avicus.atlas.module.objectives.ObjectivesModule;
import net.avicus.atlas.module.objectives.TouchableObjective;

/**
 * An objective check checks to see if an objective is complete.
 */
@ToString
public class ObjectiveCheck implements Check {

  private final WeakReference<Objective> objective;
  private final Optional<WeakReference<Team>> team;
  private final CheckType type;

  public ObjectiveCheck(WeakReference<Objective> objective, Optional<WeakReference<Team>> team,
      CheckType type) {
    this.objective = objective;
    this.team = team;
    this.type = type;
  }

  @Override
  public CheckResult test(CheckContext context) {
    Optional<Objective> optional = this.objective.getObject();

    ObjectivesModule module = context.getMatch().getRequiredModule(ObjectivesModule.class);

    if (optional.isPresent()) {
      Objective objective = optional.get();

      if (this.team.isPresent()) {
        Optional<Team> team = this.team.get().getObject();
        if (team.isPresent()) {
          switch (this.type) {
            case COMPLETED:
              return CheckResult.valueOf(objective.isCompleted(team.get()));
            case TOUCHED:
              if (objective instanceof TouchableObjective) {
                return CheckResult.valueOf(((TouchableObjective) objective).hasTouched(team.get()));
              }
            case CAPTURED:
              CheckResult res = CheckResult.IGNORE;
              for (ObjectivesModuleBridge objectiveBridge : module.getBridges().values()) {
                if (res != CheckResult.IGNORE) {
                  break;
                }
                res = objectiveBridge.performCaptureCheck(objective, team.get());
              }
              return res;
          }
        }
      } else if (objective instanceof GlobalObjective) {
        switch (this.type) {
          case COMPLETED:
            return CheckResult.valueOf(((GlobalObjective) objective).isCompleted());
          case TOUCHED:
            if (objective instanceof TouchableObjective) {
              return CheckResult.valueOf(((TouchableObjective) objective).isTouched());
            }
          case CAPTURED:
            CheckResult res = CheckResult.IGNORE;
            for (ObjectivesModuleBridge objectiveBridge : module.getBridges().values()) {
              if (res != CheckResult.IGNORE) {
                break;
              }
              res = objectiveBridge.performCaptureCheck(objective);
            }
            return res;
        }
      }
    }

    return CheckResult.IGNORE;
  }

  public enum CheckType {
    COMPLETED,
    CAPTURED,
    TOUCHED
  }
}
