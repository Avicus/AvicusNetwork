package net.avicus.atlas.module.checks.types;

import java.util.Optional;
import lombok.ToString;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.CheckResult;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import org.bukkit.entity.Player;

/**
 * A sneaking check checks the player's sneaking status.
 */
@ToString
public class SneakingCheck implements Check {

  private final boolean sneaking;

  public SneakingCheck(boolean sneaking) {
    this.sneaking = sneaking;
  }

  @Override
  public CheckResult test(CheckContext context) {
    Optional<PlayerVariable> optional = context.getFirst(PlayerVariable.class);

    if (!optional.isPresent()) {
      return CheckResult.IGNORE;
    }

    Player player = optional.get().getPlayer();
    return CheckResult.valueOf(this.sneaking == player.isSneaking());
  }
}
