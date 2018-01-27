package net.avicus.atlas.sets.competitve.objectives.flag.events;

import net.avicus.atlas.event.objective.ObjectiveCompleteEvent;
import net.avicus.atlas.sets.competitve.objectives.flag.FlagObjective;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class FlagCaptureEvent extends ObjectiveCompleteEvent {

  private static final HandlerList handlers = new HandlerList();

  public FlagCaptureEvent(Player player, FlagObjective objective) {
    super(objective, player);
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}
