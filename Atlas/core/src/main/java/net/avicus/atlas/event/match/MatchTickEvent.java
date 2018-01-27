package net.avicus.atlas.event.match;

import lombok.Getter;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.states.State;
import org.bukkit.event.HandlerList;

/**
 * A container event that is called on every heartbeat and holds a reference to the currently
 * running {@link Match}
 */
public class MatchTickEvent extends MatchEvent {

  /**
   * Event handlers.
   */
  private static final HandlerList handlers = new HandlerList();
  /**
   * State of the match.
   */
  @Getter
  private final State state;

  /**
   * Constructor.
   *
   * @param match match that the event was fired from
   * @param state state of the match
   */
  public MatchTickEvent(Match match, State state) {
    super(match);
    this.state = state;
  }

  /**
   * Get the handlers of the event.
   *
   * @return the handlers of the event
   */
  public static HandlerList getHandlerList() {
    return handlers;
  }

  /**
   * Get the handlers of the event.
   *
   * @return the handlers of the event
   */
  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}
