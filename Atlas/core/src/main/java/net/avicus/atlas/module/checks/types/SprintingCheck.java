package net.avicus.atlas.module.checks.types;

import java.util.Optional;
import lombok.ToString;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.CheckResult;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import org.bukkit.entity.Player;

/**
 * A sprinting check checks the player's sprinting status.
 */
@ToString
public class SprintingCheck implements Check {

  private final boolean sprinting;

  public SprintingCheck(boolean sprinting) {
    this.sprinting = sprinting;
  }

  @Override
  public CheckResult test(CheckContext context) {
    Optional<PlayerVariable> optional = context.getFirst(PlayerVariable.class);

    if (!optional.isPresent()) {
      return CheckResult.IGNORE;
    }

    Player player = optional.get().getPlayer();
    return CheckResult.valueOf(this.sprinting == player.isSprinting());
  }
}
