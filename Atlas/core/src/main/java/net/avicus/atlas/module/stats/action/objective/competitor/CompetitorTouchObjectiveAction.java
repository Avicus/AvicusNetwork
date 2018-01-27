package net.avicus.atlas.module.stats.action.objective.competitor;

import java.time.Instant;
import lombok.ToString;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.objectives.Objective;
import net.avicus.atlas.module.stats.action.base.CompetitorAction;

@ToString(callSuper = true)
public abstract class CompetitorTouchObjectiveAction extends
    CompetitorInteractWithObjectiveAction implements CompetitorAction {

  public CompetitorTouchObjectiveAction(Objective acted, Competitor actor, Instant when) {
    super(acted, actor, when);
  }
}
