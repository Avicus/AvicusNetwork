package net.avicus.atlas.module.results;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.countdown.MatchEndCountdown;
import net.avicus.atlas.event.competitor.CompetitorWinEvent;
import net.avicus.atlas.event.group.PlayerChangedGroupEvent;
import net.avicus.atlas.event.match.MatchCompleteEvent;
import net.avicus.atlas.event.match.MatchStateChangeEvent;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.BridgeableModule;
import net.avicus.atlas.module.Module;
import net.avicus.atlas.module.ModuleBridge;
import net.avicus.atlas.module.checks.CheckResult;
import net.avicus.atlas.module.checks.StaticResultCheck;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.objectives.GlobalObjective;
import net.avicus.atlas.module.objectives.Objective;
import net.avicus.atlas.module.objectives.ObjectivesModule;
import net.avicus.atlas.module.objectives.score.event.PointEarnEvent;
import net.avicus.atlas.module.results.scenario.EndScenario;
import net.avicus.atlas.module.results.scenario.TieScenario;
import net.avicus.atlas.module.states.StatesModule;
import net.avicus.atlas.util.AtlasTask;
import net.avicus.atlas.util.Events;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizableFormat;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.compendium.locale.text.UnlocalizedFormat;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.compendium.plugin.CompendiumPlugin;
import net.avicus.compendium.sound.SoundEvent;
import net.avicus.compendium.sound.SoundLocation;
import net.avicus.compendium.sound.SoundType;
import net.avicus.compendium.utils.Strings;
import net.avicus.grave.event.PlayerDeathEvent;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.github.paperspigot.Title;

@ToString(exclude = "match")
public class ResultsModule extends BridgeableModule<ModuleBridge<ResultsModule>> implements Module {

  private final Match match;
  @Getter
  private List<EndScenario> scenarios;

  @Getter
  private List<EndScenario> timeBased;

  public ResultsModule(Match match, List<EndScenario> scenarios) {
    this.match = match;
    this.scenarios = scenarios;
    this.timeBased = scenarios.stream().filter(e -> e.getCountdown().isPresent())
        .collect(Collectors.toList());
    buildBridges(this);
  }

  public void updateTimeBased() {
    this.timeBased.clear();
    this.timeBased = scenarios.stream().filter(e -> e.getCountdown().isPresent())
        .collect(Collectors.toList());
  }

  @Override
  public void open() {
    getBridges().values().forEach(b -> b.onOpen(this));
  }

  public void check() {
    if (!this.match.getRequiredModule(StatesModule.class).isPlaying()) {
      return;
    }

    if (!this.match.hasModule(ObjectivesModule.class)) {
      return;
    }

    List<Competitor> winners = new ArrayList<>();

    GroupsModule groups = this.match.getRequiredModule(GroupsModule.class);

    int completed = 0;
    Set<Objective> needed = Sets.newHashSet();

    List<Objective> objectives = match.getRequiredModule(ObjectivesModule.class).getObjectives();

    List<CompetitorCompletionState> states = new ArrayList<>();

    for (Objective objective : objectives) {
      if (objective instanceof GlobalObjective && ((GlobalObjective) objective).isCompleted()) {
        completed++;
      }
    }

    for (Competitor competitor : groups.getCompetitors()) {
      CompetitorCompletionState completionState = new CompetitorCompletionState(match, competitor);
      states.add(completionState);
      if (completionState.shouldWin()) {
        winners.add(competitor);
      }

      for (Objective objective : objectives) {
        if (!(objective instanceof GlobalObjective) && objective.canComplete(competitor)
            && objective.isCompleted(competitor)) {
          completed++;
        }
      }

      needed.addAll(completionState.getNeededObjectives());
    }

    if (winners.isEmpty()) {
      if (!this.scenarios.isEmpty()) {
        for (EndScenario scenario : this.scenarios) {
          if (scenario.test(this.match)) {
            scenario.execute(this.match, groups);
            return;
          }
        }
        return;
      } else if (completed >= needed.size()) {
        Map.Entry<Integer, List<CompetitorCompletionState>> highest = CompetitorCompletionState
            .getRankedCompletions(states).firstEntry();
        if (highest != null && highest.getKey() > 0) {
          for (CompetitorCompletionState competitor : highest.getValue()) {
            winners.add(competitor.getCompetitor());
          }
        } else {
          new TieScenario(match, new StaticResultCheck(CheckResult.ALLOW), 1)
              .execute(match, groups);
          return;
        }
      } else {
        return;
      }
    }

    this.match.getRequiredModule(StatesModule.class).next();

    broadcastWinners(groups.getCompetitors(), winners);
  }

