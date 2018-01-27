package net.avicus.atlas.sets.competitve.objectives.bridges;

import java.time.Instant;
import net.avicus.atlas.module.ModuleBridge;
import net.avicus.atlas.module.stats.StatsModule;
import net.avicus.atlas.sets.competitve.objectives.actions.destroyable.base.PlayerDamageDestoyableAction;
import net.avicus.atlas.sets.competitve.objectives.actions.destroyable.base.PlayerRepairDestoyableAction;
import net.avicus.atlas.sets.competitve.objectives.actions.destroyable.leakable.PlayerLeakLeakableAction;
import net.avicus.atlas.sets.competitve.objectives.actions.destroyable.monument.PlayerDestroyMonumentAction;
import net.avicus.atlas.sets.competitve.objectives.actions.flag.PlayerCaptureFlagAction;
import net.avicus.atlas.sets.competitve.objectives.actions.flag.PlayerDropFlagAction;
import net.avicus.atlas.sets.competitve.objectives.actions.flag.PlayerPickupFlagAction;
import net.avicus.atlas.sets.competitve.objectives.actions.hill.CompetitorCaptureHillAction;
import net.avicus.atlas.sets.competitve.objectives.actions.hill.PlayerCaptureHillAssistAction;
import net.avicus.atlas.sets.competitve.objectives.actions.wool.PlayerPlaceWoolAction;
import net.avicus.atlas.sets.competitve.objectives.actions.wool.PlayerTouchWoolAction;
import net.avicus.atlas.sets.competitve.objectives.destroyable.event.DestroyableDamageEvent;
import net.avicus.atlas.sets.competitve.objectives.destroyable.event.DestroyableRepairEvent;
import net.avicus.atlas.sets.competitve.objectives.destroyable.leakable.event.LeakableLeakEvent;
import net.avicus.atlas.sets.competitve.objectives.destroyable.monument.event.MonumentDestroyEvent;
import net.avicus.atlas.sets.competitve.objectives.flag.events.FlagCaptureEvent;
import net.avicus.atlas.sets.competitve.objectives.flag.events.FlagDropEvent;
import net.avicus.atlas.sets.competitve.objectives.flag.events.FlagPickupEvent;
import net.avicus.atlas.sets.competitve.objectives.hill.event.HillCaptureEvent;
import net.avicus.atlas.sets.competitve.objectives.wool.event.WoolPickupEvent;
import net.avicus.atlas.sets.competitve.objectives.wool.event.WoolPlaceEvent;
import net.avicus.atlas.util.Events;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class StatsBridge implements ModuleBridge<StatsModule>, Listener {

  private final StatsModule module;

  public StatsBridge(StatsModule module) {
    this.module = module;
  }

  @Override
  public void onOpen(StatsModule module) {
    Events.register(this);
  }

  @Override
  public void onClose(StatsModule module) {
    Events.unregister(this);
  }

  // ----------------
  // -- Objectives --
  // ----------------

  // ---------------------
  // ---- Destroyable ----
  // ---------------------

  @EventHandler(priority = EventPriority.MONITOR)
  public void onDestroyableDamage(DestroyableDamageEvent event) {
    this.module.getStore().store(
        new PlayerDamageDestoyableAction(event.getObjective(), event.getInfo(), Instant.now()));
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onDestroyableRepair(DestroyableRepairEvent event) {
    this.module.getStore().store(
        new PlayerRepairDestoyableAction(event.getObjective(), event.getPlayer(), Instant.now()));
  }

  // ------------------
  // ---- Leakable ----
  // ------------------

  @EventHandler(priority = EventPriority.MONITOR)
  public void onLeakableLeak(LeakableLeakEvent event) {
    this.module.getStore()
        .store(new PlayerLeakLeakableAction(event.getObjective(), event.getInfo(), Instant.now()));
  }

  // ------------------
  // ---- Monument ----
  // ------------------

  @EventHandler(priority = EventPriority.MONITOR)
  public void onMonumentDestroy(MonumentDestroyEvent event) {
    this.module.getStore().store(
        new PlayerDestroyMonumentAction(event.getObjective(), event.getInfo(), Instant.now()));
  }

  // --------------
  // ---- Flag ----
  // --------------

  @EventHandler(priority = EventPriority.MONITOR)
  public void onFlagCapture(FlagCaptureEvent event) {
    this.module.getStore().store(
        new PlayerCaptureFlagAction(event.getObjective(), event.getPlayers().get(0),
            Instant.now()));
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onFlagDrop(FlagDropEvent event) {
    this.module.getStore()
        .store(new PlayerDropFlagAction(event.getObjective(), event.getDropper(), Instant.now()));
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onFlagPickup(FlagPickupEvent event) {
    this.module.getStore()
        .store(new PlayerPickupFlagAction(event.getObjective(), event.getPlayer(), Instant.now()));
  }

  // --------------
  // ---- Hill ----
  // --------------

  @EventHandler(priority = EventPriority.MONITOR)
  public void onHillCapture(HillCaptureEvent event) {
    if (event.getNewOwner().isPresent()) {
      Instant when = Instant.now();
      this.module.getStore().store(
          new CompetitorCaptureHillAction(event.getObjective(), event.getNewOwner().get(), when));
      for (Player player : event.getPlayers()) {
        this.module.getStore()
            .store(new PlayerCaptureHillAssistAction(event.getObjective(), player, when));
      }
    }
  }

  // --------------
  // ---- Wool ----
  // --------------

  @EventHandler(priority = EventPriority.MONITOR)
  public void onWoolPlace(WoolPlaceEvent event) {
    this.module.getStore().store(
        new PlayerPlaceWoolAction(event.getObjective(), event.getPlayers().get(0), Instant.now()));
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onWoolCapture(WoolPickupEvent event) {
    this.module.getStore().store(
        new PlayerTouchWoolAction(event.getObjective(), event.getPlayer(), Instant.now(), false));
  }
}
