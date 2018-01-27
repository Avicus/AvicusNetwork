package net.avicus.magma.util.region.special;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import lombok.Getter;
import lombok.ToString;
import net.avicus.magma.util.region.BoundedRegion;
import net.avicus.magma.util.region.Region;
import org.bukkit.util.Vector;

@ToString
public class BoundsRegion implements Region, BoundedRegion {

  private final List<Vector> vectors;
  @Getter
  private final Vector min;
  @Getter
  private final Vector max;

  public BoundsRegion(BoundedRegion base, boolean xSide, boolean ySide, boolean zSide) {
    this.min = base.getMin();
    this.max = base.getMax();
    this.vectors = new ArrayList<>();
    base.forEach(vector -> {
      if ((vector.getBlockX() == min.getBlockX() || vector.getBlockX() == max.getBlockX())
          && xSide) {
        vectors.add(vector);
      }
      if ((vector.getBlockY() == min.getBlockY() || vector.getBlockY() == max.getBlockY())
          && ySide) {
        vectors.add(vector);
      }
      if ((vector.getBlockZ() == min.getBlockZ() || vector.getBlockZ() == max.getBlockZ())
          && zSide) {
        vectors.add(vector);
      }
    });
  }

  @Override
  public boolean contains(Vector vector) {
    return this.vectors.contains(vector);
  }

  @Override
  public Vector getRandomPosition(Random random) {
    return this.vectors.get(new Random().nextInt(this.vectors.size() - 1));
  }

  @Override
  public Iterator<Vector> iterator() {
    return this.vectors.iterator();
  }
}
