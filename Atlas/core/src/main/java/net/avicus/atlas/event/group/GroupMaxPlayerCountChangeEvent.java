package net.avicus.atlas.event.group;

import lombok.Getter;
import net.avicus.atlas.module.groups.Group;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * An event that is fired when a {@link Group}'s max player count is changed.
 */
public class GroupMaxPlayerCountChangeEvent extends Event {

  private static final HandlerList handlers = new HandlerList();
  @Getter
  private final Group group;

  public GroupMaxPlayerCountChangeEvent(Group group) {
    this.group = group;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}
