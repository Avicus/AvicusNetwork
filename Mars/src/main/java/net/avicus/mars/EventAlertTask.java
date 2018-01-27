package net.avicus.mars;

import java.util.Objects;
import java.util.Optional;

public class EventAlertTask implements Runnable {

  private Optional<CompetitiveEvent> previousEvent = null;

  @Override
  public void run() {
    Optional<CompetitiveEvent> newEvent = MarsPlugin.getInstance().getCurrentEvent();

    if (!Objects.equals(this.previousEvent, newEvent)) {
      if (newEvent.isPresent()) {
        MarsPlugin.getInstance().getLogger().warning("Event found: " + newEvent);
      } else {
        MarsPlugin.getInstance().getLogger().warning("No events currently.");
      }

      this.previousEvent = newEvent;
    }
  }
}
