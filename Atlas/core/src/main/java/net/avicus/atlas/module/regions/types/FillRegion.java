package net.avicus.atlas.module.regions.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.compendium.inventory.MaterialMatcher;
import net.avicus.compendium.inventory.MultiMaterialMatcher;
import net.avicus.magma.util.region.BoundedRegion;
import net.avicus.magma.util.region.Region;
import net.avicus.magma.util.region.shapes.CuboidRegion;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

@ToString(exclude = {"match"})
public class FillRegion implements Region {

  private final Match match;
  private final BoundedRegion bounds;
  private final MaterialMatcher materials;
  private final Optional<Vector> start;
  private final boolean connect;

  private List<Vector> points = null;

  public FillRegion(Match match, BoundedRegion bounds, MultiMaterialMatcher materials,
      Optional<Vector> start, boolean connect) {
    this.match = match;
    this.bounds = bounds;
    this.materials = materials;
    this.start = start;
    this.connect = connect;
  }

  private List<Vector> getOrFillPoints() {
    if (this.points == null) {
      fill();
    }
    return this.points;
  }

  private void fill() {
    if (this.points != null) {
      return;
    }

    World world = this.match.getWorld();
    this.points = new ArrayList<>();
    if (connect) {
      fillConnected(this.start.orElse(this.bounds.getCenter()).toLocation(world).getBlock());
    } else {
      fillAll();
    }

    if (this.points.isEmpty()) {
      this.match.warn(new Exception("Fill region contains no points."));
    }
  }

  private void fillAll() {
    for (Vector point : this.bounds) {
      // already added
      if (this.points.contains(point)) {
        continue;
      }

      Block nextBlock = point.toLocation(match.getWorld()).getBlock();

      // doesn't match materials
      if (!this.materials.matches(nextBlock.getState())) {
        continue;
      }

      this.points.add(point);
    }
  }

  private void fillConnected(Block block) {
    List<Block> next = new ArrayList<>();

    Vector min = block.getLocation().toVector().subtract(new Vector(-1, -1, -1));
    Vector max = block.getLocation().toVector().subtract(new Vector(1, 1, 1));

    CuboidRegion cuboid = new CuboidRegion(min, max);

    for (Vector add : cuboid) {
      Block nextBlock = add.toLocation(block.getWorld()).getBlock();

      // already added
      if (this.points.contains(add)) {
        continue;
      }

      // doesn't match materials
      if (!this.materials.matches(nextBlock.getState())) {
        continue;
      }

      // not within bounds
      if (!this.bounds.contains(add)) {
        continue;
      }

      next.add(nextBlock);
    }

    if (this.materials.matches(block.getState())) {
      this.points.add(block.getLocation().toVector());
    }

    next.forEach(this::fillConnected);
  }

  @Override
  public Vector getRandomPosition(Random random) {
    return this.points.get(random.nextInt(Math.max(0, this.points.size())) - 1);
  }

  @Override
  public boolean contains(Vector vector) {
    return this.getOrFillPoints().contains(vector);
  }

  @Override
  public Set<Chunk> getChunks(World world) {
    throw new UnsupportedOperationException();
  }
}
