package net.avicus.atlas.sets.competitve.objectives.actions.destroyable.monument;

import com.google.common.base.Preconditions;
import java.time.Instant;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.module.objectives.Objective;
import net.avicus.atlas.module.states.StatesModule;
import net.avicus.atlas.module.stats.action.objective.player.PlayerCompleteObjectiveAction;
import net.avicus.atlas.sets.competitve.objectives.destroyable.DestroyableEventInfo;
import net.avicus.atlas.sets.competitve.objectives.destroyable.DestroyableObjective;
import net.avicus.atlas.sets.competitve.objectives.destroyable.monument.MonumentObjective;
import net.avicus.atlas.util.Translations;
import net.avicus.compendium.locale.text.LocalizedFormat;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

@ToString
public class PlayerDestroyMonumentAction extends PlayerCompleteObjectiveAction implements
    MonumentAction {

  @Getter
  private final MonumentObjective monument;
  @Getter
  private final DestroyableEventInfo info;
  @Getter
  private final double score;
  @Getter
  private String debugMessage;

  public PlayerDestroyMonumentAction(Objective acted, DestroyableEventInfo info, Instant when) {
    super(acted, info.getActor(), when);
    Preconditions
        .checkArgument(acted instanceof MonumentObjective, "Objective must be a monument.");
    this.monument = (MonumentObjective) acted;
    this.info = info;
    Pair<StringBuilder, Double> calc = calcScore();
    this.score = calc.getValue();
    this.debugMessage = calc.getKey().toString();
  }

  @Override
  public DestroyableObjective getDestroyable() {
    return getMonument();
  }

  private Pair<StringBuilder, Double> calcScore() {
    Pair<StringBuilder, Double> res = MutablePair.of(new StringBuilder("Monument Destroy: "), 0.0);
    long playTime = Atlas.getMatch().getRequiredModule(StatesModule.class).getTotalPlayingDuration()
        .getStandardMinutes();
    double score = 0;
    if (this.info.isByHand()) {
      Pair<StringBuilder, Double> bc = getBreakScore(this.info.getTool(), this.info.getBroken(),
          this.info.getActor());
      res.getKey().append("byHand=true data=" + bc.getKey().toString() + " ");
      score = bc.getValue();
    } else {
      score = 9.1;
    }
    res.setValue(4.1 + ((playTime / 3.6)) * score);
    res.getKey().append("score=" + res.getValue() + " ");
    return res;
  }

  @Override
  public LocalizedFormat actionMessage(boolean plural) {
    return Translations.STATS_OBJECTIVES_MONUMENTS_DESTROYED;
  }
}
