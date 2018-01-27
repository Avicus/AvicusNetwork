package net.avicus.atlas.module.executors;

import net.avicus.atlas.Atlas;
import net.avicus.atlas.event.match.MatchOpenEvent;
import net.avicus.atlas.event.match.MatchStateChangeEvent;
import net.avicus.atlas.event.player.PlayerSpawnCompleteEvent;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.EntityVariable;
import net.avicus.atlas.module.checks.variable.ItemVariable;
import net.avicus.atlas.module.checks.variable.MaterialVariable;
import net.avicus.atlas.module.elimination.event.PlayerEliminateEvent;
import net.avicus.atlas.module.objectives.score.event.PointEarnEvent;
import net.avicus.grave.event.PlayerDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerOnGroundEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.material.MaterialData;


/**
 * A massive collection of listeners for different events.
 * This is just here so we don't clutter up another class.
 */
public class ExecutionListenerRegistration {

  public static void register() {
    /**
     * Block Events
     */

    ExecutionDispatch.registerListener("block-break", BlockBreakEvent.class, (e) -> {
      ExecutionDispatch.whenDispatcherExists(dispatcher -> {
        BlockBreakEvent event = (BlockBreakEvent) e;
        CheckContext context = dispatcher.getContext().duplicate();
        context.add(new MaterialVariable(
            new MaterialData(event.getBlock().getType(), event.getBlock().getData())));
        dispatcher.handleEvent(context, event, event.getPlayer(), event.getBlock().getLocation());
      });
    });

    ExecutionDispatch.registerListener("block-explode", BlockExplodeEvent.class, (e) -> {
      ExecutionDispatch.whenDispatcherExists(dispatcher -> {
        BlockExplodeEvent event = (BlockExplodeEvent) e;
        CheckContext context = dispatcher.getContext().duplicate();
        context.add(new MaterialVariable(
            new MaterialData(event.getBlock().getType(), event.getBlock().getData())));
        dispatcher.handleEvent(context, event, null, event.getBlock().getLocation());
      });
    });

    ExecutionDispatch.registerListener("block-place", BlockPlaceEvent.class, (e) -> {
      ExecutionDispatch.whenDispatcherExists(dispatcher -> {
        BlockPlaceEvent event = (BlockPlaceEvent) e;
        CheckContext context = dispatcher.getContext().duplicate();
        context.add(new MaterialVariable(
            new MaterialData(event.getBlock().getType(), event.getBlock().getData())));
        dispatcher.handleEvent(context, event, event.getPlayer(), event.getBlock().getLocation());
      });
    });

    /**
     * Entity Events
     */

    ExecutionDispatch.registerListener("creature-spawn", CreatureSpawnEvent.class, (e) -> {
      ExecutionDispatch.whenDispatcherExists(dispatcher -> {
        CreatureSpawnEvent event = (CreatureSpawnEvent) e;
        CheckContext context = dispatcher.getContext().duplicate();
        context.add(new EntityVariable(event.getEntity()));
        dispatcher.handleEvent(context, event, null, event.getLocation());
      });
    });

    ExecutionDispatch.registerListener("firework-explode", FireworkExplodeEvent.class, (e) -> {
      ExecutionDispatch.whenDispatcherExists(dispatcher -> {
        FireworkExplodeEvent event = (FireworkExplodeEvent) e;
        CheckContext context = dispatcher.getContext().duplicate();
        context.add(new EntityVariable(event.getEntity()));
        dispatcher.handleEvent(context, event, null, event.getEntity().getLocation());
      });
    });

    ExecutionDispatch.registerListener("projectile-hit", ProjectileHitEvent.class, (e) -> {
      ProjectileHitEvent event = (ProjectileHitEvent) e;
      ExecutionDispatch.whenDispatcherExists(
          dispatcher -> dispatcher.handleEvent(event, null, event.getEntity().getLocation()));
    });

    ExecutionDispatch.registerListener("item-drop", PlayerDropItemEvent.class, (e) -> {
      ExecutionDispatch.whenDispatcherExists(dispatcher -> {
        PlayerDropItemEvent event = (PlayerDropItemEvent) e;
        CheckContext context = dispatcher.getContext().duplicate();
        context.add(new ItemVariable(dispatcher.getMatch(), event.getItemDrop().getItemStack()));
        dispatcher
            .handleEvent(context, event, event.getPlayer(), event.getItemDrop().getLocation());
      });
    });

    ExecutionDispatch.registerListener("lighting-strike", LightningStrikeEvent.class, (e) -> {
      LightningStrikeEvent event = (LightningStrikeEvent) e;
      ExecutionDispatch.whenDispatcherExists(
          dispatcher -> dispatcher.handleEvent(event, null, event.getLightning().getLocation()));
    });

    /**
     * Player Events
     */

    ExecutionDispatch.registerListener("item-break", PlayerItemBreakEvent.class, (e) -> {
      ExecutionDispatch.whenDispatcherExists(dispatcher -> {
        PlayerItemBreakEvent event = (PlayerItemBreakEvent) e;
        CheckContext context = dispatcher.getContext().duplicate();
        context.add(new ItemVariable(dispatcher.getMatch(), event.getBrokenItem()));
        dispatcher.handleEvent(context, event, event.getPlayer(), null);
      });
    });

    ExecutionDispatch.registerListener("item-consume", PlayerItemConsumeEvent.class, (e) -> {
      ExecutionDispatch.whenDispatcherExists(dispatcher -> {
        PlayerItemConsumeEvent event = (PlayerItemConsumeEvent) e;
        CheckContext context = dispatcher.getContext().duplicate();
        context.add(new ItemVariable(dispatcher.getMatch(), event.getItem()));
        dispatcher.handleEvent(context, event, event.getPlayer(), event.getPlayer().getLocation());
      });
    });

    ExecutionDispatch.registerListener("item-pickup", PlayerPickupItemEvent.class, (e) -> {
      ExecutionDispatch.whenDispatcherExists(dispatcher -> {
        PlayerPickupItemEvent event = (PlayerPickupItemEvent) e;
        CheckContext context = dispatcher.getContext().duplicate();
        context.add(new ItemVariable(dispatcher.getMatch(), event.getItem().getItemStack()));
        dispatcher.handleEvent(context, event, event.getPlayer(), event.getItem().getLocation());
      });
    });

    ExecutionDispatch.registerListener("toggle-flight", PlayerToggleFlightEvent.class, (e) -> {
      PlayerToggleFlightEvent event = (PlayerToggleFlightEvent) e;
      ExecutionDispatch.whenDispatcherExists(dispatcher -> dispatcher
          .handleEvent(event, event.getPlayer(), event.getPlayer().getLocation()));
    });

    ExecutionDispatch.registerListener("toggle-sneak", PlayerToggleSneakEvent.class, (e) -> {
      PlayerToggleSneakEvent event = (PlayerToggleSneakEvent) e;
      ExecutionDispatch.whenDispatcherExists(dispatcher -> dispatcher
          .handleEvent(event, event.getPlayer(), event.getPlayer().getLocation()));
    });

    ExecutionDispatch.registerListener("toggle-sprint", PlayerToggleSprintEvent.class, (e) -> {
      PlayerToggleSprintEvent event = (PlayerToggleSprintEvent) e;
      ExecutionDispatch.whenDispatcherExists(dispatcher -> dispatcher
          .handleEvent(event, event.getPlayer(), event.getPlayer().getLocation()));
    });

    ExecutionDispatch.registerListener("toggle-on-ground", PlayerOnGroundEvent.class, (e) -> {
      PlayerOnGroundEvent event = (PlayerOnGroundEvent) e;
      ExecutionDispatch.whenDispatcherExists(dispatcher -> dispatcher
          .handleEvent(event, event.getPlayer(), event.getPlayer().getLocation()));
    });

    ExecutionDispatch.registerListener("player-death", PlayerOnGroundEvent.class, (e) -> {
      PlayerDeathEvent event = (PlayerDeathEvent) e;
      ExecutionDispatch.whenDispatcherExists(dispatcher -> dispatcher
          .handleEvent(event, event.getPlayer(), event.getPlayer().getLocation()));
    });

    ExecutionDispatch.registerListener("player-spawn", PlayerSpawnCompleteEvent.class, (e) -> {
      PlayerSpawnCompleteEvent event = (PlayerSpawnCompleteEvent) e;
      ExecutionDispatch.whenDispatcherExists(dispatcher -> dispatcher
          .handleEvent(event, event.getPlayer(), event.getPlayer().getLocation()));
    });

    ExecutionDispatch.registerListener("player-eliminate", PlayerEliminateEvent.class, (e) -> {
      PlayerEliminateEvent event = (PlayerEliminateEvent) e;
      ExecutionDispatch.whenDispatcherExists(dispatcher -> dispatcher
          .handleEvent(event, event.getPlayer(), event.getPlayer().getLocation()));
    });

    /**
     * World Events
     */

    ExecutionDispatch.registerListener("weather-change", WeatherChangeEvent.class, (e) -> {
      WeatherChangeEvent event = (WeatherChangeEvent) e;
      ExecutionDispatch
          .whenDispatcherExists(dispatcher -> dispatcher.handleEvent(event, null, null));
    });

    /**
     * General Match Events
     */

    ExecutionDispatch.registerListener("state-change", MatchStateChangeEvent.class, (e) -> {
      MatchStateChangeEvent event = (MatchStateChangeEvent) e;
      ExecutionDispatch
          .whenDispatcherExists(dispatcher -> dispatcher.handleEvent(event, null, null));
    });

    ExecutionDispatch.registerListener("match-start", MatchStateChangeEvent.class, (e) -> {
      MatchStateChangeEvent event = (MatchStateChangeEvent) e;
      if (event.isChangeToPlaying() && !event.isFromPlaying()) {
        ExecutionDispatch
            .whenDispatcherExists(dispatcher -> dispatcher.handleEvent(event, null, null));
      }
    });

    ExecutionDispatch.registerListener("match-open", MatchOpenEvent.class, (e) -> {
      MatchOpenEvent event = (MatchOpenEvent) e;
      Bukkit.getScheduler().runTaskLater(Atlas.get(), () -> ExecutionDispatch
          .whenDispatcherExists(dispatcher -> dispatcher.handleEvent(event, null, null)), 20);
    });

    ExecutionDispatch.registerListener("point-earn", PointEarnEvent.class, (e) -> {
      PointEarnEvent event = (PointEarnEvent) e;
      ExecutionDispatch.whenDispatcherExists(dispatcher -> dispatcher
          .handleEvent(event, event.getPlayer(), event.getPlayer().getLocation()));
    });
  }
}
