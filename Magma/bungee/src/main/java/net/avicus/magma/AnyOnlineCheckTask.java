package net.avicus.magma;

import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

public class AnyOnlineCheckTask implements Runnable {

  public boolean anyOnline = false;
  private final Magma plugin;

  public AnyOnlineCheckTask(Magma plugin) {
    this.plugin = plugin;
  }

  public void start() {
    this.plugin.getProxy().getScheduler().schedule(this.plugin, this, 3, 45, TimeUnit.SECONDS);
  }

  @Override
  public void run() {
    anyOnline = false;
    for (ServerInfo info : ProxyServer.getInstance().getServers().values()) {
      info.ping((serverPing, throwable) -> {
        if (throwable == null) {
          anyOnline = true;
        }
      });
    }
  }
}
