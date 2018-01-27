package net.avicus.magma;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import net.avicus.magma.database.Database;
import net.avicus.magma.database.model.impl.Server;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

public class ServerTask implements Runnable {

  private final Magma plugin;
  private final Database database;

  public ServerTask(Magma plugin, Database database) {
    this.plugin = plugin;
    this.database = database;
  }

  public void start() {
    this.plugin.getProxy().getScheduler().schedule(this.plugin, this, 3, 10, TimeUnit.SECONDS);
  }

  private ServerInfo createServerInfo(Server server) {
    InetSocketAddress address = new InetSocketAddress(server.getHost(), server.getPort());
    return this.plugin.getProxy().constructServerInfo(server.getName(), address, "", false);
  }

  public void run() {
    ProxyServer proxy = this.plugin.getProxy();

    Map<String, ServerInfo> bungeeServers = proxy.getServers();
    List<Server> dbServers = this.database.getServers().select().execute();

    List<String> removeServers = new ArrayList<>();

    for (Entry<String, ServerInfo> entry : bungeeServers.entrySet()) {
      boolean found = false;

      for (Server server : dbServers) {
        if (server.getName().equals(entry.getKey())) {
          found = true;
          break;
        }
      }

      if (!found) {
        removeServers.add(entry.getKey());
      }
    }

    Map<String, ServerInfo> addServers = new HashMap<>();
    for (Server server : dbServers) {
      addServers.put(server.getName(), createServerInfo(server));
    }

    bungeeServers.keySet().removeAll(removeServers);
    bungeeServers.putAll(addServers);
  }
}
