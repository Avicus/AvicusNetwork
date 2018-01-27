package net.avicus.atlas.module.checks.types;

import java.util.Optional;
import lombok.ToString;
import net.avicus.atlas.match.registry.WeakReference;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.CheckResult;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.module.kits.Kit;
import net.avicus.atlas.module.kits.KitsModule;
import org.bukkit.entity.Player;

/**
 * A kit check checks the player's current kit, will ignore if kits module is not enabled.
 */
@ToString
public class KitCheck implements Check {

  private final WeakReference<Kit> kit;

  public KitCheck(WeakReference<Kit> kit) {
    this.kit = kit;
  }

  @Override
  public CheckResult test(CheckContext context) {
    Optional<PlayerVariable> optional = context.getFirst(PlayerVariable.class);
    Optional<KitsModule> module = context.getMatch().getModule(KitsModule.class);

    if (!optional.isPresent() || !module.isPresent() || !this.kit.isPresent()) {
      return CheckResult.IGNORE;
    }

    Player player = optional.get().getPlayer();

    Optional<Kit> kit = module.get().getActiveKit(player);

    if (!kit.isPresent()) {
      return CheckResult.DENY;
    }

    return CheckResult
        .valueOf(module.get().getActiveKit(player).get().equals(this.kit.getObject().get()));
  }
}
