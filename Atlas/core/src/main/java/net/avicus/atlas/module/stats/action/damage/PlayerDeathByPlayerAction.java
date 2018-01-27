package net.avicus.atlas.module.stats.action.damage;

import java.time.Instant;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.entity.Player;
import tc.oc.tracker.DamageInfo;

@ToString
public class PlayerDeathByPlayerAction extends PlayerDamageAction {

  @Getter
  private final Player killer;

  public PlayerDeathByPlayerAction(Player actor, Instant when, DamageInfo info, Player killer) {
    super(actor, when, info);
    this.killer = killer;
  }

  @Override
  public double getScore() {
    return -0.6;
  }

  @Override
  public String getDebugMessage() {
    return "DEATH: player";
  }
}
