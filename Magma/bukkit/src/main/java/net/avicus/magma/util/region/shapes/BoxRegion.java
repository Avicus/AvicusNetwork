package net.avicus.magma.util.region.shapes;

import lombok.ToString;
import org.bukkit.util.Vector;

@ToString(callSuper = true)
public class BoxRegion extends CuboidRegion {

  public BoxRegion(Vector center, int x, int y, int z) {
    super(center.clone().subtract(new Vector(x, y, z)), center.clone().add(new Vector(x, y, z)));
  }
}
