package net.avicus.atlas.module.elimination.event;

import lombok.Getter;
import net.avicus.atlas.module.groups.teams.Team;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TeamEliminateEvent extends Event {

  private static final HandlerList handlers = new HandlerList();
  @Getter
  private final Team team;

  public TeamEliminateEvent(Team team) {
    this.team = team;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public HandlerList getHandlers() {
    return handlers;
  }
}
