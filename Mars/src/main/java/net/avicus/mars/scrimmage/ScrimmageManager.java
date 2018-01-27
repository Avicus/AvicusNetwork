package net.avicus.mars.scrimmage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.avicus.magma.Magma;
import net.avicus.magma.database.model.impl.ReservedSlot;
import net.avicus.mars.EventManager;
import net.avicus.mars.MarsPlugin;
import net.avicus.mars.MarsTeam;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.joda.time.Hours;
import org.joda.time.Instant;

public class ScrimmageManager implements EventManager<Scrimmage>, Runnable, Listener {

  private final MarsPlugin plugin;
  private final Map<ReservedSlot, Scrimmage> scrimmages;
  private final String server;

  public ScrimmageManager(MarsPlugin plugin) {
    this.plugin = plugin;
    this.scrimmages = new HashMap<>();
    this.server = Magma.get().localServer().getName();
  }

  @EventHandler(ignoreCancelled = true)
  public void onPlayerJoin(PlayerJoinEvent event) {
    Scrimmage scrim = getCurrentEvent().orElse(null);
    if (scrim != null) {
      for (String permission : scrim.permissions(event.getPlayer())) {
        event.getPlayer().addAttachment(this.plugin, permission, !permission.startsWith("-"));
      }
    }
  }

  @Override
  public Optional<Scrimmage> getCurrentEvent() {
    for (ReservedSlot slot : this.scrimmages.keySet()) {
      if (slot.isOngoing()) {
        return Optional.ofNullable(this.scrimmages.get(slot));
      }
    }
    return Optional.empty();
  }

  @Override
  public void start() {
    Instant from = Instant.now().minus(Hours.EIGHT.toStandardDuration());
    Instant to = Instant.now().plus(Hours.EIGHT.toStandardDuration());

    List<ReservedSlot> relevantSlots = Magma.get().database().getReservedSlots()
        .findByStart(from.toDate(), to.toDate());
    for (ReservedSlot slot : relevantSlots) {
      if (slot.getServer().equals(server)) {
        this.scrimmages.put(slot, null);
      }
    }

    Bukkit.getLogger().info(
        "Found " + relevantSlots.size() + " slot(s) that are reserved for this server from " + from
            + " to " + to);

    // Updater
    Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, this, 0, 20 * 10);

    Bukkit.getServer().getPluginManager().registerEvents(this, this.plugin);
  }

  @Override
  public void run() {
    List<ReservedSlot> toRemove = new ArrayList<>();

    for (ReservedSlot slot : this.scrimmages.keySet()) {
      if (slot.getEnd().before(new Date())) {
        toRemove.add(slot);
        Bukkit.getLogger().info("Removing " + slot + " since it has ended.");
        continue;
      }

      if (server.equals(slot.getServer())) {
        Optional<MarsTeam> team = this.plugin.createTeam(slot.getTeamId());

        if (!team.isPresent()) {
          continue;
        }

        Scrimmage existing = this.scrimmages.get(slot);
        boolean isCurrentEvent = Objects.equals(getCurrentEvent().orElse(null), existing);

        if (existing == null || !isCurrentEvent) {
          this.scrimmages.put(slot, new Scrimmage(slot, team.get()));
          Bukkit.getLogger().info("Setting up scrimmage for " + slot);
        }
      }
    }

    toRemove.forEach(this.scrimmages::remove);

    if (this.scrimmages.isEmpty() && Bukkit.getOnlinePlayers().isEmpty()) {
      Bukkit.getLogger().info("No scrimmages left.");
      Bukkit.shutdown();
    }
  }
}
