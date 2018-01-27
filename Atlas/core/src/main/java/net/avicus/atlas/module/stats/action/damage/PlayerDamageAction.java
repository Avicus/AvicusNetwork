package net.avicus.atlas.module.stats.action.damage;

import java.time.Instant;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.module.stats.action.base.PlayerAction;
import org.bukkit.entity.Player;
import tc.oc.tracker.DamageInfo;

@ToString
public abstract class PlayerDamageAction implements PlayerAction {

  @Getter
  private final Player actor;
  @Getter
  private final Instant when;
  @Getter
  @Nullable
  private final DamageInfo info;

  public PlayerDamageAction(Player actor, Instant when, DamageInfo info) {
    this.actor = actor;
    this.when = when;
    this.info = info;
  }
}
