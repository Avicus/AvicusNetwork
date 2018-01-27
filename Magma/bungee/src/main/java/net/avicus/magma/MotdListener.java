package net.avicus.magma;

import com.sk89q.minecraft.util.commands.ChatColor;
import java.text.MessageFormat;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import net.avicus.magma.database.Database;
import net.avicus.magma.database.model.impl.Announcement;
import net.avicus.magma.database.model.impl.Announcement.Type;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.ServerPing.PlayerInfo;
import net.md_5.bungee.api.ServerPing.Players;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class MotdListener implements Listener, Runnable {

  private static final Random random = new Random();

  private final Magma plugin;
  private final Database database;
  private Players players;
  private String fullMotd;

  public MotdListener(Magma plugin, Database database) {
    this.plugin = plugin;
    this.database = database;
    this.players = new Players(1, 0, new PlayerInfo[]{});
    this.fullMotd = "";
  }

  public MotdListener start() {
    this.plugin.getProxy().getScheduler().schedule(this.plugin, this, 0, 25, TimeUnit.SECONDS);
    return this;
  }

  @Override
  public void run() {
    List<Announcement> motds = this.database.getAnnouncements().findByType(Type.MOTD);
    List<Announcement> formats = this.database.getAnnouncements().findByType(Type.MOTD_FORMAT);

    int count = this.database.getSessions().activeSessions().size();

    int max = Math.max(100, count + 1);
    this.players = new Players(max, count, new PlayerInfo[]{});

    String format = formats.isEmpty() ? "" : formats.get(random.nextInt(formats.size())).getBody();
    String motd = motds.isEmpty() ? "" : motds.get(random.nextInt(motds.size())).getBody();

    this.fullMotd = ChatColor.translateAlternateColorCodes('&', MessageFormat.format(format, motd));
  }

  @EventHandler
  public void onProxyPing(ProxyPingEvent event) {
    ServerPing ping = event.getResponse();
    ping.setDescription(this.fullMotd);
    ping.setPlayers(this.players);
    event.setResponse(ping);
  }
}
