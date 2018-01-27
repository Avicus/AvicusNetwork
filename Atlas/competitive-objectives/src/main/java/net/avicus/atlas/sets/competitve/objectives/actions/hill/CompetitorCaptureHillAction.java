package net.avicus.atlas.sets.competitve.objectives.actions.hill;

import com.google.common.base.Preconditions;
import java.time.Instant;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.objectives.Objective;
import net.avicus.atlas.module.stats.action.objective.competitor.CompetitorCompleteObjectiveAction;
import net.avicus.atlas.sets.competitve.objectives.hill.HillObjective;
import net.avicus.atlas.util.Translations;
import net.avicus.compendium.locale.text.LocalizedFormat;

@ToString
public class CompetitorCaptureHillAction extends CompetitorCompleteObjectiveAction implements
    HillAction {

  @Getter
  private final HillObjective hill;

  public CompetitorCaptureHillAction(Objective acted, Competitor actor, Instant when) {
    super(acted, actor, when);
    Preconditions.checkArgument(acted instanceof HillObjective, "Objective must be a hill.");
    this.hill = (HillObjective) acted;
  }

  @Override
  public double getScore() {
    return 0;
  }

  @Override
  public String getDebugMessage() {
    return "Capture Kill (Competitor)";
  }

  @Override
  public LocalizedFormat actionMessage(boolean plural) {
    if (plural) {
      return Translations.STATS_OBJECTIVES_HILLS_CAPTUREPLURAL;
    }
    return Translations.STATS_OBJECTIVES_HILLS_CAPTURE;
  }
}
