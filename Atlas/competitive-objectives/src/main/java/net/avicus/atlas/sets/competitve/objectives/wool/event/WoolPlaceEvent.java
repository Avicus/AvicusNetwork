package net.avicus.atlas.sets.competitve.objectives.wool.event;

import net.avicus.atlas.event.objective.ObjectiveCompleteEvent;
import net.avicus.atlas.sets.competitve.objectives.wool.WoolObjective;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class WoolPlaceEvent extends ObjectiveCompleteEvent {

  private static final HandlerList handlers = new HandlerList();

  public WoolPlaceEvent(WoolObjective objective, Player player) {
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
