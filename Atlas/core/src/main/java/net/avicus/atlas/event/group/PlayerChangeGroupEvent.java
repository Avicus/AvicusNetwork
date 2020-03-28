package net.avicus.atlas.event.group;

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import net.avicus.atlas.module.groups.Group;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * An event that is fired when a player attempts to change to a {@link Group}
 */
public class PlayerChangeGroupEvent extends PlayerEvent implements Cancellable {

  /**
   * Event handlers.
   */
  private static final HandlerList handlers = new HandlerList();
  /**
   * If the event was canceled.
   */
  @Getter
  @Setter
  boolean cancelled;
  /**
   * Group the player is changing from.
   */
  @Getter
  private Optional<Group> groupFrom;
  /**
   * Group the player is transitioning to.
   */
  @Getter
  @Setter
  private Group group;
  /**
   * If the player should be (re)spawned in this group's spawn location. (Also gives loadout)
   */
  @Getter
  @Setter
  private boolean spawnTriggered;
  /**
   * If the player should be teleported to this group's spawn location.
   */
  @Getter
  private boolean teleportTriggered;

  /**
   * If this change was forced by someone with special permissions
   */
  @Getter
  private boolean force;

  /**
   * Constructor.
   *
   * @param player player that is changing groups
   * @param groupFrom group the player is changing from
   * @param group group the player is transitioning to
   * @param triggerSpawn if the player should be (re)spawned in this group's spawn location
   * @param triggerTeleport if the player should be teleported to this group's spawn location
   * @param force if this change was forced by someone with special permissions
   */
  public PlayerChangeGroupEvent(Player player, Optional<Group> groupFrom, Group group,
      boolean triggerSpawn, boolean triggerTeleport, boolean force) {
    super(player);
    this.groupFrom = groupFrom;
    this.group = group;
    this.spawnTriggered = triggerSpawn;
    this.teleportTriggered = triggerTeleport;
    this.force = force;
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
   * Set if the event should trigger a player teleport.
   * <p>
   * <p>NOTE: A spawn must be triggered in order to trigger teleporation.</p>
   *
   * @param triggerTeleport if the event should trigger a player teleport
   */
  public void setTeleportTriggered(boolean triggerTeleport) {
    if (!spawnTriggered && triggerTeleport) {
      throw new RuntimeException("cannot trigger teleport when spawn is not triggered");
    }
    this.teleportTriggered = triggerTeleport;
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
