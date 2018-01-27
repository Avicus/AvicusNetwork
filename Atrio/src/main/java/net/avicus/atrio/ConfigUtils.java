package net.avicus.atrio;

import java.util.ArrayList;
import java.util.List;
import net.avicus.compendium.config.Config;
import net.avicus.magma.util.region.BoundedRegion;
import net.avicus.magma.util.region.modifiers.BoundedJoinRegion;
import net.avicus.magma.util.region.shapes.BlockRegion;
import net.avicus.magma.util.region.shapes.CircleRegion;
import net.avicus.magma.util.region.shapes.CuboidRegion;
import net.avicus.magma.util.region.shapes.CylinderRegion;
import org.bukkit.util.Vector;

public class ConfigUtils {

  public static Vector vecFromString(String vec) {
    String[] parts = vec.split(",");
    return new Vector(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]),
        Double.parseDouble(parts[2]));
  }

  public static BoundedRegion parseBounded(Config config) {
    BoundedRegion res = null;
    switch (config.getString("region")) {
      case "block":
        res = new BlockRegion(vecFromString(config.getString("location")));
        break;
      case "cuboid":
        res = new CuboidRegion(vecFromString(config.getString("min")),
            vecFromString(config.getString("max")));
        break;
      case "circle":
        int radius = config.getInt("radius");
        res = new CircleRegion(vecFromString(config.getString("origin")), radius);
        break;
      case "cylinder":
        int rad = config.getInt("radius");
        int height = config.getInt("height");
        res = new CylinderRegion(vecFromString(config.getString("base")), rad, height);
        break;
      case "join":
        List<BoundedRegion> regions = new ArrayList<>();
        config.getList("regions", Config.class).forEach(ConfigUtils::parseBounded);
        res = new BoundedJoinRegion(regions);
        break;
    }
    if (res == null) {
      throw new RuntimeException("Region type not supported.");
    }
    return res;
  }

}
