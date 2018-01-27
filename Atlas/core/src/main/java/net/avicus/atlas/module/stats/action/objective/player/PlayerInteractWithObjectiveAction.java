package net.avicus.atlas.module.stats.action.objective.player;

import java.time.Instant;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.module.objectives.Objective;
import net.avicus.atlas.module.stats.action.base.PlayerAction;
import net.avicus.atlas.module.stats.action.objective.ObjectiveAction;
import org.bukkit.entity.Player;

@ToString(callSuper = true)
public abstract class PlayerInteractWithObjectiveAction extends ObjectiveAction implements
    PlayerAction {

  @Getter
  private final Player actor;

  public PlayerInteractWithObjectiveAction(Objective acted, Player actor, Instant when) {
    super(acted, when);
    this.actor = actor;
  }
}
