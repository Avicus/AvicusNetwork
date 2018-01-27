package net.avicus.magma.util.region.modifiers;

import java.util.Iterator;
import java.util.Random;
import lombok.ToString;
import net.avicus.magma.util.region.BoundedRegion;
import net.avicus.magma.util.region.BoundedRegionIterator;
import org.bukkit.util.Vector;

@ToString(callSuper = true)
public class BoundedTranslateRegion extends TranslateRegion implements BoundedRegion {

  private final BoundedRegion child;

  public BoundedTranslateRegion(BoundedRegion child, Vector offset) {
    super(child, offset);
    this.child = child;
  }

  @Override
  public Vector getRandomPosition(Random random) {
    return this.child.getRandomPosition(random).add(getOffset());
  }

  @Override
  public Vector getMin() {
    return this.child.getMin().add(getOffset());
  }

  @Override
  public Vector getMax() {
    return this.child.getMax().add(getOffset());
  }

  @Override
  public Iterator<Vector> iterator() {
    return new BoundedRegionIterator(this);
  }
}
