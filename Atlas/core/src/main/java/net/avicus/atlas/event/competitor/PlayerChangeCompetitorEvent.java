package net.avicus.atlas.event.competitor;

import java.util.Optional;
import lombok.Getter;
import net.avicus.atlas.module.groups.Competitor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * An event that is fired when a player changes {@link Competitor}
 */
public class PlayerChangeCompetitorEvent extends PlayerEvent {

  /**
   * Event handlers.
   */
  private static final HandlerList handlers = new HandlerList();
  /**
   * Competitor that the player used to be in.
   */
  @Getter
  private Optional<Competitor> competitorFrom;
  /**
   * Competitor that the player is now in.
   */
  @Getter
  private Optional<Competitor> competitorTo;

  /**
   * Constructor
   *
   * @param player player that is changing competitor's
   * @param competitorFrom competitor that the player used to be in
   * @param competitorTo competitor that the player is now in
   */
  public PlayerChangeCompetitorEvent(Player player, Optional<Competitor> competitorFrom,
      Optional<Competitor> competitorTo) {
    super(player);
    this.competitorFrom = competitorFrom;
    this.competitorTo = competitorTo;
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
