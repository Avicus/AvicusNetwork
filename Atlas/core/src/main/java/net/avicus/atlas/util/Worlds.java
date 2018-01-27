package net.avicus.atlas.util;

import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

public class Worlds {

  // Simplistic yaw/blockface code from https://bukkit.org/threads/banner-rotation.374049/#post-3171315
  private static final BlockFace[] axis = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH,
      BlockFace.WEST};
  private static final BlockFace[] radial = {BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST,
      BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST,
      BlockFace.NORTH_WEST};

  public static BlockFace toBlockFace(float yaw) {
    return toBlockFace(yaw, true);
  }

  public static BlockFace toBlockFace(float yaw, boolean useSubCardinalDirections) {
    if (useSubCardinalDirections) {
      return radial[Math.round(yaw / 45f) & 0x7];
    } else {
      return axis[Math.round(yaw / 90f) & 0x3];
    }
  }

  public static void playColoredParticle(Location location, int viewRadius, Color color) {
    playColoredParticle(location, viewRadius, color.getRed(), color.getGreen(), color.getBlue());
  }

  public static void playColoredParticle(Location location, int viewRadius, int red, int green,
      int blue) {
    float r = (float) red / 255.0F;
    float g = (float) green / 255.0F;
    float b = (float) blue / 255.0F;
    location.getWorld().spigot()
        .playEffect(location, Effect.COLOURED_DUST, 0, 0, r, g, b, 1, 0, viewRadius);
  }
}
