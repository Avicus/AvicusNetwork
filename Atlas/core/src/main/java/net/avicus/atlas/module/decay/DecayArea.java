package net.avicus.atlas.module.decay;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.util.AtlasTask;
import net.avicus.magma.util.region.BoundedRegion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.joda.time.Duration;

@ToString
public class DecayArea {

  @Getter
  private final BoundedRegion region;
  @Getter
  private final List<DecayPhase> phases;
  @Getter
  private final Duration fallDelay;

  private List<Location> fallen = new ArrayList<>();

  public DecayArea(BoundedRegion region, List<DecayPhase> phases, Duration fallDelay) {
    this.region = region;
    this.phases = phases;
    this.fallDelay = fallDelay;
  }

  public void decay(Location below) {
    int delay = 0;

    List<Block> near = getNearBlocks(below);

    fallen.addAll(near.stream().map(Block::getLocation).collect(Collectors.toList()));

    for (DecayPhase phase : this.phases) {
      delay += (int) phase.getDelay().getMillis();
      AtlasTask.of(() -> {
        for (Block block : near) {
          block.setType(phase.getMaterial());
          block.setData(phase.getData());
        }
      }).later(20 * (delay / 1000));
    }

    delay += (int) this.fallDelay.getMillis();
    AtlasTask.of(() -> {
      for (Block block : near) {
        Material material = block.getType();
        byte data = block.getData();

        block.setType(Material.AIR);

        FallingBlock falling = block.getWorld()
            .spawnFallingBlock(block.getLocation(), material, data);
        falling.setDropItem(false);
      }
    }).later(20 * (delay / 1000));
  }

  private List<Block> getNearBlocks(Location below) {
    double padding = 0.6;

    final List<Block> near = new ArrayList<Block>();
    near.add(below.getBlock());

    Block test = below.clone().add(padding, 0, padding).getBlock();
    if (!near.contains(test)) {
      near.add(test);
    }

    test = below.clone().add(padding, 0, -padding).getBlock();
    if (!near.contains(test)) {
      near.add(test);
    }

    test = below.clone().add(-padding, 0, padding).getBlock();
    if (!near.contains(test)) {
      near.add(test);
    }

    test = below.clone().add(-padding, 0, -padding).getBlock();
    if (!near.contains(test)) {
      near.add(test);
    }

    Iterator<Block> i = near.iterator();
    while (i.hasNext()) {
      Block block = i.next();
      if (block.getType() == Material.AIR || fallen.contains(block.getLocation()) || !this.region
          .contains(test)) {
        i.remove();
      }
    }

    return near;
  }
}
