package net.avicus.atlas.module.stats.action.match;

import java.time.Instant;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.states.State;
import net.avicus.atlas.module.stats.action.base.PlayerAction;
import org.bukkit.entity.Player;

@ToString(exclude = "match")
public abstract class PlayerMatchAction implements PlayerAction {

  @Getter
  private final Player actor;
  @Getter
  private final Instant when;
  @Getter
  private final Match match;
  @Getter
  private final State matchState;

  public PlayerMatchAction(Player actor, Instant when, Match match, State matchState) {
    this.actor = actor;
    this.when = when;
    this.match = match;
    this.matchState = matchState;
  }
}
