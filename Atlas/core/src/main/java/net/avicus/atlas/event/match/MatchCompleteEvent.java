package net.avicus.atlas.event.match;

import java.util.Collection;
import lombok.Getter;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.Competitor;
import org.bukkit.event.HandlerList;

/**
 * An event that is fired when a {@link Match} is completed with winner(s).
 * This is not called during ties.
 */
public class MatchCompleteEvent extends MatchEvent {

  /**
   * Event handlers.
   */
  private static final HandlerList handlers = new HandlerList();

  @Getter
  private final Collection<? extends Competitor> competitors;
  @Getter
  private final Collection<Competitor> winners;

  /**
   * Constructor.
   *
   * @param match match that was opened
   * @param competitors who played in the match.
   * @param winners who won the match.
   */
  public MatchCompleteEvent(Match match, Collection<? extends Competitor> competitors,
      Collection<Competitor> winners) {
    super(match);
    this.competitors = competitors;
    this.winners = winners;
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
