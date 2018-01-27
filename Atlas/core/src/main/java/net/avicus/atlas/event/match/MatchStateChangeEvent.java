package net.avicus.atlas.event.match;

import java.util.Optional;
import lombok.Getter;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.states.State;
import org.bukkit.event.HandlerList;

/**
 * An event that is fired when the {@link State} of a {@link Match} changes.
 */
public class MatchStateChangeEvent extends MatchEvent {

  /**
   * Event handlers.
   */
  private static final HandlerList handlers = new HandlerList();
  /**
   * Previous match state.
   */
  @Getter
  private final Optional<State> from;
  /**
   * Match state being changed to.
   */
  @Getter
  private final Optional<State> to;

  /**
   * Constructor.
   *
   * @param match match that the state is being changed in
   * @param from previous match state
   * @param to match state being changed to
   */
  public MatchStateChangeEvent(Match match, Optional<State> from, Optional<State> to) {
    super(match);
    this.from = from;
    this.to = to;
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
   * @return True to reflect a change from a playing state.
   */
  public boolean isFromPlaying() {
    return this.from.isPresent() && this.from.get().isPlaying();
  }

  /**
   * @return True to reflect a change to playing state.
   */
  public boolean isToPlaying() {
    return this.to.isPresent() && this.to.get().isPlaying();
  }

  /**
   * @return True to reflect a change from a non-playing to playing state.
   */
  public boolean isChangeToNotPlaying() {
    return isFromPlaying() && !isToPlaying();
  }

  /**
   * @return True to reflect a change from a playing to non-playing state.
   */
  public boolean isChangeToPlaying() {
    return !isFromPlaying() && isToPlaying();
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
