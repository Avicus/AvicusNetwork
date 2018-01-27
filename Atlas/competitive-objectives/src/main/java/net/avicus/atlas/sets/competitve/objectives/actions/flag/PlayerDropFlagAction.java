package net.avicus.atlas.sets.competitve.objectives.actions.flag;

import com.google.common.base.Preconditions;
import java.time.Instant;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.module.objectives.Objective;
import net.avicus.atlas.module.stats.action.objective.player.PlayerInteractWithObjectiveAction;
import net.avicus.atlas.sets.competitve.objectives.flag.FlagObjective;
import net.avicus.atlas.util.Translations;
import net.avicus.compendium.locale.text.LocalizedFormat;
import org.bukkit.entity.Player;

@ToString
public class PlayerDropFlagAction extends PlayerInteractWithObjectiveAction implements FlagAction {

  @Getter
  private final FlagObjective flag;

  public PlayerDropFlagAction(Objective acted, Player actor, Instant when) {
    super(acted, actor, when);
    Preconditions.checkArgument(acted instanceof FlagObjective, "Objective must be a flag.");
    this.flag = (FlagObjective) acted;
  }

  @Override
  public double getScore() {
    return -4.5;
  }

  @Override
  public String getDebugMessage() {
    return "Flag Drop: " + flag.getName().translateDefault();
  }

  @Override
  public LocalizedFormat actionMessage(boolean plural) {
    if (plural) {
      return Translations.STATS_OBJECTIVES_FLAGS_DROPPEDPLURAL;
    }
    return Translations.STATS_OBJECTIVES_FLAGS_DROPPED;
  }
}
