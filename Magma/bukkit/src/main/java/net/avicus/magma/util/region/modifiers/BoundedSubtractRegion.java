package net.avicus.magma.util.region.modifiers;

import static net.avicus.compendium.utils.Vectors.isGreater;
import static net.avicus.compendium.utils.Vectors.isLess;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Set;
import lombok.Getter;
import lombok.ToString;
import net.avicus.magma.util.region.BoundedRegion;
import net.avicus.magma.util.region.Region;
import org.bukkit.util.Vector;

@ToString(callSuper = true)
public class BoundedSubtractRegion extends SubtractRegion implements BoundedRegion {

  @Getter
  private final Vector min;
  @Getter
  private final Vector max;
  private final Set<Vector> vectors;

  public BoundedSubtractRegion(JoinRegion<BoundedRegion> child) {
    super(child);
    if (!(child instanceof BoundedRegion)) {
      throw new RuntimeException("Expected bounded region.");
    }
    Set<Vector> vectors = Sets.newHashSet();
    Region base = child.getChildren().get(0);
    for (BoundedRegion region : child.getChildren()) {
      if (region.equals(base)) {
        continue;
      }
      region.iterator().forEachRemaining(vectors::add);
    }
    this.vectors = vectors;
    Vector min = new Vector(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    Vector max = new Vector(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
    for (Vector vector : this.vectors) {
      if (isLess(vector, min)) {
        min = vector;
      }
      if (isGreater(vector, max)) {
        max = vector;
      }
    }
    this.min = min;
    this.max = max;
  }

  @Override
  public Iterator<Vector> iterator() {
    return this.vectors.iterator();
  }
}
