package net.avicus.magma.util.region.shapes;

import java.util.Iterator;
import java.util.Random;
import lombok.Getter;
import lombok.ToString;
import net.avicus.magma.util.region.BoundedRegion;
import net.avicus.magma.util.region.BoundedRegionIterator;
import org.bukkit.util.Vector;

@ToString
public class CylinderRegion implements BoundedRegion {

  private final Vector base;
  @Getter
  private final int radius;
  @Getter
  private final int height;
  private final int radiusSquared; // for a more efficient contains calculation

  public CylinderRegion(Vector base, int radius, int height) {
    this.base = base;
    this.radius = radius;
    this.height = height;
    this.radiusSquared = (int) Math.pow(radius, 2);
  }

  @Override
  public boolean contains(Vector position) {
    if (position.getY() < this.base.getY() || position.getY() > this.base.getY() + this.height) {
      return false;
    }

    double distanceSquared = Math.pow(position.getX() - this.base.getX(), 2) + Math
        .pow(position.getZ() - this.base.getZ(), 2);
    return distanceSquared <= this.radiusSquared;
  }

  @Override
  public Vector getRandomPosition(Random random) {
    double r = this.radius;
    Vector randy = new Vector(getRandom(random, -r, r), getRandom(random, 0, this.height),
        getRandom(random, -r, r));
    return this.base.clone().add(randy);
  }

  private double getRandom(Random random, double min, double max) {
    return min + (max - min) * random.nextDouble();
  }

  @Override
  public Vector getMin() {
    Vector size = new Vector(this.radius, 0, this.radius);
    return this.base.clone().subtract(size);
  }

  @Override
  public Vector getMax() {
    Vector size = new Vector(this.radius, this.height, this.radius);
    return this.base.clone().add(size);
  }

  @Override
  public Iterator<Vector> iterator() {
    return new BoundedRegionIterator(this);
  }
}
