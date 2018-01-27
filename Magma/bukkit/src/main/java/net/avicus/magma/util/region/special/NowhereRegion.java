package net.avicus.magma.util.region.special;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import lombok.ToString;
import net.avicus.magma.util.region.Region;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.util.Vector;

@ToString
public class NowhereRegion implements Region {

  @Override
  public boolean contains(Vector vector) {
    return false;
  }

  @Override
  public Set<Chunk> getChunks(World world) {
    return new HashSet<>();
  }

  @Override
  public Vector getRandomPosition(Random random) {
    throw new UnsupportedOperationException();
  }
}
