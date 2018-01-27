package net.avicus.atlas.module.kits.abilities;

import lombok.ToString;
import net.avicus.atlas.event.player.PlayerSpawnBeginEvent;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.kits.KitAbility;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerOnGroundEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

@ToString
public class DoubleJumpAbility extends KitAbility {

  private final double push;
  private final double icarus;

  public DoubleJumpAbility(Match match, double push, double icarus) {
    super(match);
    this.push = push;
    this.icarus = icarus;
  }

  /**
   * Create the double jump velocity given a player location + direction.
   *
   * @return The velocity vector.
   */
  private Vector createVelocity(Player player) {
    Vector vector = player.getLocation().getDirection().normalize();
    vector.multiply(this.push).setY(this.icarus);
    return vector;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerSpawn(PlayerSpawnBeginEvent event) {
    if (!hasAbility(event.getPlayer(), true)) {
      return;
    }

    event.getPlayer().setAllowFlight(true);
  }

  @EventHandler
  public void onPlayerOnGround(PlayerOnGroundEvent event) {
    if (!event.getOnGround()) {
      return;
    }

    if (!hasAbility(event.getPlayer(), true)) {
      return;
    }

    event.getPlayer().setAllowFlight(true);
  }

  @EventHandler
  public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
    if (!hasAbility(event.getPlayer(), true)) {
      return;
    }

    event.setCancelled(true);
    event.getPlayer().setAllowFlight(false);

    event.getPlayer().setVelocity(createVelocity(event.getPlayer()));
    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENDERDRAGON_WINGS, 1, 1);
  }
}
