package net.avicus.atlas.event.player;

import lombok.Getter;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.spawns.Spawn;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * An event that is fired when a player spawns into a {@link net.avicus.atlas.match.Match}.
 * Event is called before they are teleported or given a loadout.
 */
public class PlayerSpawnBeginEvent extends PlayerEvent {

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
  public PlayerSpawnBeginEvent(Player player, Group group, Spawn spawn, boolean giveLoadout,
      boolean teleportPlayer) {
    super(player);
    this.group = group;
    this.spawn = spawn;
    this.giveLoadout = giveLoadout;
    this.teleportPlayer = teleportPlayer;
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
