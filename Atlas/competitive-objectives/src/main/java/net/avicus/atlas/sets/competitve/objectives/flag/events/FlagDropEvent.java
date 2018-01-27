package net.avicus.atlas.sets.competitve.objectives.flag.events;

import javax.annotation.Nullable;
import lombok.Getter;
import net.avicus.atlas.event.objective.ObjectiveStateChangeEvent;
import net.avicus.atlas.module.objectives.Objective;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class FlagDropEvent extends ObjectiveStateChangeEvent {

  private static final HandlerList handlers = new HandlerList();

  @Getter
  @Nullable
  private final Player dropper;

  public FlagDropEvent(Objective objective, Player dropper) {
    super(objective);
    this.dropper = dropper;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}
