package net.avicus.atrio;

import lombok.Data;
import net.avicus.magma.util.region.BoundedRegion;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@Data
public class Pad {

  private final BoundedRegion pad;
  private final Vector velocity;

  public void bounce(Player player) {
    player.setVelocity(this.velocity);
    player.playSound(player.getLocation(), Sound.WITHER_SHOOT, 1f, 1f);
  }
}
