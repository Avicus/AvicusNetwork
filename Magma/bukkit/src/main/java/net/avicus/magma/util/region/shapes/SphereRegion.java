package net.avicus.magma.util.region.shapes;

import java.util.Iterator;
import java.util.Random;
import lombok.ToString;
import net.avicus.magma.util.region.BoundedRegion;
import net.avicus.magma.util.region.BoundedRegionIterator;
import org.bukkit.util.Vector;

@ToString
public class SphereRegion implements BoundedRegion {

  private final Vector origin;
  private final int radius;
  private final int radiusSquared; // for a more efficient contains calculation

  public SphereRegion(Vector origin, int radius) {
    this.origin = origin;
    this.radius = radius;
    this.radiusSquared = (int) Math.pow(radius, 2);
  }

  @Override
  public boolean contains(Vector position) {
    return this.origin.distanceSquared(position) <= this.radiusSquared;
  }

  @Override
  public Vector getRandomPosition(Random random) {
    double r = this.radius;
    Vector randy = new Vector(getRandom(random, -r, r), getRandom(random, -r, r),
        getRandom(random, -r, r));
    return this.origin.clone().add(randy);
  }

  private double getRandom(Random random, double min, double max) {
    return min + (max - min) * random.nextDouble();
  }

  @Override
  public Vector getMin() {
    Vector size = new Vector(this.radius, this.radius, this.radius);
    return this.origin.clone().subtract(size);
  }

  @Override
  public Vector getMax() {
    Vector size = new Vector(this.radius, this.radius, this.radius);
    return this.origin.clone().add(size);
  }

  @Override
  public Vector getCenter() {
    return this.origin.clone();
  }

  @Override
  public Iterator<Vector> iterator() {
    return new BoundedRegionIterator(this);
  }
}
