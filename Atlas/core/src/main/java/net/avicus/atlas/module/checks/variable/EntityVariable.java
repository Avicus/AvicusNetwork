package net.avicus.atlas.module.checks.variable;

import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.module.checks.Variable;
import org.bukkit.entity.Entity;

/**
 * THe entity variable contains information about the type of entity that a check is being performed
 * against.
 */
@ToString
public class EntityVariable implements Variable {

  @Getter
  private final Entity entity;

  public EntityVariable(Entity entity) {
    this.entity = entity;
  }
}
