package net.avicus.magma.util.region.special;

import java.util.Random;
import java.util.Set;
import lombok.ToString;
import net.avicus.magma.util.region.Region;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 * Represents a sector of a circle that covers all Y values.
 */
@ToString
public class SectorRegion implements Region {

  private final double centerX;
  private final double centerZ;
  private final double startAngle;
  private final double endAngle;

  /**
   * @param x The center x value.
   * @param z The center z value.
   * @param startAngle The start angle of the sector in degrees.
   * @param endAngle The end angle of the sector in degrees.
   */
  public SectorRegion(double x, double z, double startAngle, double endAngle) {
    this.centerX = x;
    this.centerZ = z;
    while (startAngle > 360) {
      startAngle -= 360;
    }
    while (endAngle > 360) {
      endAngle -= 360;
    }

    this.startAngle = Math.toRadians(Math.min(startAngle, endAngle));
    this.endAngle = Math.toRadians(Math.max(startAngle, endAngle));
  }

  @Override
  public boolean contains(Vector vector) {
    if (this.startAngle == this.endAngle) {
      return false;
    }

    double dx = vector.getX() - this.centerX;
    double dz = vector.getZ() - this.centerZ;

    // vector == center
    if (dx == 0 && dz == 0) {
      return true;
    }

    double atan2 = Math.atan2(dz, dx);

    // angle in 3rd quadrant?
    if (atan2 < 0) {
      atan2 += 2 * Math.PI;
    }

    // angle within start/end?
    return atan2 >= this.startAngle && atan2 <= this.endAngle;
  }

  @Override
  public Set<Chunk> getChunks(World world) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Vector getRandomPosition(Random random) {
    throw new UnsupportedOperationException();
  }
}
