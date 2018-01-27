package net.avicus.atlas.sets.competitve.objectives.actions.hill;

import com.google.common.base.Preconditions;
import java.time.Instant;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.objectives.Objective;
import net.avicus.atlas.module.stats.action.ScoreUtils;
import net.avicus.atlas.module.stats.action.objective.player.PlayerInteractWithObjectiveAction;
import net.avicus.atlas.sets.competitve.objectives.hill.HillObjective;
import net.avicus.atlas.util.Translations;
import net.avicus.compendium.locale.text.LocalizedFormat;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.entity.Player;

@ToString
public class PlayerCaptureHillAssistAction extends PlayerInteractWithObjectiveAction implements
    HillAction {

  @Getter
  private final HillObjective hill;
  @Getter
  private final double score;
  @Getter
  private final String debugMessage;

  public PlayerCaptureHillAssistAction(Objective acted, Player actor, Instant when) {
    super(acted, actor, when);
    Preconditions.checkArgument(acted instanceof HillObjective, "Objective must be a hill.");
    this.hill = (HillObjective) acted;
    Pair<StringBuilder, Double> calc = calcScore();
    this.score = calc.getValue();
    this.debugMessage = calc.getKey().toString();
  }

  private Pair<StringBuilder, Double> calcScore() {
    Pair<StringBuilder, Double> enemiesOnHill = MutablePair
        .of(new StringBuilder("Hill Capture "), 0.0);
    Competitor playerComp = hill.getMatch().getRequiredModule(GroupsModule.class)
        .getCompetitorOf(this.getActor()).get();
    hill.getCapturingPlayers().entries().forEach(e -> {
      if (e.getKey().equals(playerComp)) {
        return; // next
      }

      Pair<StringBuilder, Double> toughnessScore = ScoreUtils.getEntityToughnessScore(e.getValue());
      enemiesOnHill.setValue(enemiesOnHill.getValue() + toughnessScore.getValue());
      enemiesOnHill.getKey().append(toughnessScore.getKey().toString());
    });
    enemiesOnHill.setValue(4.2 + (enemiesOnHill.getValue() / 3.2) * (getActor().getHealth() / 3.2));
    enemiesOnHill.getKey().append("score=" + enemiesOnHill.getValue());
    return enemiesOnHill;
  }

  @Override
  public LocalizedFormat actionMessage(boolean plural) {
    if (plural) {
      return Translations.STATS_OBJECTIVES_HILLS_CAPTUREASSISTPLURAL;
    }

    return Translations.STATS_OBJECTIVES_HILLS_CAPTUREASSIST;
  }
}