  public void broadcastWinners(Collection<? extends Competitor> competitors,
      List<Competitor> winners) {
    List<Player> winSoundReceivers = new ArrayList<>();

    for (Competitor competitor : winners) {
      CompetitorWinEvent event = new CompetitorWinEvent(match, competitor);
      Events.call(event);
      if (winners.size() == 1) {
        broadcastWin(competitor);
      }
      winSoundReceivers.addAll(competitor.getPlayers());
    }

    winSoundReceivers.forEach(player -> {
      SoundLocation location = SoundLocation.MATCH_WIN;
      SoundType soundType = SoundType.FIREWORK;
      SoundEvent call = Events.call(new SoundEvent(player, soundType, location));
      call.getSound().play(player, 1F);
    });

    Bukkit.getOnlinePlayers().forEach(player -> {
      if (winSoundReceivers.contains(player)) {
        return;
      }
      SoundLocation location = SoundLocation.MATCH_LOSE;
      SoundType soundType = SoundType.GOLEM_DEATH;
      SoundEvent call = Events.call(new SoundEvent(player, soundType, location));
      call.getSound().play(player, 1F);
    });

    if (winners.size() > 1) {
      int place = 1;
      List<Localizable> res = new ArrayList<>();
      for (Competitor competitor : winners) {
        LocalizableFormat placeFormat = new UnlocalizedFormat("{0}. ");

        res.add(new UnlocalizedText("{0} {1}", placeFormat.with(new LocalizedNumber(place)),
            competitor.getColoredName()));
        place++;
      }
      final Localizable translation = Messages.UI_WINNERS.with();
      match.getPlayers().forEach(player -> player.sendMessage(Strings
          .padChatComponent(translation.translate(player.getLocale()), "-", ChatColor.GOLD,
              ChatColor.YELLOW)));
      res.forEach(match::broadcast);
    }

    MatchCompleteEvent event = new MatchCompleteEvent(match, competitors, winners);
    Events.call(event);
  }

  public Optional<MatchEndCountdown> getEndingCountdown() {
    if (!this.scenarios.isEmpty()) {
      for (EndScenario scenario : this.scenarios) {
        if (scenario.getCountdown().isPresent()) {
          return scenario.getCountdown();
        }
      }
    }

    return Optional.empty();
  }

  @EventHandler
  public void onMatchStart(MatchStateChangeEvent event) {
    if (getEndingCountdown().isPresent()) {
      if (event.isChangeToPlaying()) {
        CompendiumPlugin.getInstance().getCountdownManager().start(this.getEndingCountdown().get());
      } else {
        CompendiumPlugin.getInstance().getCountdownManager()
            .cancel(this.getEndingCountdown().get());
      }
    }
  }

  @Override
  public void close() {
    if (getEndingCountdown().isPresent()) {
      CompendiumPlugin.getInstance().getCountdownManager().cancel(this.getEndingCountdown().get());
    }
    getBridges().values().forEach(b -> b.onClose(this));
  }

  public void broadcastWin(Competitor competitor) {
    Localizable wins = Messages.UI_WINS.with(competitor.getColoredName());

    for (Player player : Bukkit.getOnlinePlayers()) {

      BaseComponent subtitle = Messages.UI_SPEC_JOIN_NEXT.with(ChatColor.GOLD)
          .translate(player.getLocale());

      if (match.getRequiredModule(GroupsModule.class).getCompetitorOf(player).isPresent()) {
        subtitle = Messages.UI_TEAM_LOST.with(ChatColor.RED).translate(player.getLocale());
      }

      if (competitor.hasPlayer(player)) {
        subtitle = Messages.UI_TEAM_WON.with(ChatColor.GREEN).translate(player.getLocale());
      }

      Title title = Title.builder().title(wins.translate(player.getLocale()))
          .subtitle(subtitle)
          .fadeIn(10)
          .stay(60)
          .fadeOut(20)
          .build();
      player.sendTitle(title);
    }
    this.match.importantBroadcast(wins);
  }

  public void syncCheck() {
    new AtlasTask() {
      @Override
      public void run() {
        check();
      }
    }.now();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onDeath(final PlayerDeathEvent event) {
    this.syncCheck();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onChangeTeam(PlayerChangedGroupEvent event) {
    syncCheck();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void pointEarn(final PointEarnEvent event) {
    syncCheck();
  }
}
