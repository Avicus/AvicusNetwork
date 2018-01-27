package net.avicus.atlas.sets.competitve.objectives.hill.event;

import java.util.List;
import java.util.Optional;
import lombok.Getter;
import net.avicus.atlas.event.objective.ObjectiveStateChangeEvent;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.objectives.Objective;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class HillOwnerChangeEvent extends ObjectiveStateChangeEvent {

  private static final HandlerList handlers = new HandlerList();
  @Getter
  private final List<Player> players;
  @Getter
  private final Optional<Competitor> oldOwner;
  @Getter
  private final Optional<Competitor> newOwner;

  public HillOwnerChangeEvent(Objective objective, List<Player> players,
      Optional<Competitor> oldOwner, Optional<Competitor> newOwner) {
    super(objective);
    this.players = players;
    this.oldOwner = oldOwner;
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
