package net.avicus.hook.credits.reward;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.event.competitor.CompetitorWinEvent;
import net.avicus.atlas.event.competitor.PlayerChangeCompetitorEvent;
import net.avicus.atlas.event.group.PlayerChangedGroupEvent;
import net.avicus.atlas.event.match.MatchCompleteEvent;
import net.avicus.grave.event.PlayerDeathEvent;
import net.avicus.hook.HookConfig;
import net.avicus.hook.credits.Credits;
import net.avicus.hook.utils.Events;
import net.avicus.hook.utils.Messages;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.joda.time.Duration;
import org.joda.time.Instant;

public class CreditRewardListener implements Listener {

  private final Map<UUID, Instant> teamJoinTimes;

  public CreditRewardListener() {
    this.teamJoinTimes = new HashMap<>();
    if (Atlas.get().getLoader().hasModule("competitive-objectives")) {
      Events.register(new CompetitveRewardListener());
    }
  }

  private Optional<Duration> timeInMatch(Player player) {
    Instant from = this.teamJoinTimes.get(player.getUniqueId());
    if (from == null) {
      return Optional.empty();
    }

    Instant to = Instant.now();
    return Optional.of(new Duration(from, to));
  }

  private void resetTimeInMatch(Player player) {
    this.teamJoinTimes.remove(player.getUniqueId());
  }

  @EventHandler
  public void onGroupChange(PlayerChangedGroupEvent event) {
    this.teamJoinTimes.put(event.getPlayer().getUniqueId(), Instant.now());

    if (event.getGroup().isSpectator()) {
      resetTimeInMatch(event.getPlayer());
    }
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    if (event.getLifetime().getLastDamage() == null) {
      return;
    }

    LivingEntity damager = event.getLifetime().getLastDamage().getInfo().getResolvedDamager();

    if (damager instanceof Player) {
      if (damager.equals(event.getPlayer())) {
        return;
      }

      int reward = HookConfig.Credits.Rewards.getKillPlayer();
      if (reward > 0) {
        Credits.reward((Player) damager, reward, Messages.UI_REWARD_KILL_PLAYER);
      }
    }
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    resetTimeInMatch(event.getPlayer());
  }

  @EventHandler
  public void onPlayerChangeGroup(PlayerChangeCompetitorEvent event) {
    resetTimeInMatch(event.getPlayer());
  }

  @EventHandler
  public void onComplete(MatchCompleteEvent event) {
    List<Player> winners = event.getWinners().stream()
        .flatMap(competitor -> competitor.getPlayers().stream()).collect(Collectors.toList());
    List<Player> participants = event.getCompetitors().stream()
        .flatMap(competitor -> competitor.getPlayers().stream())
        .filter(player -> !winners.contains(player)).collect(Collectors.toList());

    for (Player participant : participants) {
      Duration timeInMatch = timeInMatch(participant).orElse(null);
      if (timeInMatch == null) {
        continue;
      }
      resetTimeInMatch(participant);

      int rewardBase = HookConfig.Credits.Rewards.getLosePerMinute();
      int rewardMin = HookConfig.Credits.Rewards.getLoseMinimum();

      int minutes = (int) timeInMatch.getStandardMinutes();
      int reward = Math.max(rewardBase * minutes, rewardMin);
      if (reward > 0) {
        Credits.reward(participant, reward, Messages.UI_REWARD_PARTICIPATION);
      }
    }
  }

  @EventHandler
  public void onCompetitorWin(CompetitorWinEvent event) {
    List<Player> winners = event.getWinner().getPlayers();

    for (Player winner : winners) {
      Duration timeInMatch = timeInMatch(winner).orElse(null);
      if (timeInMatch == null) {
        continue;
      }
      resetTimeInMatch(winner);

      int rewardBase = HookConfig.Credits.Rewards.getWinPerMinute();
      int rewardMin = HookConfig.Credits.Rewards.getWinMinimum();

      int minutes = (int) timeInMatch.getStandardMinutes();
      int reward = Math.max(rewardBase * minutes, rewardMin);
      if (reward > 0) {
        Credits.reward(winner, reward, Messages.UI_REWARD_WIN);
      }
    }
  }
}
