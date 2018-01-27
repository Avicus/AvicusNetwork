package net.avicus.atlas.sets.competitve.objectives.hill.event;

import lombok.Getter;
import net.avicus.atlas.event.objective.ObjectiveStateChangeEvent;
import net.avicus.atlas.sets.competitve.objectives.hill.HillObjective;
import org.bukkit.event.HandlerList;

public class HillCompletionChangeEvent extends ObjectiveStateChangeEvent {

  private static final HandlerList handlers = new HandlerList();
  @Getter
  private final double oldCompletion;
  @Getter
  private final double newCompletion;

  public HillCompletionChangeEvent(HillObjective objective, double oldCompletion,
      double newCompletion) {
    super(objective);
    this.oldCompletion = oldCompletion;
    this.newCompletion = newCompletion;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}
