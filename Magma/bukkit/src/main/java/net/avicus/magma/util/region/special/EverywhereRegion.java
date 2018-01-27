package net.avicus.magma.util.region.special;

import java.util.Random;
import lombok.ToString;
import net.avicus.magma.util.region.Region;
import org.bukkit.util.Vector;

@ToString
public class EverywhereRegion implements Region {

  @Override
  public boolean contains(Vector vector) {
    return true;
  }

  @Override
  public Vector getRandomPosition(Random random) {
    throw new UnsupportedOperationException();
  }
}
