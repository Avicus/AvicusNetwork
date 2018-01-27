package net.avicus.atlas.module.checks.types;

import java.util.Optional;
import lombok.ToString;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.CheckResult;
import net.avicus.atlas.module.checks.variable.EntityVariable;
import org.bukkit.entity.EntityType;

/**
 * An entity type check checks the type of entity that is causing an event.
 */
@ToString
public class EntityTypeCheck implements Check {

  private final EntityType type;

  public EntityTypeCheck(EntityType type) {
    this.type = type;
  }

  @Override
  public CheckResult test(CheckContext context) {
    Optional<EntityVariable> var = context.getFirst(EntityVariable.class);
    if (var.isPresent()) {
      return CheckResult.valueOf(this.type == var.get().getEntity().getType());
    }
    return CheckResult.IGNORE;
  }
}
