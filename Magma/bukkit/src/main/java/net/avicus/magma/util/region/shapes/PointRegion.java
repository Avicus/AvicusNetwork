package net.avicus.magma.util.region.shapes;

import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import lombok.ToString;
import net.avicus.magma.util.region.BoundedRegion;
import net.avicus.magma.util.region.Region;
import org.bukkit.util.Vector;

@ToString
public class PointRegion implements Region, BoundedRegion {

  private final Vector vector;

  public PointRegion(Vector vector) {
    this.vector = vector;
  }

  @Override
  public boolean contains(Vector vector) {
    return this.vector.equals(vector);
  }

  @Override
  public Vector getRandomPosition(Random random) {
    return this.vector;
  }

  @Override
  public Vector getMin() {
    return this.vector.clone();
  }

  @Override
  public Vector getMax() {
    return this.vector.clone();
  }

  @Override
  public Iterator<Vector> iterator() {
    return Collections.singletonList(this.vector).iterator();
  }
}
