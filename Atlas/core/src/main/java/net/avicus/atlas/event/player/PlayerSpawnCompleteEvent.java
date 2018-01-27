package net.avicus.atlas.event.player;

import lombok.Getter;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.spawns.Spawn;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * An event that is fired when a player spawns into a {@link net.avicus.atlas.match.Match}.
 * Event is called after they are teleported and/or given a loadout.
 */
public class PlayerSpawnCompleteEvent extends PlayerEvent {

  /**
   * Event handlers.
   */
  private static final HandlerList handlers = new HandlerList();
  /**
   * Group the player is a member of.
   */
  @Getter
  private final Group group;
  /**
   * Spawn location the player is placed.
   */
  @Getter
  private final Spawn spawn;
  /**
   * If a loadout should be applied to the player.
   */
  @Getter
  private final boolean giveLoadout;
  /**
   * If the player should be teleported.
   */
  @Getter
  private final boolean teleportPlayer;

  /**
   * Constructor.
   *
   * @param player player that is spawning
   * @param group group the player is a member of
   * @param spawn spawn location the player is placed
   * @param giveLoadout if a loadout should be applied to the player
   * @param teleportPlayer if the player should be teleported
   */
  public PlayerSpawnCompleteEvent(PlayerSpawnBeginEvent event) {
    super(event.getPlayer());
    this.group = event.getGroup();
    this.spawn = event.getSpawn();
    this.giveLoadout = event.isGiveLoadout();
    this.teleportPlayer = event.isTeleportPlayer();
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
