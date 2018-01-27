package net.avicus.atlas.sets.competitve.objectives.actions.destroyable.base;

import com.google.common.base.Preconditions;
import java.time.Instant;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.module.objectives.Objective;
import net.avicus.atlas.module.stats.action.objective.player.PlayerTouchObjectiveAction;
import net.avicus.atlas.sets.competitve.objectives.destroyable.DestroyableEventInfo;
import net.avicus.atlas.sets.competitve.objectives.destroyable.DestroyableObjective;
import net.avicus.atlas.util.Translations;
import net.avicus.compendium.locale.text.LocalizedFormat;
import org.bukkit.entity.Player;

@ToString
public class PlayerRepairDestoyableAction extends PlayerTouchObjectiveAction implements
    DestroyableAction {

  @Getter
  private final DestroyableObjective destroyable;

  public PlayerRepairDestoyableAction(Objective acted, Player player, Instant when) {
    super(acted, player, when, true);
    Preconditions
        .checkArgument(acted instanceof DestroyableObjective, "Objective must be a destroyable.");
    this.destroyable = (DestroyableObjective) acted;
  }

  @Override
  public DestroyableEventInfo getInfo() {
    return null;
  }

  @Override
  public double getScore() {
    return 5.3;
  }

  @Override
  public String getDebugMessage() {
    return "Repair Destroyable";
  }

  @Override
  public LocalizedFormat actionMessage(boolean plural) {
    if (plural) {
      return Translations.STATS_OBJECTIVES_DESTROYABLES_REPAIREDPLURAL;
    }
    return Translations.STATS_OBJECTIVES_DESTROYABLES_REPAIRED;
  }
}
