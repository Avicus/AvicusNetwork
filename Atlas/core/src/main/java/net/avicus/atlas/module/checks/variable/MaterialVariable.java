package net.avicus.atlas.module.checks.variable;

import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.module.checks.Variable;
import org.bukkit.material.MaterialData;

/**
 * The material variable is similar to the item variable but only includes the material of the item
 * (or block) being checked against.
 */
@ToString
public class MaterialVariable implements Variable {

  @Getter
  private final MaterialData data;

  public MaterialVariable(MaterialData data) {
    this.data = data;
  }
}
