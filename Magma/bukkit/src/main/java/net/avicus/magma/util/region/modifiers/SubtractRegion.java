package net.avicus.magma.util.region.modifiers;

import com.google.common.collect.Sets;
import java.util.Random;
import java.util.Set;
import lombok.ToString;
import net.avicus.magma.util.region.Region;
import org.bukkit.util.Vector;

@ToString
public class SubtractRegion implements Region {

  private final JoinRegion<?> child;

  public SubtractRegion(JoinRegion child) {
    this.child = child;
  }

  @Override
  public boolean contains(Vector vector) {
    Region base = this.child.getChildren().get(0);
    for (Region region : this.child.getChildren()) {
      if (region.equals(base)) {
        continue;
      }
      if (region.contains(vector)) {
        return false;
      }
    }
    return base.contains(vector);
  }

  @Override
  public Vector getRandomPosition(Random random) {
    Set<Vector> vectors = Sets.newHashSet();
    Region base = this.child.getChildren().get(0);
    for (Region region : this.child.getChildren()) {
      if (region.equals(base)) {
        continue;
      }
      vectors.add(region.getRandomPosition(random));
    }
    return vectors.stream().findAny().orElse(null);
  }
}
