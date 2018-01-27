package net.avicus.atlas.module.structures;

import com.google.common.base.Preconditions;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.magma.util.region.BoundedRegion;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

/**
 * Represents a schematic that is loaded (and can be reloaded) from a {@link BoundedRegion}.
 */
@ToString(callSuper = true)
public class BoundedRegionSchematic extends Schematic {

  /**
   * Region to pull the schematic from.
   */
  private BoundedRegion region;
  /**
   * Match that the region exists inside of.
   */
  private Match match;

  public BoundedRegionSchematic(String id, BoundedRegion region, Match match) {
    super(id);
    this.region = region;
    this.match = match;
  }

  @Override
  void load() {
    loadFromBounded(this.region, this.match);
  }

  /**
   * Populates block and data arrays from a region.
   *
   * @param region source of the blocks
   * @param match match that the region is in
   */
  private void loadFromBounded(BoundedRegion region, Match match) {
    Preconditions.checkNotNull(region);
    Preconditions.checkNotNull(match);

    short width = new Integer((region.getMax().getBlockX() - region.getMin().getBlockX()) + 1)
        .shortValue();
    short length = new Integer((region.getMax().getBlockZ() - region.getMin().getBlockZ()) + 1)
        .shortValue();
    short height = new Integer((region.getMax().getBlockY() - region.getMin().getBlockY()) + 1)
        .shortValue();
    Vector min = region.getMin();

    short[] blocks = new short[width * height * length];
    byte[] data = new byte[width * height * length];

    region.forEach(vector -> {
      Vector relative = vector.clone().subtract(min);
      int x = relative.getBlockX();
      int y = relative.getBlockY();
      int z = relative.getBlockZ();

      int index = y * width * length + z * width + x;
      Block block = vector.toLocation(match.getWorld()).getBlock();
      blocks[index] = (short) block.getTypeId();
      data[index] = block.getData();
    });

    this.blocks = blocks;
    this.data = data;
    this.width = width;
    this.length = length;
    this.height = height;
  }

  @Override
  public void reload() {
    this.load();
  }

  @Override
  public void clearSource() {
    this.match.getWorld().fastBlockChange(this.region, new MaterialData(Material.AIR));
  }
}
