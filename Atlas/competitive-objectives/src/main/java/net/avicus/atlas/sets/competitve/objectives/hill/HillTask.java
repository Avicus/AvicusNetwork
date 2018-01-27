package net.avicus.atlas.sets.competitve.objectives.hill;

import java.util.List;
import net.avicus.atlas.module.objectives.ObjectivesModule;
import net.avicus.atlas.module.shop.PlayerEarnPointEvent;
import net.avicus.atlas.sets.competitve.objectives.hill.event.HillCaptureEvent;
import net.avicus.atlas.util.AtlasTask;
import net.avicus.atlas.util.Events;

public class HillTask extends AtlasTask {

  private final ObjectivesModule manager;
  private final List<HillObjective> hills;

  public HillTask(ObjectivesModule manager) {
    super();
    this.manager = manager;
    this.hills = manager.getObjectivesByType(HillObjective.class);
  }

  public void start() {
    repeat(0, 5);
  }

  @Override
  public void run() {
    for (HillObjective hill : this.hills) {
      boolean wasCompleted = hill.isCompleted();

      hill.tick(5);

      if (hill.isPermanent()) {
        if (!wasCompleted && hill.isCompleted()) {
          this.manager.broadcastCompletion(hill, hill.getOwner().get().getGroup());

          HillCaptureEvent captureEvent = new HillCaptureEvent(
              hill.getOwner().get().getGroup().getPlayers(), hill, hill.getOwner());
          Events.call(captureEvent);
          hill.getOwner().get().getGroup().getPlayers()
              .forEach(p -> Events.call(new PlayerEarnPointEvent(p, "hill-capture")));
        }
      }
    }
  }
}
