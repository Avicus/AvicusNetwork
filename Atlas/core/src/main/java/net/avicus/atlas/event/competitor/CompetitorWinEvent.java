package net.avicus.atlas.event.competitor;

import lombok.Getter;
import lombok.Setter;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.Competitor;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * An event that is fired when a {@link Competitor} wins a {@link Match}
 */
public class CompetitorWinEvent extends Event implements Cancellable {

  /**
   * Event handlers.
   */
  private static final HandlerList handlers = new HandlerList();
  /**
   * Match that the competitor won.
   */
  @Getter
  private final Match match;
  /**
   * Competitor that won the match.
   */
  @Getter
  private final Competitor winner;
  /**
   * If the event was canceled.
   */
  @Getter
  @Setter
  private boolean cancelled;

  /**
   * @param match match that the competitor won
   * @param winner competitor that won the match
   */
  public CompetitorWinEvent(Match match, Competitor winner) {
    this.match = match;
    this.winner = winner;
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
