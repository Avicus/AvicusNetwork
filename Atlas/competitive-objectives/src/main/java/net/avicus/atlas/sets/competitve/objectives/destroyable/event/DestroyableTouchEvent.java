package net.avicus.atlas.sets.competitve.objectives.destroyable.event;

import net.avicus.atlas.event.objective.ObjectiveTouchEvent;
import net.avicus.atlas.sets.competitve.objectives.destroyable.DestroyableEventInfo;
import net.avicus.atlas.sets.competitve.objectives.destroyable.DestroyableObjective;
import org.bukkit.event.HandlerList;

public class DestroyableTouchEvent extends ObjectiveTouchEvent {

  private static final HandlerList handlers = new HandlerList();

  public DestroyableTouchEvent(DestroyableObjective objective, DestroyableEventInfo info) {
    super(objective, info.getActor());
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}
