package net.avicus.magma.util.distance;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import org.bukkit.entity.Player;

public class SimplePlayerStore implements PlayerStore {

  private static final List<SimplePlayerStore> registeredStores = new ArrayList<>();
  private final String id;
  @Getter
  private final List<Player> players;

  public SimplePlayerStore(String id) {
    this.id = id;
    this.players = new ArrayList<>();
  }

  public static void register(SimplePlayerStore store) {
    registeredStores.add(store);
  }

  public static void unRegister(String id) {
    registeredStores.removeAll(
        registeredStores.stream().filter(s -> s.id.equals(id)).collect(Collectors.toList()));
  }

  public static SimplePlayerStore getFromPlayer(Player player) {
    return registeredStores.stream().filter(s -> s.players.contains(player)).findFirst()
        .orElse(new SimplePlayerStore(player.getName().toLowerCase()).addPlayer(player));
  }

  public SimplePlayerStore addPlayer(Player player) {
    this.players.add(player);
    return this;
  }

  public void removePlayer(Player player) {
    this.players.remove(player);
  }

  public boolean hasPlayer(Player player) {
    return this.players.contains(player);
  }
}
