package net.avicus.atlas.module.objectives.locatable;

import java.util.List;
import net.avicus.atlas.module.objectives.Objective;
import net.avicus.atlas.util.Events;
import net.avicus.grave.event.PlayerDeathByPlayerEvent;
import net.avicus.magma.util.distance.DistanceCalculationMetric;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import tc.oc.tracker.event.PlayerCoarseMoveEvent;

public class LocatableListener implements Listener {

  private final List<? extends LocatableObjective> objectives;

  public LocatableListener(List<? extends LocatableObjective> objectives) {
    this.objectives = objectives;
  }

  public static void reset(Objective objective) {
    if (objective instanceof LocatableObjective) {
      if (((LocatableObjective) objective).resetDistance()) {
        Events.call(new LocatableUpdateDistanceEvent(objective));
      }
    }
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onMove(PlayerCoarseMoveEvent event) {
    this.objectives.forEach(o -> {
      if (o.getDistanceCalculationMetricType(event.getPlayer())
          == DistanceCalculationMetric.Type.PLAYER && o
          .updateDistance(event.getPlayer(), event.getTo())) {
        Events.call(new LocatableUpdateDistanceEvent(o));
      }
    });
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onKill(PlayerDeathByPlayerEvent event) {
    this.objectives.forEach(o -> {
      if (o.getDistanceCalculationMetricType(event.getCause())
          == DistanceCalculationMetric.Type.KILL && o
          .updateDistance(event.getCause(), event.getLocation())) {
        Events.call(new LocatableUpdateDistanceEvent(o));
      }
    });
  }
}
