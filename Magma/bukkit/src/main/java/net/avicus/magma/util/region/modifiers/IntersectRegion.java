package net.avicus.magma.util.region.modifiers;

import java.util.Random;
import lombok.ToString;
import net.avicus.magma.util.region.Region;
import org.bukkit.util.Vector;

@ToString
public class IntersectRegion implements Region {

  private final JoinRegion<Region> child;

  public IntersectRegion(JoinRegion child) {
    this.child = child;
  }

  @Override
  public boolean contains(Vector vector) {
    for (Region region : this.child.getChildren()) {
      if (!region.contains(vector)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public Vector getRandomPosition(Random random) {
    throw new UnsupportedOperationException();
  }
}
