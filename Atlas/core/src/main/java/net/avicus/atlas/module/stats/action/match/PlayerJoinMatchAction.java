package net.avicus.atlas.module.stats.action.match;

import java.time.Instant;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.states.State;
import org.bukkit.entity.Player;

@ToString(callSuper = true)
public class PlayerJoinMatchAction extends PlayerMatchAction {

  public PlayerJoinMatchAction(Player actor, Instant when, Match match, State matchState) {
    super(actor, when, match, matchState);
  }

  @Override
  public double getScore() {
    return 0;
  }

  @Override
  public String getDebugMessage() {
    return "Match Join";
  }
}
