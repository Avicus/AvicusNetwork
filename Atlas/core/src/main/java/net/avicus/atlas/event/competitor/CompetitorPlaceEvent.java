package net.avicus.atlas.event.competitor;

import lombok.Getter;
import lombok.Setter;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.Competitor;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * An event that is fired when a {@link Competitor} places in a {@link Match}
 */
public class CompetitorPlaceEvent extends Event implements Cancellable {

  /**
   * Event handlers.
   */
  private static final HandlerList handlers = new HandlerList();
  /**
   * Match that the competitor placed in.
   */
  @Getter
  private final Match match;
  /**
   * Competitor that has placed.
   */
  @Getter
  private final Competitor competitor;
  /**
   * Ranking of the competitor.
   */
  @Getter
  private final int place;
  /**
   * If the event was canceled.
   */
  @Getter
  @Setter
  private boolean cancelled;

  /**
   * Constructor.
   *
   * @param match match that the competitor placed in
   * @param competitor competitor that has placed
   * @param place ranking of the competitor
   */
  public CompetitorPlaceEvent(Match match, Competitor competitor, int place) {
    this.match = match;
    this.competitor = competitor;
    this.place = place;
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
