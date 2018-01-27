package net.avicus.atlas.module.structures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.match.registry.RegisterableObject;
import net.avicus.atlas.util.AtlasTask;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * Represents a collection of blocks that can be placed inside of a world.
 * <p>
 * All arrays are based on an index of: y coord * width * length + z coord * width + x coord,
 * where the coordinates are relative based on the minimum corner of the schematic region.
 */
@Getter
@ToString
public abstract class Schematic implements RegisterableObject<Schematic> {

  /**
   * Unique identifier for this schematic that is used for XML references.
   */
  private final String id;

  /**
   * Array of blocks in the schematic.
   */
  protected short[] blocks;
  /**
   * Corresponding array of block data for the blocks in the schematic. Every block has a data
   * value.
   */
  protected byte[] data;
  /**
   * Width (x max - min) of the schematic
   */
  protected short width;
  /**
   * Length (z max - min) of the schematic
   */
  protected short length;
  /**
   * Height (y max - min) of the schematic
   */
  protected short height;
  /**
   * A collection of tasks used to paste blocks from this schematic.
   */
  private List<AtlasTask> pendingPastes = new ArrayList<>();

  public Schematic(String id) {
    this.id = id;
  }

  abstract void load();

  public void reload() {

  }

  ;

  public void clearSource() {

  }

  ;

  /**
   * Past the blocks in this schematic
   *
   * @param loc min corner to begin placement
   * @param natural If the blocks should be placed with an effect
   * @param random If the blocks should be placed in random order
   * @param ignoreAir If air blocks should be skipped during pasting
   * @param pasteDelay delay between pastes (in ticks)
   */
  public void paste(Location loc, boolean natural, boolean random, boolean ignoreAir,
      int pasteDelay) {
    pendingPastes.clear();

    for (int x = 0; x < this.width; ++x) {
      for (int y = 0; y < this.height; ++y) {
        for (int z = 0; z < this.length; ++z) {
          int index = y * this.width * this.length + z * this.width + x;
          int taskX = x;
          int taskY = y;
          int taskZ = z;
          if (ignoreAir && this.blocks[index] == 0) {
            continue;
          }
          if (pasteDelay > 0) {
            pendingPastes.add(AtlasTask.of(() -> {
                  Vector vector = new Vector(taskX + loc.getX(), taskY + loc.getY(),
                      taskZ + loc.getZ());
                  loc.getWorld().fastBlockChange(vector, this.blocks[index], this.data[index]);
                  if (natural) {
                    loc.getWorld().spigot()
                        .playEffect(vector.toLocation(loc.getWorld()), Effect.STEP_SOUND,
                            this.blocks[index] + (this.data[index] << 12), 0, 0, 0, 0, 1, 1, 45);
                  }
                }
            ));
          } else {
            Vector vector = new Vector(taskX + loc.getX(), taskY + loc.getY(), taskZ + loc.getZ());
            loc.getWorld().fastBlockChange(vector, this.blocks[index], this.data[index]);
            if (natural) {
              loc.getWorld().spigot()
                  .playEffect(vector.toLocation(loc.getWorld()), Effect.STEP_SOUND,
                      this.blocks[index] + (this.data[index] << 12), 0, 0, 0, 0, 1, 1, 45);
            }
          }
        }
      }
    }

    if (random) {
      Collections.shuffle(pendingPastes);
    }

    int delay = pasteDelay;
    for (AtlasTask task : pendingPastes) {
      task.later(delay);
      delay += pasteDelay;
    }
  }

  /**
   * Paste all blocks as fast as possible in X->Y->Z order.
   *
   * @param location minimum corner to begin placement of blocks.
   */
  public void paste(Location location) {
    paste(location, false, false, true, 0);
  }

  /**
   * Cancels all remaining pastes in the paste queue.
   */
  public void cancelAllPendingPastes() {
    pendingPastes.forEach(AtlasTask::cancel0);
    pendingPastes.clear();
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public Schematic getObject() {
    return this;
  }
}