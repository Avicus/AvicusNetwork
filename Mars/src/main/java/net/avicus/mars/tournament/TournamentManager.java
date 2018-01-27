package net.avicus.mars.tournament;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.countdown.StartingCountdown;
import net.avicus.atlas.event.group.PlayerChangedGroupEvent;
import net.avicus.atlas.event.match.MatchCloseEvent;
import net.avicus.atlas.event.match.MatchOpenEvent;
import net.avicus.atlas.event.player.PlayerJoinDelayedEvent;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.states.StatesModule;
import net.avicus.magma.database.model.impl.Tournament;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.network.user.Users;
import net.avicus.mars.EventManager;
import net.avicus.mars.MarsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TournamentManager implements EventManager<TournamentMatch>, Listener {

  @Getter
  private final Tournament tournament;
  // Sorted by order of play, last element is always current.
  @Getter
  private final List<TournamentMatch> matches = new ArrayList<>();

  public TournamentManager(Tournament tournament) {
    this.tournament = tournament;
  }

  @Override
  public Optional<TournamentMatch> getCurrentEvent() {
    if (this.matches.isEmpty()) {
      return Optional.empty();
    }

    TournamentMatch last = this.matches.get(this.matches.size() - 1);
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
      this.matches.add(new TournamentMatch(this.tournament, event.getMatch()));
    }
  }

  @EventHandler
  public void onJoin(PlayerJoinDelayedEvent e) {
    if (getCurrentEvent().isPresent()) {
      Optional<User> user = Users.user(e.getPlayer().getUniqueId());

      if (user.isPresent()) {
        Optional<Group> target = getCurrentEvent().get().getIntendedPlayerGroup(user.get().getId());
        target.ifPresent(t -> {
          if (t.isFull(false)) {
            e.getPlayer().kickPlayer(
                "You are not permitted to play because the max number of playing players has been reached.");
          } else {
            getCurrentEvent().get().getGroupsModule().changeGroup(e.getPlayer(), t, true, true);
          }
        });
      }
    }
  }

  @EventHandler
  public void assignPlayer(PlayerChangedGroupEvent event) {
    if (getCurrentEvent().isPresent()) {
      if (event.getGroupFrom().isPresent() && event.getGroup().isSpectator()) {
        return;
      }

      Optional<User> user = Users.user(event.getPlayer().getUniqueId());

      if (user.isPresent()) {
        Optional<Group> target = getCurrentEvent().get().getIntendedPlayerGroup(user.get().getId());
        if (target.isPresent()) {
          event.setGroup(target.get());
        }
      }
    }
  }

  @EventHandler
  public void onMatchOpen(MatchOpenEvent event) {
    if (event.getMatch().getRequiredModule(StatesModule.class).isStarting() && getCurrentEvent()
        .isPresent()) {
      StartingCountdown starting = new ConditionalStartCountdown(event.getMatch(),
          getCurrentEvent().get());
      Atlas.get().getMatchManager().getRotation().startMatch(starting);
    }
  }
}
