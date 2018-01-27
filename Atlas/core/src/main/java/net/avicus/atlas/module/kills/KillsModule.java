package net.avicus.atlas.module.kills;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.ToString;
import net.avicus.atlas.event.group.PlayerChangedGroupEvent;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.Module;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.LocationVariable;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.grave.event.PlayerDeathEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;
import tc.oc.tracker.Damage;
import tc.oc.tracker.DamageInfo;

@ToString(exclude = "match")
public class KillsModule implements Module {

  private final Match match;
  private final List<KillReward> rewards;
  private final Optional<Check> killCheck;
  private final Optional<Check> deathCheck;

  private final Map<Competitor, Integer> kills;
  private final Map<Competitor, Integer> deaths;

  public KillsModule(Match match, List<KillReward> rewards, Optional<Check> killCheck,
      Optional<Check> deathCheck) {
    this.match = match;
    this.rewards = rewards;
    this.killCheck = killCheck;
    this.deathCheck = deathCheck;
    this.kills = new HashMap<>();
    this.deaths = new HashMap<>();
  }

  private void reset(Competitor competitor) {
    this.kills.remove(competitor);
    this.deaths.remove(competitor);
  }

  public int getKills(Competitor competitor) {
    return this.kills.getOrDefault(competitor, 0);
  }

  public int getDeaths(Competitor team) {
    return this.deaths.getOrDefault(team, 0);
  }

  public List<KillReward> rewardsFor(Player killer, Player target) {
    return this.rewards.stream()
        .filter((reward) -> reward.passes(this.match, killer, target))
        .collect(Collectors.toList());
  }

  @EventHandler
  public void onPlayerDeathReward(PlayerDeathEvent event) {
    Player dead = event.getPlayer();

    Damage lastDamage = event.getLifetime().getLastDamage();
    if (lastDamage == null) {
      return;
    }

    DamageInfo info = lastDamage.getInfo();
    if (!(info.getResolvedDamager() instanceof Player)) {
      return;
    }

    Player killer = (Player) info.getResolvedDamager();

    // Don't reward suicide
    if (dead.equals(killer)) {
      return;
    }

    // Reward relevant kill rewards
    for (KillReward reward : rewardsFor(killer, dead)) {
      reward.give(killer);
    }
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    Player player = event.getPlayer();

    // Kills
    if (event.getLifetime().getLastDamage() != null) {
      LivingEntity entityCause = event.getLifetime().getLastDamage().getInfo().getResolvedDamager();

      if (entityCause instanceof Player) {
        Player cause = (Player) entityCause;

        if (this.killCheck.isPresent()) {
          CheckContext context = new CheckContext(this.match);
          context.add(new PlayerVariable(cause));
          context.add(new LocationVariable(cause.getLocation()));
          if (this.killCheck.get().test(context).fails()) {
            return;
          }
        }

        Optional<Competitor> competitor = this.match.getRequiredModule(GroupsModule.class)
            .getCompetitorOf(player);
        if (competitor.isPresent()) {
          this.kills.put(competitor.get(), getKills(competitor.get()) + 1);
        }
      }
    }

    // Deaths
    {
      if (this.deathCheck.isPresent()) {
        CheckContext context = new CheckContext(this.match);
        context.add(new PlayerVariable(player));
        context.add(new LocationVariable(player.getLocation()));
        if (this.deathCheck.get().test(context).fails()) {
          return;
        }
      }

      Optional<Competitor> competitor = this.match.getRequiredModule(GroupsModule.class)
          .getCompetitorOf(player);
      if (competitor.isPresent()) {
        this.deaths.put(competitor.get(), getDeaths(competitor.get()) + 1);
      }
    }

    DeathMessage.broadcast(this.match, event);
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    Optional<Competitor> competitor = this.match.getRequiredModule(GroupsModule.class)
        .getCompetitorOf(event.getPlayer());
    if (competitor.isPresent()) {
      reset(competitor.get());
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPlayerChangeTeam(PlayerChangedGroupEvent event) {
    if (!event.getGroupFrom().isPresent()) {
      return;
    }

    Optional<Competitor> competitor = this.match.getRequiredModule(GroupsModule.class)
        .getCompetitorOf(event.getPlayer());
    if (competitor.isPresent()) {
      reset(competitor.get());
    }
  }
}
