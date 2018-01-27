package net.avicus.atlas.sets.competitve.objectives.destroyable.leakable.event;

import lombok.Getter;
import net.avicus.atlas.event.objective.ObjectiveCompleteEvent;
import net.avicus.atlas.sets.competitve.objectives.destroyable.DestroyableEventInfo;
import net.avicus.atlas.sets.competitve.objectives.destroyable.leakable.LeakableObjective;
import org.bukkit.event.HandlerList;

public class LeakableLeakEvent extends ObjectiveCompleteEvent {

  private static final HandlerList handlers = new HandlerList();

  @Getter
  private final DestroyableEventInfo info;

  public LeakableLeakEvent(LeakableObjective objective, DestroyableEventInfo info) {
    super(objective, info.getActor());
    this.info = info;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}
