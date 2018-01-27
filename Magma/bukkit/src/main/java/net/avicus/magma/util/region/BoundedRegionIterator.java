package net.avicus.magma.util.region;

import java.util.Iterator;
import lombok.Getter;
import org.bukkit.util.Vector;

/**
 * A generic region iterator for regions that can't iterate nicely.
 */
public class BoundedRegionIterator implements Iterator<Vector> {

  @Getter
  private final BoundedRegion region;

  private final Vector min;
  private final Vector max;

  private int nextX;
  private int nextY;
  private int nextZ;

  public BoundedRegionIterator(BoundedRegion region) {
    this.region = region;

    this.min = region.getMin();
    this.max = region.getMax();

    this.nextX = this.min.getBlockX();
    this.nextY = this.min.getBlockY();
    this.nextZ = this.min.getBlockZ();
  }

  private void forward() {
    while (hasNext() && !this.region.contains(new Vector(this.nextX, this.nextY, this.nextZ))) {
      forwardOne();
    }
  }

  private void forwardOne() {
    if (++this.nextX <= this.max.getX()) {
      return;
    }

    this.nextX = this.min.getBlockX();

    if (++this.nextY <= this.max.getY()) {
      return;
    }

    this.nextY = this.min.getBlockY();

    if (++this.nextZ <= this.max.getZ()) {
      return;
    }

    // flag
    this.nextX = Integer.MIN_VALUE;
  }

  @Override
  public boolean hasNext() {
    return this.nextX != Integer.MIN_VALUE;
  }

  @Override
  public Vector next() {
    if (!hasNext()) {
      throw new java.util.NoSuchElementException();
    }

    Vector result = new Vector(this.nextX, this.nextY, this.nextZ);

    // step forward one block
    forwardOne();

    // continue forward until a block is found
    forward();

    return result;
  }
}
