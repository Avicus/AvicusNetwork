package net.avicus.atlas.module.stats.action.match;

import java.time.Instant;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.states.State;
import org.bukkit.entity.Player;

@ToString(callSuper = true)
public class PlayerEliminateAction extends PlayerLeaveMatchAction {

  public PlayerEliminateAction(Player actor, Instant when, Match match, State matchState) {
    super(actor, when, match, matchState);
  }
}
