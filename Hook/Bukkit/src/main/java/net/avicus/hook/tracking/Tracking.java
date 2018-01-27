package net.avicus.hook.tracking;

import java.util.Date;
import net.avicus.atlas.Atlas;
import net.avicus.grave.event.PlayerDeathEvent;
import net.avicus.hook.Hook;
import net.avicus.hook.HookConfig;
import net.avicus.hook.utils.Events;
import net.avicus.hook.utils.HookTask;
import net.avicus.magma.database.model.impl.Death;
import net.avicus.magma.network.user.Users;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Tracking implements Listener {

  public static void init() {
    Tracking tracking = new Tracking();
    Events.register(tracking);

    if (Atlas.get().getLoader().hasModule("competitive-objectives")) {
      Events.register(new CompetitveTracker(tracking));
    }
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    if (!HookConfig.Tracking.isDeaths()) {
      return;
    }

    int user = Users.user(event.getPlayer()).getId();
    int cause = 0;

    if (event.getLifetime().getLastDamage() != null) {
      LivingEntity entity = event.getLifetime().getLastDamage().getInfo().getResolvedDamager();
      if (entity instanceof Player) {
        cause = Users.user((Player) entity).getId();
      }
    }

    Death death = new Death(user, cause, new Date());
    HookTask.of(() -> Hook.database().getDeaths().insert(death).execute()).nowAsync();
  }
}
