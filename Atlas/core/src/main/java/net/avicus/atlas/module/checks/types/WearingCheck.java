package net.avicus.atlas.module.checks.types;

import java.util.Optional;
import lombok.ToString;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.CheckResult;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.util.ScopableItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A wearing check checks the type of item a player is wearing in any of their armor slots.
 */
@ToString
public class WearingCheck implements Check {

  private final ScopableItemStack itemStack;

  public WearingCheck(ScopableItemStack itemStack) {
    this.itemStack = itemStack;
  }

  @Override
  public CheckResult test(CheckContext context) {
    Optional<PlayerVariable> optional = context.getFirst(PlayerVariable.class);

    if (!optional.isPresent()) {
      return CheckResult.IGNORE;
    }

    Player player = optional.get().getPlayer();
    ItemStack[] contents = player.getInventory().getArmorContents();

    for (ItemStack test : contents) {
      boolean matches = this.itemStack.equals(player, test);
      if (matches) {
        return CheckResult.ALLOW;
      }
    }

    return CheckResult.DENY;
  }
}
