package net.avicus.atlas.module.shop;

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Setter;
import net.avicus.atlas.event.group.PlayerChangedGroupEvent;
import net.avicus.atlas.match.Match;
import net.avicus.compendium.number.NumberAction;
import net.avicus.grave.event.PlayerDeathByPlayerEvent;
import net.avicus.grave.event.PlayerDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PointListener implements Listener {

  private final Match match;
  private final HashMap<UUID, AtomicInteger> points;
  @Setter
  private Shop shop;

  public PointListener(Match match) {
    this.match = match;
    this.points = Maps.newHashMap();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onDeath(PlayerDeathEvent event) {
    modifyPoints(event.getPlayer().getUniqueId(), 0, NumberAction.SET);
  }

  @EventHandler
  public void onPointEarn(PlayerEarnPointEvent event) {
    modifyPoints(event.getPlayer().getUniqueId(),
        this.shop.getConfig().getPoints(event.getAction()), NumberAction.ADD);
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onKill(PlayerDeathByPlayerEvent event) {
    modifyPoints(event.getCause().getUniqueId(),
        this.shop.getConfig().getPoints("player-kill"), NumberAction.ADD);
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onJoin(PlayerChangedGroupEvent event) {
    if (!event.getGroup().isSpectator()) {
      modifyPoints(event.getPlayer().getUniqueId(),
          this.shop.getConfig().getPoints("join-match"), NumberAction.SET);
    }
  }

  public int getPoints(Player player) {
    return getPoints(player.getUniqueId());
  }

  public int getPoints(UUID uuid) {
    AtomicInteger get = points.get(uuid);
    if (get != null) {
      return get.get();
    }
    return 0;
  }

  public void modifyPoints(UUID uuid, int change, NumberAction action) {
    AtomicInteger get = points.get(uuid);
    if (get == null) {
      points.put(uuid, new AtomicInteger());
      get = points.get(uuid);
    }

    get.set(action.perform(get.get(), change));
  }
}
