package net.avicus.magma;


import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HttpsURLConnection;
import lombok.Getter;
import lombok.Setter;
import net.avicus.magma.database.Database;
import net.avicus.magma.database.model.impl.Session;
import net.avicus.magma.database.model.impl.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class StatusCheckTask implements Runnable, Listener {

  private final Magma plugin;
  private final Database database;
  private final List<Boolean> statusHistory;
  @Getter
  @Setter
  private boolean sessionsOnline = true;

  public StatusCheckTask(Magma plugin, Database database) {
    this.plugin = plugin;
    this.database = database;
    this.statusHistory = new ArrayList<>();
  }

  public StatusCheckTask start() {
    this.plugin.getProxy().getScheduler().schedule(this.plugin, this, 0, 100, TimeUnit.SECONDS);
    return this;
  }

  @EventHandler
  public void onPreLogin(PreLoginEvent event) throws SQLException {
    if (event.getConnection().getVersion() < 4) {
      event.setCancelReason(ChatColor.translateAlternateColorCodes('&',
          "&c&nOutdated Minecraft Version!\n\n&fPlease update your Minecraft version \n&fto 1.7 or greater to join Avicus!"));
      event.setCancelled(true);
      return;
    }

    // ==================
    // == Offline Mode ==
    // ==================
    // To sum this up:
    // 1. Find user who has the same username.
    // 2. Check their latest 15 logins.
    // 3. Do any IPs match this one?
    // 4. -> Set UUID to the one in the database
    // 5. -> Set offline mode (don't check Mojang servers)

    boolean offlineMode = !this.isSessionsOnline();

    if (offlineMode) {
      String ip = event.getConnection().getAddress().getAddress().getHostAddress();
      Optional<User> user = this.database.getUsers().findByName(event.getConnection().getName());

      if (user.isPresent()) {
        UUID uuid = user.get().getUniqueId();
        List<Session> sessions = this.database.getSessions().select()
            .where("user_id", user.get().getUniqueId()).limit(15).order("created_at", "DESC")
            .execute();

        List<String> safeIps = new ArrayList<String>();

        for (Session session : sessions) {
          String sessionIp = session.getIp();

          if (!safeIps.contains(sessionIp)) {
            safeIps.add(sessionIp);
          }
        }

        for (String safeIp : safeIps) {
          if (safeIp.equals(ip)) {
            event.getConnection().setOnlineMode(false);
            event.getConnection().setUniqueId(uuid);
            break;
          }
        }
      }
    }
  }

  @Override
  public void run() {
    this.statusHistory.add(checkStatus());

    if (this.statusHistory.size() > 12) {
      this.statusHistory.remove(0);
    }

    int online = 0;
    for (boolean status : this.statusHistory) {
      if (status) {
        online++;
      }
    }
    double percent = (double) online / (double) this.statusHistory.size();
    this.setSessionsOnline(percent >= 0.90);
  }

  private boolean checkStatus() {
    try {
      HttpsURLConnection connection = (HttpsURLConnection) new URL(
          "https://sessionserver.mojang.com").openConnection();
      connection.setConnectTimeout(10000);
      connection.setReadTimeout(10000);
      connection.setRequestMethod("HEAD");
      int responseCode = connection.getResponseCode();
      connection.disconnect();

      return responseCode >= 200 && responseCode <= 399;
    } catch (IOException exception) {
      return false;
    }
  }
}
