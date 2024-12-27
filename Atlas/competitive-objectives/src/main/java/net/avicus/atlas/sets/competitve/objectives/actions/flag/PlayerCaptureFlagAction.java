package net.avicus.atlas.sets.competitve.objectives.actions.flag;

import com.google.common.base.Preconditions;
import java.time.Instant;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.module.objectives.Objective;
import net.avicus.atlas.module.stats.action.objective.player.PlayerCompleteObjectiveAction;
import net.avicus.atlas.sets.competitve.objectives.flag.FlagObjective;
import net.avicus.atlas.util.Translations;
import net.avicus.compendium.locale.text.LocalizedFormat;
import org.bukkit.entity.Player;

@ToString
public class PlayerCaptureFlagAction extends PlayerCompleteObjectiveAction implements FlagAction {

  @Getter
  private final FlagObjective flag;
  @Getter
  private final double score;

  public PlayerCaptureFlagAction(Objective acted, Player actor, Instant when) {
    super(acted, actor, when);
    Preconditions.checkArgument(acted instanceof FlagObjective, "Objective must be a flag.");
    this.flag = (FlagObjective) acted;
    this.score = 5.1 * (actor.getHealth() / 6.3);
  }

  @Override
  public String getDebugMessage() {
    return "Flag Capture: " + flag.getName().renderDefault();
  }

  @Override
  public LocalizedFormat actionMessage(boolean plural) {
    if (plural) {
      return Translations.STATS_OBJECTIVES_FLAGS_CAPTUREDPLURAL;
    }

    return Translations.STATS_OBJECTIVES_FLAGS_CAPTURED;
  }
}
