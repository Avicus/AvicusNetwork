package net.avicus.hook.gadgets.types.trail;

import java.util.UUID;
import net.avicus.hook.utils.HookTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class TrailTask extends HookTask {

  private final TrailManager manager;

  public TrailTask(TrailManager manager) {
    this.manager = manager;
  }

  public void start() {
    repeat(0, 10);
  }

  @Override
  public void run() {
    // Todo: Look into this task's efficiency

    for (UUID user : this.manager.usersWithTrails()) {
      TrailContext trail = this.manager.getTrail(user).orElse(null);

      if (trail == null) {
        continue;
      }

      Player player = Bukkit.getPlayer(user);

      if (player == null || player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
        continue;
      }

      this.manager.play(trail, player, player.getLocation());
    }
  }
}
