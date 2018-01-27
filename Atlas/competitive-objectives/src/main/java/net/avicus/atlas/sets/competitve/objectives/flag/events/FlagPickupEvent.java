package net.avicus.atlas.sets.competitve.objectives.flag.events;

import lombok.Getter;
import net.avicus.atlas.event.objective.ObjectiveStateChangeEvent;
import net.avicus.atlas.sets.competitve.objectives.flag.FlagObjective;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class FlagPickupEvent extends ObjectiveStateChangeEvent {

  private static final HandlerList handlers = new HandlerList();
  @Getter
  private final Player player;

  public FlagPickupEvent(FlagObjective objective, Player player) {
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
