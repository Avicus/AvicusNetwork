package net.avicus.atlas.sets.competitve.objectives.destroyable.event;

import lombok.Getter;
import net.avicus.atlas.event.objective.ObjectiveStateChangeEvent;
import net.avicus.atlas.sets.competitve.objectives.destroyable.DestroyableObjective;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class DestroyableRepairEvent extends ObjectiveStateChangeEvent {

  private static final HandlerList handlers = new HandlerList();
  @Getter
  private final Player player;

  public DestroyableRepairEvent(DestroyableObjective objective, Player player) {
    super(objective);
    this.player = player;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}
