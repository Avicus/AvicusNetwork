package net.avicus.atlas.sets.competitve.objectives;

import com.google.common.collect.Lists;
import java.util.logging.Logger;
import lombok.Setter;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.component.AtlasComponentManager;
import net.avicus.atlas.component.visual.SidebarComponent;
import net.avicus.atlas.component.visual.SoundComponent;
import net.avicus.atlas.external.ModuleSet;
import net.avicus.atlas.map.AtlasMapFactory;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.module.executors.ExecutionDispatch;
import net.avicus.atlas.module.groups.ffa.FFAModule;
import net.avicus.atlas.module.groups.teams.TeamsModule;
import net.avicus.atlas.module.objectives.ObjectivesFactory;
import net.avicus.atlas.module.objectives.ObjectivesModule;
import net.avicus.atlas.module.results.ResultsModule;
import net.avicus.atlas.module.shop.PointEarnConfig;
import net.avicus.atlas.module.stats.StatsModule;
import net.avicus.atlas.sets.competitve.objectives.bridges.GroupsBridge.FFABridge;
import net.avicus.atlas.sets.competitve.objectives.bridges.GroupsBridge.TeamsBridge;
import net.avicus.atlas.sets.competitve.objectives.bridges.ObjectivesBridge;
import net.avicus.atlas.sets.competitve.objectives.bridges.ResultsBridge;
import net.avicus.atlas.sets.competitve.objectives.bridges.SBHook;
import net.avicus.atlas.sets.competitve.objectives.bridges.StatsBridge;
import net.avicus.atlas.sets.competitve.objectives.destroyable.DestroyableFactory;
import net.avicus.atlas.sets.competitve.objectives.destroyable.event.DestroyableDamageEvent;
import net.avicus.atlas.sets.competitve.objectives.destroyable.event.DestroyableRepairEvent;
import net.avicus.atlas.sets.competitve.objectives.destroyable.event.DestroyableTouchEvent;
import net.avicus.atlas.sets.competitve.objectives.destroyable.leakable.event.LeakableLeakEvent;
import net.avicus.atlas.sets.competitve.objectives.destroyable.monument.event.MonumentDestroyEvent;
import net.avicus.atlas.sets.competitve.objectives.flag.FlagFactory;
import net.avicus.atlas.sets.competitve.objectives.flag.events.FlagCaptureEvent;
import net.avicus.atlas.sets.competitve.objectives.flag.events.FlagDropEvent;
import net.avicus.atlas.sets.competitve.objectives.flag.events.FlagPickupEvent;
import net.avicus.atlas.sets.competitve.objectives.flag.events.FlagStealEvent;
import net.avicus.atlas.sets.competitve.objectives.hill.HillFactory;
import net.avicus.atlas.sets.competitve.objectives.hill.event.HillCaptureEvent;
import net.avicus.atlas.sets.competitve.objectives.hill.event.HillOwnerChangeEvent;
import net.avicus.atlas.sets.competitve.objectives.listeners.SoundListener;
import net.avicus.atlas.sets.competitve.objectives.phases.PhasesFactory;
import net.avicus.atlas.sets.competitve.objectives.wool.WoolFactory;
import net.avicus.atlas.sets.competitve.objectives.wool.event.WoolPickupEvent;
import net.avicus.atlas.sets.competitve.objectives.wool.event.WoolPlaceEvent;
import net.avicus.atlas.sets.competitve.objectives.zones.ZonesParsingBridge;
import net.avicus.atlas.util.Events;

public class Main extends ModuleSet {

  @Setter
  private Atlas atlas;
  @Setter
  private MatchFactory matchFactory;
  @Setter
  private Logger logger;

