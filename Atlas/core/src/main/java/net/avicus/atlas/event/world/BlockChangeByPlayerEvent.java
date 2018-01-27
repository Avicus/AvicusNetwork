package net.avicus.atlas.event.world;

import lombok.Getter;
import lombok.ToString;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * An event that is fired when any block in the world is changed by a player.
 *
 * @param <T> type of the event that caused this event to fire.
 */
@ToString(callSuper = true)
public class BlockChangeByPlayerEvent<T extends Event> extends BlockChangeEvent {

  /**
   * Event handlers.
   */
  private static final HandlerList handlers = new HandlerList();
  @Getter
  private final Player player;

  public BlockChangeByPlayerEvent(Block block, T cause, BlockState oldState, BlockState newState,
      Player player) {
    super(block, cause, oldState, newState);
    this.player = player;
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
