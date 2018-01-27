package net.avicus.magma.util.region.shapes;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import lombok.ToString;
import net.avicus.magma.util.region.BoundedRegion;
import net.avicus.magma.util.region.Region;
import org.bukkit.util.Vector;

@ToString
public class CuboidRegion implements Region, BoundedRegion {

  private final Vector min;
  private final Vector max;

  public CuboidRegion(Vector min, Vector max) {
    this.min = Vector.getMinimum(min, max);
    this.max = Vector.getMaximum(min, max);
  }

  @Override
  public boolean contains(Vector vector) {
    return vector.isInAABB(this.min, this.max);
  }

  @Override
  public Vector getRandomPosition(Random random) {
    double x = random.nextDouble() * (this.max.getX() - this.min.getX()) + this.min.getX();
    double y = random.nextDouble() * (this.max.getY() - this.min.getY()) + this.min.getY();
    double z = random.nextDouble() * (this.max.getZ() - this.min.getZ()) + this.min.getZ();
    return new Vector(x, y, z);
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
    return new Iterator<Vector>() {
      private int nextX = min.getBlockX();
      private int nextY = min.getBlockY();
      private int nextZ = min.getBlockZ();

      @Override
      public boolean hasNext() {
        return this.nextX != Integer.MAX_VALUE; // special case
      }

      @Override
      public Vector next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }

        Vector result = new Vector(this.nextX, this.nextY, this.nextZ);

        // increment next
        if (++this.nextX > max.getBlockX()) {
          this.nextX = min.getBlockX();
          if (++this.nextY > max.getBlockY()) {
            this.nextY = min.getBlockY();
            if (++this.nextZ > max.getBlockZ()) {
              this.nextX = Integer.MAX_VALUE;
            }
          }
        }

        return result;
      }
    };
  }
}
