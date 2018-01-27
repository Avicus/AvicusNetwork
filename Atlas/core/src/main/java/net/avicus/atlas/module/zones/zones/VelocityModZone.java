package net.avicus.atlas.module.zones.zones;

import java.util.Optional;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.zones.Zone;
import net.avicus.atlas.module.zones.ZoneMessage;
import net.avicus.magma.util.region.Region;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;
import tc.oc.tracker.event.PlayerCoarseMoveEvent;

@ToString(callSuper = true)
public class VelocityModZone extends Zone {

  private final Optional<Vector> velocity;
  private final Optional<Double> push;
  private final Optional<Double> icarus;

  public VelocityModZone(Match match, Region region, Optional<ZoneMessage> message,
      Optional<Vector> velocity, Optional<Double> push, Optional<Double> icarus) {
    super(match, region, message);
    this.velocity = velocity;
    this.push = push;
    this.icarus = icarus;
  }

  @Override
  public boolean isActive() {
    return this.velocity.isPresent() ||
        this.push.isPresent() ||
        this.icarus.isPresent();
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onMove(PlayerCoarseMoveEvent event) {
    handle(event.getPlayer(), event.getFrom(), event.getTo());
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onTP(PlayerTeleportEvent event) {
    handle(event.getPlayer(), event.getFrom(), event.getTo());
  }

  public void handle(Player player, Location fromLoc, Location toLoc) {
    boolean from = getRegion().contains(fromLoc);

    if (from) {
      return;
    }

    boolean to = getRegion().contains(toLoc);

    if (!to) {
      return;
    }

    Vector velocity = this.velocity.orElse(new Vector());

    if (this.icarus.isPresent()) {
      velocity.setY(this.icarus.get());
    }

    if (this.push.isPresent()) {
      Vector direction = player.getLocation().getDirection().normalize();
      direction.multiply(this.push.get());
      velocity.add(direction);
    }

    player.setVelocity(velocity);
  }
}
