package net.avicus.atlas.sets.competitve.objectives.actions.wool;

import static net.avicus.atlas.module.stats.action.ScoreUtils.getNearbyPlayers;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.AtomicDouble;
import java.time.Instant;
import java.util.HashMap;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.objectives.Objective;
import net.avicus.atlas.module.stats.action.objective.player.PlayerTouchObjectiveAction;
import net.avicus.atlas.sets.competitve.objectives.wool.WoolObjective;
import net.avicus.atlas.util.Translations;
import net.avicus.compendium.locale.text.LocalizedFormat;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.entity.Player;

@ToString
public class PlayerTouchWoolAction extends PlayerTouchObjectiveAction implements WoolAction {

  @Getter
  private final WoolObjective wool;
  @Getter
  private final double score;
  @Getter
  private String debugMessage;

  public PlayerTouchWoolAction(Objective acted, Player actor, Instant when, boolean helpful) {
    super(acted, actor, when, helpful);
    Preconditions.checkArgument(acted instanceof WoolObjective, "Objective must be a wool.");
    this.wool = (WoolObjective) acted;
    Pair<StringBuilder, Double> calc = calcScore();
    this.score = calc.getValue();
    this.debugMessage = calc.getKey().toString();
  }

  private Pair<StringBuilder, Double> calcScore() {
    StringBuilder stringBuilder = new StringBuilder(
        "Touch Wool: " + wool.getName().translateDefault() + " ");
    GroupsModule module = Atlas.getMatch().getRequiredModule(GroupsModule.class);

    Pair<StringBuilder, HashMap<Integer, AtomicDouble>> nearby = getNearbyPlayers(
        getActor().getLocation(), module.getCompetitorOf(getActor()).get(), module);
    double in5 = nearby.getValue().get(5).get();
    double in10 = nearby.getValue().get(10).get();
    double in15 = nearby.getValue().get(15).get();
    double in20 = nearby.getValue().get(20).get();
    double val =
        4.3 + (this.getActor().getHealth() * .42) + (in5 * .9) + (in10 * .5) + (in15 * .25) + (in20
            * .2);
    stringBuilder.append(
        "in5=" + in5 + " in10=" + in10 + " in15=" + in15 + " in20=" + in20 + " data=" + nearby
            .getKey().toString());
    return MutablePair.of(stringBuilder, val);
  }

  @Override
  public LocalizedFormat actionMessage(boolean plural) {
    if (plural) {
      return Translations.STATS_OBJECTIVES_WOOLS_TOUCHEDPLURAL;
    }
    return Translations.STATS_OBJECTIVES_WOOLS_TOUCHED;
  }
}
