package net.avicus.atlas.module.checks.variable;

import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.module.checks.Variable;
import org.bukkit.Location;

/**
 * The location variable contains information about the location in the world where the action is
 * taking place.
 */
@ToString
public class LocationVariable implements Variable {

  @Getter
  private final Location location;

  public LocationVariable(Location location) {
    this.location = location;
  }
}
