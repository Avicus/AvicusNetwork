package net.avicus.atlas.sets.competitve.objectives.bridges;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import net.avicus.atlas.event.match.MatchStateChangeEvent;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.bridge.ObjectivesModuleBridge;
import net.avicus.atlas.module.checks.CheckResult;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.groups.teams.Team;
import net.avicus.atlas.module.objectives.Objective;
import net.avicus.atlas.module.objectives.ObjectivesModule;
import net.avicus.atlas.module.objectives.locatable.LocatableListener;
import net.avicus.atlas.sets.competitve.objectives.destroyable.DestroyableObjective;
import net.avicus.atlas.sets.competitve.objectives.destroyable.leakable.LeakableListener;
import net.avicus.atlas.sets.competitve.objectives.destroyable.leakable.LeakableObjective;
import net.avicus.atlas.sets.competitve.objectives.destroyable.monument.MonumentListener;
import net.avicus.atlas.sets.competitve.objectives.destroyable.monument.MonumentObjective;
import net.avicus.atlas.sets.competitve.objectives.flag.FlagListener;
import net.avicus.atlas.sets.competitve.objectives.flag.FlagObjective;
import net.avicus.atlas.sets.competitve.objectives.hill.HillListener;
import net.avicus.atlas.sets.competitve.objectives.hill.HillObjective;
import net.avicus.atlas.sets.competitve.objectives.listeners.LocatableUpdater;
import net.avicus.atlas.sets.competitve.objectives.phases.DestroyablePhase;
import net.avicus.atlas.sets.competitve.objectives.phases.PhaseApplyCountdown;
import net.avicus.atlas.sets.competitve.objectives.wool.WoolListener;
import net.avicus.atlas.sets.competitve.objectives.wool.WoolObjective;
import net.avicus.atlas.util.Events;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizedText;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.compendium.plugin.CompendiumPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@Getter
public class ObjectivesBridge extends ObjectivesModuleBridge implements Listener {

  // Caches - filled on initialization
  private final List<Listener> listeners;
  private final List<MonumentObjective> monuments;
  private final List<HillObjective> hills;
  private final List<WoolObjective> wools;
  private final List<FlagObjective> flags;
  private final List<LeakableObjective> leakables;
  List<Objective> objectives;
  private Match match;
  private ObjectivesModule module;

  // Phase caches
  private final List<DestroyableObjective> objectivesWithPhases = new ArrayList<>();
  private final Map<DestroyablePhase, List<DestroyableObjective>> phases = new HashMap<>();

  public ObjectivesBridge(ObjectivesModule module) {
    this.module = module;
    this.match = module.getMatch();
    this.listeners = module.getListeners();
    this.monuments = module.getObjectivesByType(MonumentObjective.class);
    this.hills = module.getObjectivesByType(HillObjective.class);
    this.wools = module.getObjectivesByType(WoolObjective.class);
    this.flags = module.getObjectivesByType(FlagObjective.class);
    this.leakables = module.getObjectivesByType(LeakableObjective.class);

    this.objectives = Lists.newArrayList();
    this.objectives.addAll(this.monuments);
    this.objectives.addAll(this.hills);
    this.objectives.addAll(this.wools);
    this.objectives.addAll(this.flags);
    this.objectives.addAll(this.leakables);
    populatePhaseCache();
  }

