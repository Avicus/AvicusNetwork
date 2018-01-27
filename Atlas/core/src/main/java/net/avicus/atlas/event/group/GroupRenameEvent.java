package net.avicus.atlas.event.group;

import lombok.Getter;
import lombok.Setter;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * An event that is fired when a {@link Group} is being renamed.
 */
public class GroupRenameEvent extends Event implements Cancellable {

  /**
   * Event handlers.
   */
  private static final HandlerList handlers = new HandlerList();
  /**
   * Group that is being renamed.
   */
  @Getter
  private final Group group;
  /**
   * The new name of the group.
   */
  @Getter
  @Setter
  private LocalizedXmlString name;
  /**
   * If the event was canceled.
   */
  @Getter
  @Setter
  private boolean cancelled;

  /**
   * Constructor.
   *
   * @param group group that is being renamed
   * @param name new name of the group
   */
  public GroupRenameEvent(Group group, LocalizedXmlString name) {
    this.group = group;
    this.name = name;
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
