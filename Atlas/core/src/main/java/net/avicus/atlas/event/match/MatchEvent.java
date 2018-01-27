package net.avicus.atlas.event.match;

import lombok.Getter;
import net.avicus.atlas.match.Match;
import org.bukkit.event.Event;

/**
 * An event that is the superclass to all events that happen inside of matches.
 */
public abstract class MatchEvent extends Event {

  /**
   * Match that the event occurred in.
   */
  @Getter
  final Match match;

  /**
   * Constructor.
   *
   * @param match match that the event occurred in
   */
  protected MatchEvent(Match match) {
    this.match = match;
  }
}