  @Override
  public void onEnable() {
    this.logger.info("Enabling competitive objectives set.");
    this.matchFactory.register(PhasesFactory.class);

    addFactories();
    registerExecutionListeners();

    AtlasMapFactory.TYPE_DETECTORS.add(new CompetitiveTypeDetector());

    ResultsModule.BRIDGES.putIfAbsent(ResultsModule.class, Lists.newArrayList());
    ResultsModule.BRIDGES.get(ResultsModule.class).add(ResultsBridge.class);

    ObjectivesModule.BRIDGES.putIfAbsent(ObjectivesModule.class, Lists.newArrayList());
    ObjectivesModule.BRIDGES.get(ObjectivesModule.class).add(ObjectivesBridge.class);

    StatsModule.BRIDGES.putIfAbsent(StatsModule.class, Lists.newArrayList());
    StatsModule.BRIDGES.get(StatsModule.class).add(StatsBridge.class);

    TeamsModule.BRIDGES.putIfAbsent(TeamsModule.class, Lists.newArrayList());
    TeamsModule.BRIDGES.get(TeamsModule.class).add(TeamsBridge.class);
    FFAModule.BRIDGES.putIfAbsent(FFAModule.class, Lists.newArrayList());
    FFAModule.BRIDGES.get(FFAModule.class).add(FFABridge.class);

    PointEarnConfig.CONFIGURABLES.add("destroyable-damage");
    PointEarnConfig.CONFIGURABLES.add("destroyable-touch");
    PointEarnConfig.CONFIGURABLES.add("destroyable-repair");
    PointEarnConfig.CONFIGURABLES.add("leakable-leak");
    PointEarnConfig.CONFIGURABLES.add("monument-destroy");
    PointEarnConfig.CONFIGURABLES.add("flag-capture");
    PointEarnConfig.CONFIGURABLES.add("flag-pickup");
    PointEarnConfig.CONFIGURABLES.add("flag-steal");
    PointEarnConfig.CONFIGURABLES.add("hill-capture");
    PointEarnConfig.CONFIGURABLES.add("wool-pickup");
    PointEarnConfig.CONFIGURABLES.add("wool-place");

    this.logger.info("Enabled competitive objectives set.");
  }

  @Override
  public void onDisable() {
    ObjectivesFactory.FACTORY_MAP.remove("hill");
    ObjectivesFactory.FACTORY_MAP.remove("monument");
    ObjectivesFactory.FACTORY_MAP.remove("leakable");
    ObjectivesFactory.FACTORY_MAP.remove("wool");
    ObjectivesFactory.FACTORY_MAP.remove("flag");

    this.logger.info("Disabled competitive objectives set.");
  }

  @Override
  public void onComponentsEnable(AtlasComponentManager componentManager) {
    if (componentManager.hasModule(SoundComponent.class)) {
      this.logger.info("Registered sound hook.");
      Events.register(new SoundListener(componentManager.get(SoundComponent.class)));
    }
    if (componentManager.hasModule(SidebarComponent.class)) {
      SidebarComponent.HOOKS.add(new SBHook());
      this.logger.info("Registered sidebar hook.");
    }
  }

  private void addFactories() {
    ObjectivesFactory.FACTORY_MAP.put("hill", new HillFactory());
    ObjectivesFactory.FACTORY_MAP.put("monument", new DestroyableFactory());
    ObjectivesFactory.FACTORY_MAP.put("leakable", new DestroyableFactory());
    ObjectivesFactory.FACTORY_MAP.put("wool", new WoolFactory());
    ObjectivesFactory.FACTORY_MAP.put("flag", new FlagFactory());

    new ZonesParsingBridge().buildBridge();
  }

