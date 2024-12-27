package net.avicus.atlas.module.stats.action.objective.score;

import com.google.common.base.Preconditions;
import java.time.Instant;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.module.objectives.Objective;
import net.avicus.atlas.module.objectives.score.ScoreObjective;
import net.avicus.atlas.module.stats.action.objective.player.PlayerInteractWithObjectiveAction;
import net.avicus.atlas.util.Translations;
import net.avicus.compendium.locale.text.LocalizedFormat;
import org.bukkit.entity.Player;

@ToString
public class PlayerEarnPointAction extends PlayerInteractWithObjectiveAction implements
    ScoreAction {

  @Getter
  private final ScoreObjective scoreObjective;

  public PlayerEarnPointAction(Objective acted, Player actor, Instant when) {
    super(acted, actor, when);
    Preconditions.checkArgument(acted instanceof ScoreObjective, "Objective must be a score.");
    this.scoreObjective = (ScoreObjective) acted;
  }

  @Override
  public double getScore() {
    return 4.2;
  }

  @Override
  public String getDebugMessage() {
    return "Earn Point: " + scoreObjective.getName().renderDefault();
  }

  @Override
  public LocalizedFormat actionMessage(boolean plural) {
    if (plural) {
      return Translations.STATS_OBJECTIVES_POINTS_EARNEDPLURAL;
    }
    return Translations.STATS_OBJECTIVES_POINTS_EARNED;
  }
}
