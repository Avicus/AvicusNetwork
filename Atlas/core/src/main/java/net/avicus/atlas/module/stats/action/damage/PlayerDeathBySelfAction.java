package net.avicus.atlas.module.stats.action.damage;

import java.time.Instant;
import lombok.ToString;
import org.bukkit.entity.Player;
import tc.oc.tracker.DamageInfo;

@ToString
public class PlayerDeathBySelfAction extends PlayerDamageAction {

  public PlayerDeathBySelfAction(Player actor, Instant when, DamageInfo info) {
    super(actor, when, info);
  }

  @Override
  public double getScore() {
    return -1.5;
  }

  @Override
  public String getDebugMessage() {
    return "DEATH: self";
  }
}
