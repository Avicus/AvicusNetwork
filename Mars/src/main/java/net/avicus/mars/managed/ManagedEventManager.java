package net.avicus.mars.managed;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import net.avicus.atlas.event.match.MatchCloseEvent;
import net.avicus.atlas.event.match.MatchOpenEvent;
import net.avicus.mars.EventManager;
import net.avicus.mars.MarsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ManagedEventManager implements EventManager<ManagedMatch>, Listener {

  // Sorted by order of play, last element is always current.
  @Getter
  private final List<ManagedMatch> matches = new ArrayList<>();

  public ManagedEventManager() {
  }

  @Override
  public Optional<ManagedMatch> getCurrentEvent() {
    if (this.matches.isEmpty()) {
      return Optional.empty();
    }

    ManagedMatch last = this.matches.get(this.matches.size() - 1);
    return last.isOngoing() ? Optional.of(last) : Optional.empty();
  }

  @Override
  public void start() {
    Bukkit.getServer().getPluginManager().registerEvents(this, MarsPlugin.getInstance());
  }

  @EventHandler
  public void endCurrent(MatchCloseEvent event) {
    if (getCurrentEvent().isPresent()) {
      getCurrentEvent().get().setOngoing(false);
    }
  }

  @EventHandler
  public void startCurrent(MatchOpenEvent event) {
    if (getCurrentEvent().isPresent()) {
      getCurrentEvent().get().setOngoing(true);
    } else {
      this.matches.add(new ManagedMatch());
    }
  }
}
