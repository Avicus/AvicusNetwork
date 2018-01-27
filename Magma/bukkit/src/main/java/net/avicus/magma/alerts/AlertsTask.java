package net.avicus.magma.alerts;

import java.util.List;
import java.util.Optional;
import net.avicus.magma.Magma;
import net.avicus.magma.MagmaConfig;
import net.avicus.magma.api.graph.types.alert.Alert;
import net.avicus.magma.network.user.Users;
import net.avicus.magma.util.MagmaTask;
import org.bukkit.entity.Player;
import org.joda.time.DateTime;

public class AlertsTask extends MagmaTask {

  private DateTime lastUpdate = new DateTime();

  public void start() {
    repeatAsync(0, 20 * MagmaConfig.Alerts.getPoll());
  }

  @Override
  public void run() throws Exception {
    List<Alert> alerts = Magma.get().getApiClient().getAlerts().getAlertsAfter(this.lastUpdate);
    this.lastUpdate = new DateTime();

    for (Alert alert : alerts) {
      Optional<Player> player = Users.player(alert.getUserId());

      if (player.isPresent()) {
        Alerts.add(alert);
        Alerts.notify(player.get());
      }
    }
  }
}
