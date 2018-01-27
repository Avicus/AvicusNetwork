package net.avicus.magma.util.region.shapes;

import lombok.ToString;
import org.bukkit.util.Vector;

@ToString(callSuper = true)
public class RectangleRegion extends CuboidRegion {

  public RectangleRegion(double xMin, double zMin, double xMax, double zMax) {
    super(new Vector(xMin, Double.MIN_VALUE, zMin), new Vector(xMax, Double.MAX_VALUE, zMax));
  }
}
