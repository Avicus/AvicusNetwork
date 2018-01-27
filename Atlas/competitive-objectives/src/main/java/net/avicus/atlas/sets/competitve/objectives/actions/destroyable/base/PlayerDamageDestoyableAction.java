package net.avicus.atlas.sets.competitve.objectives.actions.destroyable.base;

import com.google.common.base.Preconditions;
import java.time.Instant;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.module.objectives.Objective;
import net.avicus.atlas.module.states.StatesModule;
import net.avicus.atlas.module.stats.action.objective.player.PlayerTouchObjectiveAction;
import net.avicus.atlas.sets.competitve.objectives.destroyable.DestroyableEventInfo;
import net.avicus.atlas.sets.competitve.objectives.destroyable.DestroyableObjective;
import net.avicus.atlas.util.Translations;
import net.avicus.compendium.locale.text.LocalizedFormat;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

@ToString
public class PlayerDamageDestoyableAction extends PlayerTouchObjectiveAction implements
    DestroyableAction {

  @Getter
  private final DestroyableObjective destroyable;
  @Getter
  private final DestroyableEventInfo info;
  @Getter
  private final double score;
  @Getter
  private final String debugMessage;

  public PlayerDamageDestoyableAction(Objective acted, DestroyableEventInfo info, Instant when) {
    super(acted, info.getActor(), when, false);
    Preconditions
        .checkArgument(acted instanceof DestroyableObjective, "Objective must be a destroyable.");
    this.destroyable = (DestroyableObjective) acted;
    this.info = info;
    Pair<StringBuilder, Double> calc = calcScore();
    this.score = calc.getValue();
    this.debugMessage = calc.getKey().toString();
  }

  private Pair<StringBuilder, Double> calcScore() {
    Pair<StringBuilder, Double> res = MutablePair
        .of(new StringBuilder("Damage Destroyable: "), 0.0);
    long playTime = Atlas.getMatch().getRequiredModule(StatesModule.class).getTotalPlayingDuration()
        .getStandardMinutes();
    double score = 0;
    if (this.info.isByHand()) {
      Pair<StringBuilder, Double> bc = getBreakScore(this.info.getTool(), this.info.getBroken(),
          this.info.getActor());
      score = bc.getValue() / 3.1;
      res.getKey().append("byHand=true data=" + bc.getKey().toString());
    } else {
      score = 5.1;
    }

    res.setValue(2.1 + (playTime / 3.6) * score);
    res.getKey().append("score=" + res.getValue());
    return res;
  }

  @Override
  public LocalizedFormat actionMessage(boolean plural) {
    if (plural) {
      return Translations.STATS_OBJECTIVES_DESTROYABLES_DAMAGEDPLURAL;
    }
    return Translations.STATS_OBJECTIVES_DESTROYABLES_DAMAGED;
  }
}
