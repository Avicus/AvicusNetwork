package net.avicus.atlas.sets.competitve.objectives.listeners;

import net.avicus.atlas.module.objectives.locatable.LocatableListener;
import net.avicus.atlas.sets.competitve.objectives.destroyable.event.DestroyableDamageEvent;
import net.avicus.atlas.sets.competitve.objectives.destroyable.event.DestroyableRepairEvent;
import net.avicus.atlas.sets.competitve.objectives.destroyable.event.DestroyableTouchEvent;
import net.avicus.atlas.sets.competitve.objectives.destroyable.leakable.event.LeakableLeakEvent;
import net.avicus.atlas.sets.competitve.objectives.destroyable.monument.event.MonumentDestroyEvent;
import net.avicus.atlas.sets.competitve.objectives.flag.events.FlagCaptureEvent;
import net.avicus.atlas.sets.competitve.objectives.flag.events.FlagDropEvent;
import net.avicus.atlas.sets.competitve.objectives.flag.events.FlagPickupEvent;
import net.avicus.atlas.sets.competitve.objectives.flag.events.FlagRecoverEvent;
import net.avicus.atlas.sets.competitve.objectives.flag.events.FlagStealEvent;
import net.avicus.atlas.sets.competitve.objectives.wool.event.WoolPickupEvent;
import net.avicus.atlas.sets.competitve.objectives.wool.event.WoolPlaceEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class LocatableUpdater implements Listener {

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void destroyableDamage(final DestroyableDamageEvent event) {
    LocatableListener.reset(event.getObjective());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void destroyableRepair(final DestroyableRepairEvent event) {
    LocatableListener.reset(event.getObjective());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void destroyableTouch(final DestroyableTouchEvent event) {
    LocatableListener.reset(event.getObjective());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void leakableLeak(final LeakableLeakEvent event) {
    LocatableListener.reset(event.getObjective());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void monumentDestroy(final MonumentDestroyEvent event) {
    LocatableListener.reset(event.getObjective());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void flagCapture(final FlagCaptureEvent event) {
    LocatableListener.reset(event.getObjective());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void flagRecover(final FlagRecoverEvent event) {
    LocatableListener.reset(event.getObjective());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void flagDrop(final FlagDropEvent event) {
    LocatableListener.reset(event.getObjective());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void flagPickup(final FlagPickupEvent event) {
    LocatableListener.reset(event.getObjective());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void flagSteal(final FlagStealEvent event) {
    LocatableListener.reset(event.getObjective());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void woolPickup(final WoolPickupEvent event) {
    LocatableListener.reset(event.getObjective());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void woolPlace(final WoolPlaceEvent event) {
    LocatableListener.reset(event.getObjective());
  }
}
