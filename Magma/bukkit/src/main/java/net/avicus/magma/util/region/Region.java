package net.avicus.magma.util.region;

import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public interface Region {

  boolean contains(Vector vector);

  Vector getRandomPosition(Random random);

  default Set<Chunk> getChunks(World world) {
    return Arrays.stream(world.getLoadedChunks()).filter(chunk -> !chunk.isEmpty())
        .collect(Collectors.toSet());
  }

  default boolean contains(Location location) {
    return this.contains(location.getBlock().getLocation().toVector());
  }

  default boolean contains(Block block) {
    return this.contains(block.getLocation().toVector());
  }

  default boolean contains(BlockState block) {
    return this.contains(block.getLocation().toVector());
  }

  default boolean contains(Entity entity) {
    return this.contains(entity.getLocation());
  }
}
