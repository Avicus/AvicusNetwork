package net.avicus.atlas.module.checks.types;

import java.util.Optional;
import lombok.ToString;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.CheckResult;
import net.avicus.atlas.module.checks.variable.LocationVariable;
import net.avicus.compendium.inventory.MultiMaterialMatcher;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

/**
 * A void check checks if the location supplied is above void.
 */
@ToString
public class VoidCheck implements Check {

  private final int min;
  private final int max;
  private final Optional<MultiMaterialMatcher> ignoredBlocks;

  public VoidCheck(int min, int max, Optional<MultiMaterialMatcher> ignoredBlocks) {
    this.min = min;
    this.max = max;
    this.ignoredBlocks = ignoredBlocks;
  }

  @Override
  public CheckResult test(CheckContext context) {
    Optional<LocationVariable> optional = context.getFirst(LocationVariable.class);

    if (!optional.isPresent()) {
      return CheckResult.IGNORE;
    }

    Vector vector = optional.get().getLocation().toVector();
    World world = optional.get().getLocation().getWorld();
    // Check is at bottom count as void.
    if (vector.getY() == this.min) {
      return CheckResult.ALLOW;
    }

    for (int i = this.min; i <= this.max; i++) {
      Block block = world.getBlockAt(vector.getBlockX(), i, vector.getBlockZ());
      // Ignore if same block
      if (block.getLocation().toVector().equals(vector)) {
        continue;
      }

      if (this.ignoredBlocks.map(m -> m.matches(block.getState())).orElse(false)) {
        continue;
      }

      if (block.getType() != Material.AIR) {
        return CheckResult.DENY;
      }
    }
    return CheckResult.ALLOW;
  }
}
