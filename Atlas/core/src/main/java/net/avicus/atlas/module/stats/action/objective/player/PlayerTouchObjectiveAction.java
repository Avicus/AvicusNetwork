package net.avicus.atlas.module.stats.action.objective.player;

import java.time.Instant;
import lombok.ToString;
import net.avicus.atlas.module.objectives.Objective;
import net.avicus.atlas.module.stats.action.base.PlayerAction;
import org.bukkit.entity.Player;

@ToString(callSuper = true)
public abstract class PlayerTouchObjectiveAction extends
    PlayerInteractWithObjectiveAction implements PlayerAction {

  private final boolean helpful;

  public PlayerTouchObjectiveAction(Objective acted, Player actor, Instant when, boolean helpful) {
    super(acted, actor, when);
    this.helpful = helpful;
  }
}
