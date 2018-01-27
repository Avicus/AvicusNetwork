package net.avicus.atlas.sets.competitve.objectives.flag.events;

import net.avicus.atlas.event.objective.ObjectiveStateChangeEvent;
import net.avicus.atlas.sets.competitve.objectives.flag.FlagObjective;
import org.bukkit.event.HandlerList;

public class FlagRecoverEvent extends ObjectiveStateChangeEvent {

  private static final HandlerList handlers = new HandlerList();

  public FlagRecoverEvent(FlagObjective objective) {
    super(objective);
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}
