package net.avicus.atlas.event.match;

import net.avicus.atlas.match.Match;
import org.bukkit.event.HandlerList;

/**
 * An event that is fired when a {@link Match} is closed.
 */
public class MatchCloseEvent extends MatchEvent {

  /**
   * Event handlers.
   */
  private static final HandlerList handlers = new HandlerList();

  /**
   * Constructor.
   *
   * @param match match that is closed
   */
  public MatchCloseEvent(Match match) {
    super(match);
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
