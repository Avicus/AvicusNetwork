package net.avicus.atlas.module.checks.types;

import java.util.Optional;
import lombok.ToString;
import net.avicus.atlas.match.registry.WeakReference;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.CheckResult;
import net.avicus.atlas.module.checks.variable.LocationVariable;
import net.avicus.magma.util.region.Region;

/**
 * An inside check checks if a player is in a certain region.
 */
@ToString
public class InsideCheck implements Check {

  private final Optional<WeakReference<Region>> regionReference;

  public InsideCheck(Optional<WeakReference<Region>> regionReference) {
    this.regionReference = regionReference;
  }

  @Override
  public CheckResult test(CheckContext context) {
    Optional<Region> region = Optional.empty();

    if (this.regionReference.isPresent()) {
      region = this.regionReference.get().getObject();
    }

    if (!region.isPresent()) {
      return CheckResult.IGNORE;
    }

    Optional<LocationVariable> var = context.getFirst(LocationVariable.class);

    if (var.isPresent()) {
      return CheckResult.valueOf(region.get().contains(var.get().getLocation()));
    }

    return CheckResult.IGNORE;
  }
}
