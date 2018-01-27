package net.avicus.atlas.module.stats.action;

import java.time.Instant;
import java.util.Iterator;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.event.group.PlayerChangedGroupEvent;
import net.avicus.atlas.event.match.MatchStateChangeEvent;
import net.avicus.atlas.event.player.PlayerSpawnBeginEvent;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.elimination.event.PlayerEliminateEvent;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.objectives.score.event.PointEarnEvent;
import net.avicus.atlas.module.states.State;
import net.avicus.atlas.module.states.StatesModule;
import net.avicus.atlas.module.stats.action.base.CompetitorAction;
import net.avicus.atlas.module.stats.action.base.PlayerAction;
import net.avicus.atlas.module.stats.action.damage.PlayerAssistKillAction;
import net.avicus.atlas.module.stats.action.damage.PlayerDeathByNaturalAction;
import net.avicus.atlas.module.stats.action.damage.PlayerDeathByPlayerAction;
import net.avicus.atlas.module.stats.action.damage.PlayerDeathBySelfAction;
import net.avicus.atlas.module.stats.action.damage.PlayerKillAction;
import net.avicus.atlas.module.stats.action.lifetime.LifetimeStore;
import net.avicus.atlas.module.stats.action.lifetime.type.CompetitorLifetime;
import net.avicus.atlas.module.stats.action.lifetime.type.ObjectiveLifetime;
import net.avicus.atlas.module.stats.action.lifetime.type.PlayerLifetime;
import net.avicus.atlas.module.stats.action.match.PlayerChangeGroupAction;
import net.avicus.atlas.module.stats.action.match.PlayerEliminateAction;
import net.avicus.atlas.module.stats.action.match.PlayerJoinMatchAction;
import net.avicus.atlas.module.stats.action.match.PlayerLeaveMatchAction;
import net.avicus.atlas.module.stats.action.match.PlayerMatchAction;
import net.avicus.atlas.module.stats.action.objective.ObjectiveAction;
import net.avicus.atlas.module.stats.action.objective.score.PlayerEarnPointAction;
import net.avicus.grave.event.PlayerDeathByEntityEvent;
import net.avicus.grave.event.PlayerDeathByPlayerEvent;
import net.avicus.grave.event.PlayerDeathEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import tc.oc.tracker.Damage;
import tc.oc.tracker.DamageInfo;
import tc.oc.tracker.damage.OwnedMobDamageInfo;

@ToString(exclude = "match")
public class ActionStore implements Listener {

  private final Match match;
  @Getter
  private final LifetimeStore lifetimeStore;
  private State currentState;

  public ActionStore(Match match) {
    this.match = match;
    this.currentState = match.getRequiredModule(StatesModule.class).getState();
    this.lifetimeStore = new LifetimeStore(match);
  }

  // ---------------
  // -- Lifetimes --
  // ---------------

  public void store(CompetitorAction action) {
    if (action instanceof ObjectiveAction) {
      ObjectiveLifetime lifetime = this.lifetimeStore
          .getCurrentLifetime(((ObjectiveAction) action).getActed());
      lifetime.addAction((ObjectiveAction) action);
    }

    CompetitorLifetime lifetime = this.lifetimeStore.getCurrentLifetime(action.getActor());
    lifetime.addAction(action);
  }

  public void store(PlayerAction action) {
    if (action.getActor() == null) {
      return;
    }

    if (action instanceof ObjectiveAction) {
      ObjectiveLifetime lifetime = this.lifetimeStore
          .getCurrentLifetime(((ObjectiveAction) action).getActed());
      lifetime.addAction((ObjectiveAction) action);
    }

    if (action instanceof PlayerMatchAction) {
      this.lifetimeStore.getMatchLifetime().addAction((PlayerMatchAction) action);
    }

    PlayerLifetime lifetime = this.lifetimeStore.getCurrentLifetime(action.getActor(), true);
    lifetime.addAction(action);
  }

