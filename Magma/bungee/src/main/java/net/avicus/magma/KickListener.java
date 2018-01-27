package net.avicus.magma;

import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class KickListener implements Listener {

  private final Magma plugin;
  private final SetMultimap<UUID, String> tried = Multimaps
      .newSetMultimap(new HashMap<>(), HashSet::new);

  public KickListener(Magma plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onUserKick(ServerKickEvent event) {
    if (event.getKickReason().contains(NetworkIdentification.URL) || event.getKickReason()
        .contains("[Proxy]")) {
      return;
    }

    final ProxiedPlayer player = event.getPlayer();
    final Set<String> skip = this.tried.get(player.getUniqueId());
    Magma.get()
        .getLobby(player, Sets.union(Collections.singleton(event.getKickedFrom().getName()), skip),
            skip)
        .ifPresent(server -> {
          event.setCancelled(true);
          event.setCancelServer(server);
          skip.add(server.getName());
        });

    final UUID uniqueId = player.getUniqueId();
    ProxyServer.getInstance().getScheduler()
        .schedule(this.plugin, () -> this.tried.removeAll(uniqueId), 15, TimeUnit.SECONDS);
    event.getPlayer().sendMessage(ChatMessageType.CHAT, event.getKickReasonComponent());
  }
}
