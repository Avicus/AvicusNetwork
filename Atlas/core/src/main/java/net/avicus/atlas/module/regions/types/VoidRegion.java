package net.avicus.atlas.module.regions.types;

import java.util.Optional;
import java.util.Random;
import java.util.Set;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.compendium.inventory.MultiMaterialMatcher;
import net.avicus.magma.util.region.Region;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

@ToString(exclude = "match")
public class VoidRegion implements Region {

  private final Match match;
  private final int yMin;
  private final int yMax;
  private final Optional<MultiMaterialMatcher> ignoredBlocks;

  public VoidRegion(Match match, int yMin, int yMax, Optional<MultiMaterialMatcher> ignoredBlocks) {
    this.match = match;
    this.yMin = yMin;
    this.yMax = yMax;
    this.ignoredBlocks = ignoredBlocks;
  }

  @Override
  public boolean contains(Vector vector) {
    World world = this.match.getWorld();

    // Check is at bottom count as void.
    if (vector.getY() == yMin) {
      return true;
    }

    for (int i = yMin; i <= yMax; i++) {
      Block block = world.getBlockAt(vector.getBlockX(), i, vector.getBlockZ());
      // Ignore if same block
      if (block.getLocation().toVector().equals(vector)) {
        continue;
      }

      if (this.ignoredBlocks.map(m -> m.matches(block.getState())).orElse(false)) {
        continue;
      }

      if (block.getType() != Material.AIR) {
        return false;
      }
    }
    return true;
  }

  @Override
  public Vector getRandomPosition(Random random) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Set<Chunk> getChunks(World world) {
    throw new UnsupportedOperationException();
  }
}
