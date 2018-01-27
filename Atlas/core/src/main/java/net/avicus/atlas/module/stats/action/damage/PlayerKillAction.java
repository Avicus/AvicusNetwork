package net.avicus.atlas.module.stats.action.damage;

import java.time.Instant;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.module.stats.action.ScoreUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.entity.Player;
import tc.oc.tracker.DamageInfo;
import tc.oc.tracker.Lifetimes;

@ToString
public class PlayerKillAction extends PlayerDamageAction {

  @Getter
  private final Player victim;
  @Getter
  private final double score;
  @Getter
  private final String debugMessage;

  public PlayerKillAction(Player actor, Instant when, DamageInfo info, Player victim) {
    super(actor, when, info);
    this.victim = victim;
    Pair<StringBuilder, Double> calc = ScoreUtils
        .getDamageInfoScore(Lifetimes.getLifetime(victim), info, victim.getLocation());
    this.score = calc.getValue();
    this.debugMessage = "Kill: " + calc.getKey().toString();
  }
}
