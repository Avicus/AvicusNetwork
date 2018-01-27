package net.avicus.atlas.module.checks.types;

import java.util.Optional;
import lombok.ToString;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.CheckResult;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import org.bukkit.entity.Entity;

/**
 * A onGround check checks the player's onGround status.
 */
@ToString
public class OnGroundCheck implements Check {

  private final boolean onGround;

  public OnGroundCheck(boolean onGround) {
    this.onGround = onGround;
  }

  @Override
  public CheckResult test(CheckContext context) {
    Optional<PlayerVariable> optional = context.getFirst(PlayerVariable.class);

    if (!optional.isPresent()) {
      return CheckResult.IGNORE;
    }

    Entity player = optional.get().getPlayer();
    return CheckResult.valueOf(this.onGround == player.isOnGround());
  }
}
