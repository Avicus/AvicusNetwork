package net.avicus.magma.util.region;

import org.bukkit.util.Vector;

public interface RepelableRegion extends Region {

  Vector getRepelVector(Vector from);
}
