package net.avicus.atlas.sets.competitve.objectives.actions.wool;

import com.google.common.base.Preconditions;
import java.time.Instant;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.module.objectives.Objective;
import net.avicus.atlas.module.stats.action.objective.player.PlayerCompleteObjectiveAction;
import net.avicus.atlas.sets.competitve.objectives.wool.WoolObjective;
import net.avicus.atlas.util.Translations;
import net.avicus.compendium.locale.text.LocalizedFormat;
import org.bukkit.entity.Player;

@ToString
public class PlayerPlaceWoolAction extends PlayerCompleteObjectiveAction implements WoolAction {

  @Getter
  private final WoolObjective wool;
  @Getter
  private final double score;

  public PlayerPlaceWoolAction(Objective acted, Player actor, Instant when) {
    super(acted, actor, when);
    Preconditions.checkArgument(acted instanceof WoolObjective, "Objective must be a wool.");
    this.wool = (WoolObjective) acted;
    this.score = 9.41 * (actor.getHealth() / 6.3);
  }

  @Override
  public String getDebugMessage() {
    return "Place Wool: " + wool.getName().translateDefault();
  }

  @Override
  public LocalizedFormat actionMessage(boolean plural) {
    if (plural) {
      return Translations.STATS_OBJECTIVES_WOOLS_PLACEDPLURAL;
    }
    return Translations.STATS_OBJECTIVES_WOOLS_PLACED;
  }
}
