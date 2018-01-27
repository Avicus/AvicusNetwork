package net.avicus.magma.util.region.modifiers;

import com.google.common.collect.Iterators;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import lombok.ToString;
import net.avicus.magma.util.region.BoundedRegion;
import org.bukkit.util.Vector;

@ToString(callSuper = true)
public class BoundedJoinRegion extends JoinRegion<BoundedRegion> implements BoundedRegion {

  private final Vector min;
  private final Vector max;

  @SuppressWarnings("unchecked")
  public BoundedJoinRegion(List<? extends BoundedRegion> children) {
    super((List<BoundedRegion>) children);

    Vector min = new Vector(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    Vector max = new Vector(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

    for (BoundedRegion region : children) {
      Vector regionMin = region.getMin();
      Vector regionMax = region.getMax();

      if (regionMin.getX() < min.getX() && regionMin.getY() < min.getY() && regionMin.getZ() < min
          .getZ()) {
        min = regionMin;
      }
      if (regionMax.getX() > max.getX() && regionMax.getY() > max.getY() && regionMax.getZ() > max
          .getZ()) {
        max = regionMax;
      }
    }

    this.min = min;
    this.max = max;
  }

  @Override
  public Vector getRandomPosition(Random random) {
    int num = random.nextInt(getChildren().size());
    return getChildren().get(num).getRandomPosition(random);
  }

  @Override
  public Vector getMin() {
    return this.min.clone();
  }

  @Override
  public Vector getMax() {
    return this.max.clone();
  }

  @Override
  public Iterator<Vector> iterator() {
    Iterator<Vector> iterator = getChildren().get(0).iterator();
    for (BoundedRegion region : getChildren()) {
      if (!region.equals(getChildren().get(0))) {
        iterator = Iterators.concat(iterator, region.iterator());
      }
    }
    return iterator;
  }
}
