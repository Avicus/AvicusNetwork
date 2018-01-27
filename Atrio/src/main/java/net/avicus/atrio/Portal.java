package net.avicus.atrio;

import java.util.Optional;
import java.util.Random;
import lombok.Data;
import net.avicus.magma.util.region.BoundedRegion;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.joda.time.DateTime;

@Data
public class Portal {

  private final PortalType type;
  private final Optional<DateTime> open;
  private final Optional<DateTime> close;
  private final BoundedRegion enter;
  private final BoundedRegion exit;
  private final float yaw;
  private final float pitch;

  public void teleport(Player player) {
    if (!canUse()) {
      player.sendMessage(ChatColor.RED + "This portal cannot be used at this time!");
      return;
    }

    player.teleport(this.getExit().getRandomPosition(new Random())
        .toLocation(AtrioPlugin.getInstance().getWorld(), this.getYaw(), this.getPitch()));
    player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 1.4f);
  }

  private boolean canUse() {
    return this.open.map(d -> d.isBefore(new DateTime())).orElse(true) && this.close
        .map(d -> d.isAfter(new DateTime())).orElse(true);
  }

  public enum PortalType {
    CLICK, ENTER
  }
}