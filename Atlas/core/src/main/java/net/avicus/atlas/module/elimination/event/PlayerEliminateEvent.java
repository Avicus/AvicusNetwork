package net.avicus.atlas.module.elimination.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerEliminateEvent extends PlayerEvent {

  private static final HandlerList handlers = new HandlerList();

  public PlayerEliminateEvent(Player player) {
    super(player);
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public HandlerList getHandlers() {
    return handlers;
  }
}