  @Override
  public boolean broadcastCompletion(Objective objective, Group group, Optional<Player> cause) {
    if (this.objectives.isEmpty()) {
      return false;
    }

    Localizable objectiveName = objective.getName().toText(group.getTeamColor().getChatColor());

    LocalizedText broadcast;

    if (cause.isPresent()) {
      Player player = cause.get();
      Optional<Competitor> competitor = this.match.getRequiredModule(GroupsModule.class)
          .getCompetitorOf(player);

      UnlocalizedText who = new UnlocalizedText(cause.get().getName(),
          competitor.isPresent() ? competitor.get().getChatColor() : ChatColor.WHITE);
      broadcast = Messages.GENERIC_OBJECTIVE_COMPLETED_BY.with(objectiveName, who);
      if (objective instanceof LeakableObjective) {
        broadcast = Messages.GENERIC_LEAKABLE_LEAKED_BY.with(objectiveName, who);
      }
    } else {
      broadcast = Messages.GENERIC_OBJECTIVE_COMPLETED.with(objectiveName);
      if (objective instanceof LeakableObjective) {
        broadcast = Messages.GENERIC_LEAKABLE_LEAKED.with(objectiveName);
      }
    }

    this.match.broadcast(Messages.UI_IMPORTANT.with(TextStyle.ofBold(), broadcast));

    return true;
  }

  @Override
  public void onOpen(ObjectivesModule module) {
    Events.register(new LocatableUpdater());

    if (monuments.size() > 0) {
      listeners.add(new MonumentListener(module, monuments));
      listeners.add(new LocatableListener(monuments));
    }
    if (hills.size() > 0) {
      listeners.add(new HillListener(module, hills));
    }
    if (wools.size() > 0) {
      listeners.add(new WoolListener(module, wools));
      listeners.add(new LocatableListener(wools));
    }
    if (flags.size() > 0) {
      listeners.add(new FlagListener(module, flags));
      listeners.add(new LocatableListener(flags));
    }
    if (leakables.size() > 0) {
      listeners.add(new LeakableListener(module, leakables));
      listeners.add(new LocatableListener(leakables));
    }

    listeners.add(this);
  }

  @Override
  public void onClose(ObjectivesModule module) {

  }

  public void populatePhaseCache() {
    objectivesWithPhases.clear();
    objectivesWithPhases.addAll(
        this.getLeakables().stream().filter(objective -> objective.getPhase().isPresent())
            .collect(Collectors.toList()));
    objectivesWithPhases.addAll(
        this.getMonuments().stream().filter(objective -> objective.getPhase().isPresent())
            .collect(Collectors.toList()));

    phases.clear();
    objectivesWithPhases.forEach(objective -> {
      DestroyablePhase phase = objective.getPhase().get();
      phases.putIfAbsent(phase, new ArrayList<>());
      phases.get(phase).add(objective);
    });
  }

  public void startPhaseCountdowns(Match match) {
    phases.forEach((phase, withPhase) -> {
      PhaseApplyCountdown countdown = new PhaseApplyCountdown(match, phase.getDelay(),
          phase, withPhase);
      CompendiumPlugin.getInstance().getCountdownManager().start(countdown);
    });
  }

  @EventHandler
  public void handlePhaseCountdowns(MatchStateChangeEvent event) {
    if (event.isChangeToPlaying() && event.getFrom().isPresent() && !event.getFrom().get()
        .isPlaying()) {
      startPhaseCountdowns(event.getMatch());
      return;
    }
    if (event.isChangeToNotPlaying() && event.getFrom().isPresent() && event.getFrom().get()
        .isPlaying()) {
      CompendiumPlugin.getInstance().getCountdownManager()
          .cancelAll(countdown -> countdown instanceof PhaseApplyCountdown);
    }
  }

  @Override
  public CheckResult performCaptureCheck(Objective objective, Team team) {
    if (objective instanceof FlagObjective) {
      return CheckResult.valueOf(((FlagObjective) objective).isCarrier(team));
    }
    if (objective instanceof HillObjective) {
      return CheckResult.valueOf(
          ((HillObjective) objective).getOwner().isPresent() && ((HillObjective) objective)
              .getOwner().get().equals(team));
    }

    return CheckResult.IGNORE;
  }

  @Override
  public CheckResult performCaptureCheck(Objective objective) {
    if (objective instanceof FlagObjective) {
      return CheckResult.valueOf(((FlagObjective) objective).isCarried());
    }
    if (objective instanceof HillObjective) {
      return CheckResult.valueOf(((HillObjective) objective).getOwner().isPresent());
    }

    return CheckResult.IGNORE;
  }
}
