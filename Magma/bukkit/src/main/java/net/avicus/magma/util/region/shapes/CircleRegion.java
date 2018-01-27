package net.avicus.magma.util.region.shapes;

import lombok.ToString;
import org.bukkit.util.Vector;

@ToString(callSuper = true)
public class CircleRegion extends CylinderRegion {

  public CircleRegion(Vector center, int radius) {
    super(center, radius, 0);
  }
}
