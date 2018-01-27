package net.avicus.atlas.component.util;

import net.avicus.atlas.Atlas;
import net.avicus.atlas.util.AtlasTask;
import net.avicus.magma.module.Module;
import org.bukkit.entity.Arrow;

public class ArrowRemovalComponent extends AtlasTask implements Module {

  private final int FIRE_DELAY = 20 * 50;
  private final int DELAY = 20 * 120;

  @Override
  public void enable() {
    this.repeat(0, 120); // Every 2 seconds
  }

  @Override
  public void disable() {
    this.cancel0();
  }

  @Override
  public void run() {
    Atlas.performOnMatch(m -> {
      m.getWorld().getEntitiesByClass(Arrow.class).forEach(a -> {
        if (a.getFireTicks() > FIRE_DELAY || a.getTicksLived() > DELAY) {
          a.remove();
        }
      });
    });
  }
}