  private void registerExecutionListeners() {
    /**
     * Objective Completion Events
     */

    ExecutionDispatch.registerListener("leakable-leak", LeakableLeakEvent.class, (e) -> {
      LeakableLeakEvent event = (LeakableLeakEvent) e;
      ExecutionDispatch.whenDispatcherExists(dispatcher -> dispatcher
          .handleEvent(event, event.getPlayers().get(0), event.getPlayers().get(0).getLocation()));
    });

    ExecutionDispatch.registerListener("monument-destroy", MonumentDestroyEvent.class, (e) -> {
      MonumentDestroyEvent event = (MonumentDestroyEvent) e;
      ExecutionDispatch.whenDispatcherExists(dispatcher -> dispatcher
          .handleEvent(event, event.getPlayers().get(0), event.getPlayers().get(0).getLocation()));
    });

    ExecutionDispatch.registerListener("flag-capture", FlagCaptureEvent.class, (e) -> {
      FlagCaptureEvent event = (FlagCaptureEvent) e;
      ExecutionDispatch.whenDispatcherExists(dispatcher -> dispatcher
          .handleEvent(event, event.getPlayers().get(0), event.getPlayers().get(0).getLocation()));
    });

    ExecutionDispatch.registerListener("hill-capture", HillCaptureEvent.class, (e) -> {
      HillCaptureEvent event = (HillCaptureEvent) e;
      ExecutionDispatch
          .whenDispatcherExists(dispatcher -> dispatcher.handleEvent(event, null, null));
    });

    ExecutionDispatch.registerListener("wool-place", WoolPlaceEvent.class, (e) -> {
      WoolPlaceEvent event = (WoolPlaceEvent) e;
      ExecutionDispatch.whenDispatcherExists(dispatcher -> dispatcher
          .handleEvent(event, event.getPlayers().get(0), event.getPlayers().get(0).getLocation()));
    });

    /**
     * Objective Touch Events
     */

    ExecutionDispatch.registerListener("destroyable-touch", DestroyableTouchEvent.class, (e) -> {
      DestroyableTouchEvent event = (DestroyableTouchEvent) e;
      ExecutionDispatch.whenDispatcherExists(dispatcher -> dispatcher
          .handleEvent(event, event.getPlayer(), event.getPlayer().getLocation()));
    });

    ExecutionDispatch.registerListener("wool-pickup", WoolPickupEvent.class, (e) -> {
      WoolPickupEvent event = (WoolPickupEvent) e;
      ExecutionDispatch.whenDispatcherExists(dispatcher -> dispatcher
          .handleEvent(event, event.getPlayer(), event.getPlayer().getLocation()));
    });

    /**
     * Objective State Change Events
     */

    ExecutionDispatch.registerListener("destroyable-damage", DestroyableDamageEvent.class, (e) -> {
      DestroyableDamageEvent event = (DestroyableDamageEvent) e;
      ExecutionDispatch.whenDispatcherExists(dispatcher -> dispatcher
          .handleEvent(event, event.getInfo().getActor(),
              event.getInfo().getActor().getLocation()));
    });

    ExecutionDispatch.registerListener("destroyable-repair", DestroyableRepairEvent.class, (e) -> {
      DestroyableRepairEvent event = (DestroyableRepairEvent) e;
      ExecutionDispatch.whenDispatcherExists(dispatcher -> dispatcher
          .handleEvent(event, event.getPlayer(), event.getPlayer().getLocation()));
    });

    ExecutionDispatch.registerListener("flag-drop", FlagDropEvent.class, (e) -> {
      FlagDropEvent event = (FlagDropEvent) e;
      ExecutionDispatch
          .whenDispatcherExists(dispatcher -> dispatcher.handleEvent(event, null, null));
    });

    ExecutionDispatch.registerListener("flag-pickup", FlagPickupEvent.class, (e) -> {
      FlagPickupEvent event = (FlagPickupEvent) e;
      ExecutionDispatch.whenDispatcherExists(dispatcher -> dispatcher
          .handleEvent(event, event.getPlayer(), event.getPlayer().getLocation()));
    });

    ExecutionDispatch.registerListener("flag-steal", FlagStealEvent.class, (e) -> {
      FlagStealEvent event = (FlagStealEvent) e;
      ExecutionDispatch.whenDispatcherExists(dispatcher -> dispatcher
          .handleEvent(event, event.getPlayer(), event.getPlayer().getLocation()));
    });

    ExecutionDispatch.registerListener("hill-owner-change", HillOwnerChangeEvent.class, (e) -> {
      HillOwnerChangeEvent event = (HillOwnerChangeEvent) e;
      ExecutionDispatch
          .whenDispatcherExists(dispatcher -> dispatcher.handleEvent(event, null, null));
    });
  }
}
