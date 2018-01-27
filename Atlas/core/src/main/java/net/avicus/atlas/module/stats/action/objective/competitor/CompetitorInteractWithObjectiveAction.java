package net.avicus.atlas.module.stats.action.objective.competitor;

import java.time.Instant;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.objectives.Objective;
import net.avicus.atlas.module.stats.action.base.CompetitorAction;
import net.avicus.atlas.module.stats.action.objective.ObjectiveAction;

@ToString(callSuper = true)
public abstract class CompetitorInteractWithObjectiveAction extends ObjectiveAction implements
    CompetitorAction {

  @Getter
  private final Competitor actor;

  public CompetitorInteractWithObjectiveAction(Objective acted, Competitor actor, Instant when) {
    super(acted, when);
    this.actor = actor;
  }
}
