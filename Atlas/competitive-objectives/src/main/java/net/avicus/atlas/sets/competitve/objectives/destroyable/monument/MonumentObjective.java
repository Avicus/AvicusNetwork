package net.avicus.atlas.sets.competitve.objectives.destroyable.monument;

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.GroupVariable;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.groups.teams.Team;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.module.objectives.TouchableDistanceMetrics;
import net.avicus.atlas.sets.competitve.objectives.destroyable.DestroyableObjective;
import net.avicus.atlas.sets.competitve.objectives.phases.DestroyablePhase;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.inventory.MultiMaterialMatcher;
import net.avicus.compendium.inventory.SingleMaterialMatcher;
import net.avicus.compendium.locale.text.LocalizedFormat;
import net.avicus.magma.util.region.BoundedRegion;

@Getter
@ToString(exclude = "match")
public class MonumentObjective extends DestroyableObjective {

  private final Match match;
  @Setter
  private LocalizedXmlString name;
  private final Optional<Team> owner;

  public MonumentObjective(Match match, TouchableDistanceMetrics metrics, LocalizedXmlString name,
      Optional<Team> owner, BoundedRegion region, Optional<Integer> points,
      Optional<Integer> pointsPerBlock, MultiMaterialMatcher materials, boolean destroyable,
      boolean enforceAntiRepair, boolean repairable, boolean fireworks, Optional<Check> breakCheck,
      Optional<Check> repairCheck, Optional<SingleMaterialMatcher> completedState,
      double neededCompletion, boolean anyRepair, Optional<DestroyablePhase> phase) {
    super(match, metrics, region, points, pointsPerBlock, materials, destroyable, repairable,
        enforceAntiRepair, fireworks, breakCheck, repairCheck, completedState, neededCompletion,
        anyRepair, phase);
    this.match = match;
    this.name = name;
    this.owner = owner;
  }

  public boolean isOwner(Group group) {
    return this.owner.isPresent() && this.owner.get().equals(group);
  }

  @Override
  public boolean canComplete(Competitor competitor) {
    if (this.getBreakCheck().isPresent() && competitor.getGroup() instanceof Team) {
      Team team = (Team) competitor.getGroup();
      CheckContext context = new CheckContext(this.match);
      context.add(new GroupVariable(team));
      if (this.getBreakCheck().get().test(context).fails()) {
        return false;
      }
    }
    return !this.owner.isPresent() || !isOwner(competitor.getGroup());
  }

  @Override
  public boolean isIncremental() {
    return this.getOriginals().size() > 1;
  }

  @Override
  public boolean isCompleted() {
    if (getOwner().isPresent()) {
      return getCompletion() >= this.getNeededCompletion();
    } else if (getCompletion() >= this.getNeededCompletion()) {
      return true;
    } else if (getHighestCompleter().isPresent()) {
      return getCompletion(getHighestCompleter().get()) >= this.getNeededCompletion();
    } else {
      return false;
    }
  }

  @Override
  public LocalizedFormat getTouchMessage() {
    return Messages.GENERIC_OBJECTIVE_TOUCHED;
  }
}
