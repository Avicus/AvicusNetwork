package net.avicus.hook.credits;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerModifyCreditEvent extends PlayerEvent {

  private static final HandlerList handlers = new HandlerList();
  @Getter
  private final int amount;

  public PlayerModifyCreditEvent(Player player, int amount) {
    super(player);
    this.amount = amount;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public HandlerList getHandlers() {
    return handlers;
  }
}
