package net.avicus.magma.util.region;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.util.Vector;

public interface BoundedRegion extends Region, RepelableRegion, Iterable<Vector> {

  Vector getMin();

  Vector getMax();

  default Vector getCenter() {
    return getMin().getMidpoint(getMax());
  }

  @Override
  default Set<Chunk> getChunks(World world) {
    Set<Chunk> chunks = new HashSet<>();
    this.iterator().forEachRemaining(vector -> chunks.add(vector.toLocation(world).getChunk()));
    return chunks;
  }

  @Override
  default Vector getRepelVector(Vector from) {
    return from.subtract(getCenter()).normalize();
  }
}
