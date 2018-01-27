package net.avicus.atlas.sets.competitve.objectives.hill.event;

import java.util.List;
import java.util.Optional;
import lombok.Getter;
import net.avicus.atlas.event.objective.ObjectiveCompleteEvent;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.sets.competitve.objectives.hill.HillObjective;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class HillCaptureEvent extends ObjectiveCompleteEvent {

  private static final HandlerList handlers = new HandlerList();
  @Getter
  private final Optional<Competitor> newOwner;

  public HillCaptureEvent(List<Player> players, HillObjective objective,
      Optional<Competitor> newOwner) {
    super(objective, players);
    this.newOwner = newOwner;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}
