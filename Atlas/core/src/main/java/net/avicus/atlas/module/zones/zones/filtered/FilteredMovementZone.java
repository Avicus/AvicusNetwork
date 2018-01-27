package net.avicus.atlas.module.zones.zones.filtered;

import com.google.common.collect.ArrayListMultimap;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;
import lombok.ToString;
import net.avicus.atlas.event.match.MatchCloseEvent;
import net.avicus.atlas.event.match.MatchOpenEvent;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.module.zones.Zone;
import net.avicus.atlas.module.zones.ZoneMessage;
import net.avicus.atlas.util.AtlasTask;
import net.avicus.magma.util.region.Region;
import net.avicus.magma.util.region.RepelableRegion;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.Vector;
import org.joda.time.Instant;
import tc.oc.tracker.event.PlayerCoarseMoveEvent;

@ToString(callSuper = true)
public class FilteredMovementZone extends Zone {

  private final Optional<Check> enter;
  private final Optional<Check> leave;

  private final ArrayListMultimap<UUID, Instant> attempts; // stores enter attempts in the past 5 seconds
  private final AtlasTask clearAttempts;

  public FilteredMovementZone(Match match, Region region, Optional<ZoneMessage> message,
      Optional<Check> enter, Optional<Check> leave) {
    super(match, region, message);
    this.enter = enter;
    this.leave = leave;

    this.attempts = ArrayListMultimap.create();
    this.clearAttempts = new AtlasTask() {
      @Override
      public void run() {
        Iterator<Instant> iterator = attempts.values().iterator();
        Instant fiveSecs = Instant.now().minus(5000);
        while (iterator.hasNext()) {
          Instant instant = iterator.next();
          if (instant.isBefore(fiveSecs)) {
            iterator.remove();
          }
        }
      }
    };
  }

  @Override
  public boolean isActive() {
    return this.enter.isPresent() || this.leave.isPresent();
  }

  @EventHandler
  public void onMatchOpen(MatchOpenEvent event) {
    this.clearAttempts.cancel0();
    this.clearAttempts.repeat(0, 20 * 10);
  }

  @EventHandler
  public void onMatchOpen(MatchCloseEvent event) {
    this.clearAttempts.cancel0();
  }


  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onTP(PlayerTeleportEvent event) {
    event.setCancelled(handleMove(event.getPlayer(), event.getFrom(), event.getTo()));
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onMove(VehicleMoveEvent event) {
    if (event.getVehicle().getPassenger() != null && event.getVehicle()
        .getPassenger() instanceof Player) {
      if (handleMove((Player) event.getVehicle().getPassenger(), event.getFrom(), event.getTo())) {
        pushAway(event.getVehicle(), event.getTo());
      }
    }
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onCoarseMove(PlayerCoarseMoveEvent event) {
    event.setCancelled(handleMove(event.getPlayer(), event.getFrom(), event.getTo()));
  }

  public boolean handleMove(Player player, Location fromLoc, Location toLoc) {
    try {
      if (isObserving(this.match, player)) {
        return false;
      }
    } catch (RuntimeException e) {
      return false;
    }
    boolean from = getRegion().contains(fromLoc);

    boolean to = getRegion().contains(toLoc);

    // ignore if they are not coming from this region
    if (!from && !to) {
      return false;
    }

    if (from && !to && this.leave.isPresent()) {
      CheckContext context = new CheckContext(this.match);
      context.add(new PlayerVariable(player));
      if (this.leave.get().test(context).fails()) {
        message(player);
        pushAway(player, toLoc);
        attempt(player);
        return true;
      }
    }

    if (to && !from && this.enter.isPresent()) {
      CheckContext context = new CheckContext(this.match);
      context.add(new PlayerVariable(player));
      if (this.enter.get().test(context).fails()) {
        message(player);
        pushAway(player, fromLoc);
        attempt(player);
        return true;
      }
    }

    return false;
  }

  private void pushAway(Entity player, Location from) {
    if (getRegion() instanceof RepelableRegion) {
      RepelableRegion repelable = (RepelableRegion) getRegion();
      Vector velocity = repelable.getRepelVector(from.toVector()).normalize().multiply(-0.2);
      player.setVelocity(velocity);
    }
  }

  private void attempt(Player player) {
    int attempts = this.attempts.get(player.getUniqueId()).size() + 1;
    this.attempts.put(player.getUniqueId(), Instant.now());

    if (attempts >= 10) {
      player.damage(1);
    }
  }
}
