package net.avicus.atlas.event.objective;

import java.util.Collections;
import java.util.List;
import lombok.Getter;
import net.avicus.atlas.module.objectives.Objective;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * An event that is the superclass to all events that are related to the completion of objectives.
 */
public abstract class ObjectiveCompleteEvent extends ObjectiveEvent {

  private static final HandlerList handlers = new HandlerList();

  /**
   * List of players who completed the event.
   */
  @Getter
  private final List<Player> players;

  /**
   * Constructor.
   *
   * @param objective objective involved in the event
   * @param players list of players who completed the objective
   */
  public ObjectiveCompleteEvent(Objective objective, List<Player> players) {
    super(objective);
    this.players = players;
  }

  /**
   * Constructor (one winner)
   *
   * @param objective objective involved in the event
   * @param player player who completed the objective
   */
  public ObjectiveCompleteEvent(Objective objective, Player player) {
    this(objective, Collections.singletonList(player));
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}
