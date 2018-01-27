package net.avicus.magma.util.region.modifiers;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Random;
import lombok.Getter;
import lombok.ToString;
import net.avicus.magma.util.region.Region;
import org.bukkit.util.Vector;

@ToString
public class JoinRegion<T extends Region> implements Region {

  @Getter
  private final List<T> children;

  public JoinRegion(List<T> children) {
    Preconditions.checkArgument(children.size() > 0, "Joined region lacks children.");
    this.children = children;
  }

  @Override
  public boolean contains(Vector vector) {
    for (Region region : this.children) {
      if (region.contains(vector)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Vector getRandomPosition(Random random) {
    throw new UnsupportedOperationException();
  }
}
