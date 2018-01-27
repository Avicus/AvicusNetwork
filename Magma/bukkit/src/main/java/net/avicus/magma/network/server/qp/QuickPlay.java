package net.avicus.magma.network.server.qp;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.avicus.magma.Magma;
import net.avicus.magma.MagmaConfig;
import org.bukkit.entity.Player;

public class QuickPlay {

  private static List<PlayerRequestHandler.PlayerRequestMessage> messages;

  public static void init() {
    if (MagmaConfig.Server.QuickPlay.isEnabled()) {
      messages = new ArrayList<>();

      Magma.get().getRedis().register(new PlayerRequestHandler());
    }
  }

  public static void onRequest(PlayerRequestHandler.PlayerRequestMessage message) {
    messages.add(message);

    messages.sort(new Comparator<PlayerRequestHandler.PlayerRequestMessage>() {
      @Override
      public int compare(PlayerRequestHandler.PlayerRequestMessage o1,
          PlayerRequestHandler.PlayerRequestMessage o2) {
        return Integer.compare(o2.priority(), o1.priority());
      }
    });
  }

  private static void cleanMessages() {
    Date now = new Date();
    Iterator<PlayerRequestHandler.PlayerRequestMessage> iterator = messages.iterator();
    while (iterator.hasNext()) {
      PlayerRequestHandler.PlayerRequestMessage message = iterator.next();
      if (now.after(message.getExpiration())) {
        iterator.remove();
      }
    }
  }

  public static List<PlayerRequestHandler.PlayerRequestMessage> requests(Player player) {
    if (messages == null) {
      return ImmutableList.of();
    }
    cleanMessages();

    return messages.stream().filter((message) -> {
      if (message.getServer() == null) {
        return false;
      }

      if (message.getServer().isPermissible() && !player
          .hasMetadata(message.getServer().getPermission().get())) {
        return false;
      }

      return true;
    }).collect(Collectors.toList());
  }

  public static Optional<PlayerRequestHandler.PlayerRequestMessage> highestRequest(Player player) {
    return requests(player).stream().findFirst();
  }
}
