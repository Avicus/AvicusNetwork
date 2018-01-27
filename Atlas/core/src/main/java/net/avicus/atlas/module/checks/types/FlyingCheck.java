package net.avicus.atlas.module.checks.types;

import java.util.Optional;
import lombok.ToString;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.CheckResult;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import org.bukkit.entity.Player;

/**
 * A flying check checks the player's flying status.
 */
@ToString
public class FlyingCheck implements Check {

  private final boolean flying;

  public FlyingCheck(boolean flying) {
    this.flying = flying;
  }

  @Override
  public CheckResult test(CheckContext context) {
    Optional<PlayerVariable> optional = context.getFirst(PlayerVariable.class);

    if (!optional.isPresent()) {
      return CheckResult.IGNORE;
    }

    Player player = optional.get().getPlayer();
    return CheckResult.valueOf(this.flying == player.isFlying());
  }
}
