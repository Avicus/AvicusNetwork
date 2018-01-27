package net.avicus.atlas.sets.competitve.objectives.bridges;

import java.util.Optional;
import net.avicus.atlas.command.GameCommands;
import net.avicus.atlas.component.visual.SidebarHook;
import net.avicus.atlas.module.objectives.ObjectivesModule;
import net.avicus.atlas.sets.competitve.objectives.CompetitveRenderer;
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
import net.avicus.atlas.sets.competitve.objectives.hill.event.HillCaptureEvent;
import net.avicus.atlas.sets.competitve.objectives.hill.event.HillCompletionChangeEvent;
import net.avicus.atlas.sets.competitve.objectives.hill.event.HillOwnerChangeEvent;
import net.avicus.atlas.sets.competitve.objectives.wool.event.WoolPickupEvent;
import net.avicus.atlas.sets.competitve.objectives.wool.event.WoolPlaceEvent;
import net.avicus.atlas.util.Messages;
import net.avicus.atlas.util.ObjectiveRenderer;
import net.avicus.compendium.locale.text.Localizable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class SBHook extends SidebarHook {

  private static final ObjectiveRenderer RENDERER = new CompetitveRenderer();

  static {
    GameCommands.RENDERER = RENDERER;
  }

  @Override
  public ObjectiveRenderer getRenderer() {
    return RENDERER;
  }

  @Override
  public Optional<Localizable> getTitle(ObjectivesModule module) {
    ObjectivesBridge bridge = getMatch().getRequiredModule(ObjectivesModule.class)
        .getBridge(ObjectivesBridge.class);

    if (bridge.objectives.isEmpty()) {
      return Optional.empty();
    }

    if (bridge.objectives.size() == bridge.getHills().size()) {
      return Optional.of(Messages.UI_HILLS.with());
    } else if (bridge.objectives.size() == bridge.getMonuments().size()) {
      return Optional.of(Messages.UI_MONUMENTS.with());
    } else if (bridge.objectives.size() == bridge.getWools().size()) {
      return Optional.of(Messages.UI_WOOL.with());
    } else if (bridge.objectives.size() == bridge.getFlags().size()) {
      return Optional.of(Messages.UI_FLAGS.with());
    } else if (bridge.objectives.size() == bridge.getLeakables().size()) {
      return Optional.of(Messages.UI_LEAKABLES.with());
    }

    return Optional.empty();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void destroyableDamage(final DestroyableDamageEvent event) {
    getComponent().delayedUpdate();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void destroyableRepair(final DestroyableRepairEvent event) {
    getComponent().delayedUpdate();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void destroyableTouch(final DestroyableTouchEvent event) {
    getComponent().delayedUpdate();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void leakableLeak(final LeakableLeakEvent event) {
    getComponent().delayedUpdate();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void monumentDestroy(final MonumentDestroyEvent event) {
    getComponent().delayedUpdate();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void flagCapture(final FlagCaptureEvent event) {
    getComponent().delayedUpdate();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void flagRecover(final FlagRecoverEvent event) {
    getComponent().delayedUpdate();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void flagDrop(final FlagDropEvent event) {
    getComponent().delayedUpdate();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void flagPickup(final FlagPickupEvent event) {
    getComponent().delayedUpdate();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void flagSteal(final FlagStealEvent event) {
    getComponent().delayedUpdate();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void hillCapture(final HillCaptureEvent event) {
    getComponent().delayedUpdate();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void hillCompletionChange(final HillCompletionChangeEvent event) {
    getComponent().delayedUpdate();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void hillOwnerChange(final HillOwnerChangeEvent event) {
    getComponent().delayedUpdate();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void woolPickup(final WoolPickupEvent event) {
    getComponent().delayedUpdate();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void woolPlace(final WoolPlaceEvent event) {
    getComponent().delayedUpdate();
  }
}
