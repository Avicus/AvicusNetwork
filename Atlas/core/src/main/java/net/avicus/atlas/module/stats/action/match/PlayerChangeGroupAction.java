package net.avicus.atlas.module.stats.action.match;

import java.time.Instant;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.states.State;
import org.bukkit.entity.Player;

@ToString
public class PlayerChangeGroupAction extends PlayerMatchAction {

  @Getter
  @Nullable
  private final Group from;
  @Getter
  @Nullable
  private final Group to;

  public PlayerChangeGroupAction(Player actor, Instant when, Match match, State matchState,
      Group from, Group to) {
    super(actor, when, match, matchState);
    this.from = from;
    this.to = to;
  }

  @Override
  public double getScore() {
    return -2.5;
  }

  @Override
  public String getDebugMessage() {
    String from = this.from == null ? "N/A" : this.from.getId();
    String to = this.to == null ? "N/A" : this.to.getId();
    return "Group Change: " + from + "->" + to;
  }
}
