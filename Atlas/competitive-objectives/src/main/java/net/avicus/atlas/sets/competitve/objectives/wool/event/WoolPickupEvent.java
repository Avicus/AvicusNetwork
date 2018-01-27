package net.avicus.atlas.sets.competitve.objectives.wool.event;

import net.avicus.atlas.event.objective.ObjectiveTouchEvent;
import net.avicus.atlas.sets.competitve.objectives.wool.WoolObjective;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class WoolPickupEvent extends ObjectiveTouchEvent {

  private static final HandlerList handlers = new HandlerList();

  public WoolPickupEvent(WoolObjective objective, Player player) {
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
