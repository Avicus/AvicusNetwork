package net.avicus.magma.util.region.modifiers;

import java.util.List;
import java.util.Random;
import lombok.ToString;
import net.avicus.magma.util.region.Region;
import org.bukkit.util.Vector;

@ToString
public class InvertRegion implements Region {

  private final Region child;

  public InvertRegion(Region child) {
    this.child = child;
  }

  public InvertRegion(List<Region> children) {
    this.child = new JoinRegion<>(children);
  }

  @Override
  public boolean contains(Vector vector) {
    // invert contains()
    return !this.child.contains(vector);
  }

  @Override
  public Vector getRandomPosition(Random random) {
    throw new UnsupportedOperationException();
  }
}
