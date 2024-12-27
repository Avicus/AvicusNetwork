package net.avicus.atlas.sets.competitve.objectives.actions.flag;

import com.google.common.base.Preconditions;
import java.time.Instant;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.module.objectives.Objective;
import net.avicus.atlas.module.stats.action.objective.player.PlayerTouchObjectiveAction;
import net.avicus.atlas.sets.competitve.objectives.flag.FlagObjective;
import net.avicus.atlas.util.Translations;
import net.avicus.compendium.locale.text.LocalizedFormat;
import org.bukkit.entity.Player;

@ToString
public class PlayerPickupFlagAction extends PlayerTouchObjectiveAction implements FlagAction {

  @Getter
  private final FlagObjective flag;

  public PlayerPickupFlagAction(Objective acted, Player actor, Instant when) {
    super(acted, actor, when, false);
    Preconditions.checkArgument(acted instanceof FlagObjective, "Objective must be a flag.");
    this.flag = (FlagObjective) acted;
  }

  @Override
  public double getScore() {
    return 4.3;
  }

  @Override
  public String getDebugMessage() {
    return "Flag Pickup: " + flag.getName().renderDefault();
  }

  @Override
  public LocalizedFormat actionMessage(boolean plural) {
    if (plural) {
      return Translations.STATS_OBJECTIVES_FLAGS_PICKEDPLURAL;
    }

    return Translations.STATS_OBJECTIVES_FLAGS_PICKED;
  }
}
