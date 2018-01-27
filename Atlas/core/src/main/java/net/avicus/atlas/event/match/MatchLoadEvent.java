package net.avicus.atlas.event.match;

import lombok.Getter;
import lombok.Setter;
import net.avicus.atlas.match.Match;
import org.bukkit.WorldCreator;
import org.bukkit.event.HandlerList;

/**
 * An event that is fired when a {@link Match} is loaded.
 */
public class MatchLoadEvent extends MatchEvent {

  /**
   * Event handlers.
   */
  private static final HandlerList handlers = new HandlerList();
  /**
   * World creator of the match world.
   */
  @Getter
  @Setter
  private WorldCreator creator;

  /**
   * @param match match that is being loaded
   * @param creator world creator of the match world
   */
  public MatchLoadEvent(Match match, WorldCreator creator) {
    super(match);
    this.creator = creator;
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