  @EventHandler(priority = EventPriority.LOW)
  public void startLifetime(PlayerSpawnBeginEvent event) {
    this.lifetimeStore.restartLifetime(event.getPlayer());
  }

  // ------------
  // -- Damage --
  // ------------

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerDeathNatural(PlayerDeathEvent event) {
    if (event instanceof PlayerDeathByEntityEvent) {
      return;
    }

    @Nullable DamageInfo info = null;
    if (event.getLifetime().getLastDamage() != null) {
      info = event.getLifetime().getLastDamage().getInfo();
    }
    store(new PlayerDeathByNaturalAction(event.getPlayer(), Instant.now(), info));
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerDeathPlayer(PlayerDeathByPlayerEvent event) {
    @Nullable DamageInfo info = null;
    if (event.getLifetime().getLastDamage() != null) {
      info = event.getLifetime().getLastDamage().getInfo();
    }
    Instant when = Instant.now();

    if (event.getCause().equals(event.getPlayer())) {
      store(new PlayerDeathBySelfAction(event.getCause(), when, info));
      return;
    }

    store(new PlayerDeathByPlayerAction(event.getPlayer(), when, info, event.getCause()));
    store(new PlayerKillAction(event.getCause(), when, info, event.getPlayer()));
    Iterator<Damage> life = event.getLifetime().getDamageLast();

    long epoch = Instant.now().getEpochSecond();

    while (life.hasNext()) {
      Damage found = life.next();
      if (epoch - found.getTime().getMillis() > 15000) {
        break;
      }

      LivingEntity damager = found.getInfo().getResolvedDamager();
      if (damager != null) {
        if (damager.getType() == EntityType.PLAYER) {
          store(
              new PlayerAssistKillAction((Player) damager, when, found.getInfo(), event.getCause(),
                  event.getPlayer()));
        } else if (found.getInfo() instanceof OwnedMobDamageInfo) {
          OwnedMobDamageInfo owned = (OwnedMobDamageInfo) found.getInfo();
          if (owned.getMobOwner() != null) {
            store(new PlayerAssistKillAction(owned.getMobOwner(), when, found.getInfo(),
                event.getCause(), event.getPlayer()));
          }
        }
      }
    }
  }

  // -----------
  // -- Match --
  // -----------

  @EventHandler(priority = EventPriority.MONITOR)
  public void onStateChange(MatchStateChangeEvent event) {
    if (event.getTo().isPresent()) {
      this.currentState = event.getTo().get();
      Instant when = Instant.now();

      if (event.isChangeToPlaying()) {
        match.getRequiredModule(GroupsModule.class).getCompetitors().stream()
            .flatMap(c -> c.getPlayers().stream()).forEach(player -> {
          store(new PlayerJoinMatchAction(player, when, this.match, this.currentState));
        });
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerEliminate(PlayerEliminateEvent event) {
    store(
        new PlayerEliminateAction(event.getPlayer(), Instant.now(), this.match, this.currentState));
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerJoinGroup(PlayerChangedGroupEvent event) {
    Instant when = Instant.now();
    store(new PlayerChangeGroupAction(event.getPlayer(), when, this.match, this.currentState,
        event.getGroupFrom().orElse(null), event.getGroup()));
    event.getGroupFrom().ifPresent(group -> {
      if (group.isSpectator() && !event.getGroup().isSpectator() && this.currentState.isPlaying()) {
        store(new PlayerJoinMatchAction(event.getPlayer(), when, this.match, this.currentState));
      } else if (!group.isSpectator() && event.getGroup().isSpectator() && this.currentState
          .isPlaying()) {
        store(new PlayerLeaveMatchAction(event.getPlayer(), when, this.match, this.currentState));
      }
    });
  }

  // ---------------
  // ---- Score ----
  // ---------------

  @EventHandler(priority = EventPriority.MONITOR)
  public void onScore(PointEarnEvent event) {
    if (event.getPlayer() != null) {
      store(new PlayerEarnPointAction(event.getObjective(), event.getPlayer(), Instant.now()));
    }
  }
}
