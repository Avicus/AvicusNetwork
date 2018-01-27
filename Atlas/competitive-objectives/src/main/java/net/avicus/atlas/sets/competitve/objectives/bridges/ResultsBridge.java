package net.avicus.atlas.sets.competitve.objectives.bridges;

import net.avicus.atlas.module.ModuleBridge;
import net.avicus.atlas.module.results.ResultsModule;
import net.avicus.atlas.sets.competitve.objectives.destroyable.event.DestroyableDamageEvent;
import net.avicus.atlas.sets.competitve.objectives.destroyable.event.DestroyableRepairEvent;
import net.avicus.atlas.sets.competitve.objectives.destroyable.event.DestroyableTouchEvent;
import net.avicus.atlas.sets.competitve.objectives.destroyable.leakable.event.LeakableLeakEvent;
import net.avicus.atlas.sets.competitve.objectives.destroyable.monument.event.MonumentDestroyEvent;
import net.avicus.atlas.sets.competitve.objectives.flag.events.FlagCaptureEvent;
import net.avicus.atlas.sets.competitve.objectives.flag.events.FlagPickupEvent;
import net.avicus.atlas.sets.competitve.objectives.hill.event.HillCaptureEvent;
import net.avicus.atlas.sets.competitve.objectives.hill.event.HillCompletionChangeEvent;
import net.avicus.atlas.sets.competitve.objectives.wool.event.WoolPickupEvent;
import net.avicus.atlas.sets.competitve.objectives.wool.event.WoolPlaceEvent;
import net.avicus.atlas.util.Events;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ResultsBridge implements ModuleBridge<ResultsModule>, Listener {

  private final ResultsModule module;

  public ResultsBridge(ResultsModule module) {
    this.module = module;
  }

  @Override
  public void onOpen(ResultsModule module) {
    Events.register(this);
  }

  @Override
  public void onClose(ResultsModule module) {
    Events.unregister(this);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void destroyableDamage(final DestroyableDamageEvent event) {
    module.syncCheck();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void destroyableRepair(final DestroyableRepairEvent event) {
    module.syncCheck();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void destroyableTouch(final DestroyableTouchEvent event) {
    module.syncCheck();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void leakableLeak(final LeakableLeakEvent event) {
    module.syncCheck();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void monumentDestroy(final MonumentDestroyEvent event) {
    module.syncCheck();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void flagCapture(final FlagCaptureEvent event) {
    module.syncCheck();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void flagPickup(final FlagPickupEvent event) {
    module.syncCheck();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void hillCapture(final HillCaptureEvent event) {
    module.syncCheck();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void hillCompletionChange(final HillCompletionChangeEvent event) {
    module.syncCheck();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void woolPickup(final WoolPickupEvent event) {
    module.syncCheck();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void woolPlace(final WoolPlaceEvent event) {
    module.syncCheck();
  }
}
