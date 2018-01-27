package net.avicus.hook.rate;

import javax.annotation.Nonnegative;
import lombok.Getter;
import net.avicus.atlas.event.match.MatchEvent;
import net.avicus.atlas.match.Match;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public final class MapRatedEvent extends MatchEvent {

  private static final HandlerList HANDLER_LIST = new HandlerList();
  @Getter
  private final Player player;
  @Getter
  @Nonnegative
  private final int rating;

  public MapRatedEvent(Match match, Player player, @Nonnegative int rating) {
    super(match);
    this.player = player;
    this.rating = rating;
  }

  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLER_LIST;
  }
}
