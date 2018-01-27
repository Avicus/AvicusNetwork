package net.avicus.atlas.sets.competitve.objectives.listeners;

import net.avicus.atlas.component.visual.SoundComponent;
import net.avicus.atlas.sets.competitve.objectives.destroyable.leakable.event.LeakableLeakEvent;
import net.avicus.atlas.sets.competitve.objectives.destroyable.monument.event.MonumentDestroyEvent;
import net.avicus.atlas.sets.competitve.objectives.flag.events.FlagCaptureEvent;
import net.avicus.atlas.sets.competitve.objectives.hill.event.HillCaptureEvent;
import net.avicus.atlas.sets.competitve.objectives.wool.event.WoolPlaceEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SoundListener implements Listener {

  private final SoundComponent soundComponent;

  public SoundListener(SoundComponent soundComponent) {
    this.soundComponent = soundComponent;
  }

  @EventHandler
  public void leakableLeak(final LeakableLeakEvent event) {
    this.soundComponent.objectiveComplete();
  }

  @EventHandler
  public void monumentDestroy(final MonumentDestroyEvent event) {
    this.soundComponent.objectiveComplete();
  }

  @EventHandler
  public void flagCapture(final FlagCaptureEvent event) {
    this.soundComponent.objectiveComplete();
  }

  @EventHandler
  public void hillCapture(final HillCaptureEvent event) {
    this.soundComponent.objectiveComplete();
  }

  @EventHandler
  public void woolPlace(final WoolPlaceEvent event) {
    this.soundComponent.objectiveComplete();
  }
}
