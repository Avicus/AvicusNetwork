package net.avicus.atlas.sets.competitve.objectives.flag;

import java.util.List;
import java.util.Optional;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.module.objectives.ObjectivesModule;
import net.avicus.atlas.util.AtlasTask;
import net.avicus.atlas.util.Worlds;
import org.bukkit.Location;

public class FlagHighlightTask extends AtlasTask {

  private final ObjectivesModule module;
  private final List<FlagObjective> flags;

  public FlagHighlightTask(ObjectivesModule module, List<FlagObjective> flags) {
    this.module = module;
    this.flags = flags;
  }

  public void start() {
    repeat(0, 3);
  }

  @Override
  public void run() {
    boolean anyCarried = false;

    for (FlagObjective flag : this.flags) {
      Optional<Location> optional = flag.getCurrentLocation();
      if (optional.isPresent() && flag.isHighlightHolder() && (!flag.getHighlightDelay().isPresent()
          || (flag.getHoldingTime().isPresent() && flag.getHoldingTime().get()
          .isAfter(flag.getHighlightDelay().get().getMillis())))) {
        Location curr = optional.get().clone().add(0, 2.0, 0);
        double x = curr.getX();
        double z = curr.getZ();

        while (curr.getY() < optional.get().getY() + 46) {
          curr.setY(curr.getY() + 1);
          curr.setX(x + 0.4 * Math.cos(curr.getY()));
          curr.setZ(z + 0.4 * Math.sin(curr.getY()));

          Worlds.playColoredParticle(curr, 256, flag.getColor().getColor());
        }
      }

      if (flag.isCarried()) {
        anyCarried = true;
      }

      // Reward
      if (flag.isCarried() && flag.getCarryingPoints() > 0 && flag.getHoldingTime().isPresent()) {
        flag.reward();
      }
    }

    // Updates the flashing carrying symbol
    if (anyCarried) {
      Atlas.get().getSideBar().syncUpdate();
    }
  }
}