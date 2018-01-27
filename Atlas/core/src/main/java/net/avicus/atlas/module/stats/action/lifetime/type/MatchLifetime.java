package net.avicus.atlas.module.stats.action.lifetime.type;

import java.time.Instant;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.stats.action.match.PlayerMatchAction;

@ToString(exclude = "match")
public class MatchLifetime extends ActionLifetime<PlayerMatchAction> {

  @Getter
  private final Match match;

  public MatchLifetime(Instant start, Match match) {
    super(start);
    this.match = match;
  }
}
