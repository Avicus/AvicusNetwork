package net.avicus.atlas.sets.arcade;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.Getter;
import net.avicus.atlas.event.match.MatchOpenEvent;
import net.avicus.atlas.event.match.MatchStateChangeEvent;
import net.avicus.atlas.event.player.PlayerJoinDelayedEvent;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.Module;
import net.avicus.atlas.module.elimination.EliminationModule;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.objectives.ObjectivesModule;
import net.avicus.atlas.module.spawns.SpawnsModule;
import net.avicus.atlas.module.states.StatesModule;
import net.avicus.atlas.util.AtlasTask;
import net.avicus.magma.util.Sidebar;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

@Getter
public abstract class ArcadeGame implements Module {

  public static final Random RANDOM = new Random();
  private final Match match;
  private final BiPredicate<Player, Group> groupSelector;
  // Modules
  private final GroupsModule groupsModule;
  private final Collection<? extends Group> groups;
  private final EliminationModule eliminationModule;
  private final SpawnsModule spawnsModule;
  private final AtlasTask updateTask;
  private Collection<Player> playingPlayers = Lists.newArrayList();

  public ArcadeGame(Match match, BiPredicate<Player, Group> groupSelector) {
    this.match = match;
    this.groupSelector = groupSelector;
    this.groupsModule = match.getRequiredModule(GroupsModule.class);
    this.groups = this.getGroupsModule().getGroups();
    this.eliminationModule = match.getRequiredModule(EliminationModule.class);
    this.spawnsModule = match.getRequiredModule(SpawnsModule.class);

    this.updateTask = AtlasTask.of(() ->
        this.playingPlayers = groupsModule.getCompetitors().stream()
            .flatMap(c -> c.getPlayers().stream()).collect(
                Collectors.toList())
    );
  }

  private SBHook sideBar() {
    return ArcadeMain.getInstance().getSbHook();
  }

  @Override
  public void open() {
    if (sideBar() != null) {
      sideBar().setCurrentGame(this);
    }
    this.updateTask.repeat(40, 15);
  }

  @Override
  public void close() {
    this.updateTask.cancel0();
    if (sideBar() != null) {
      sideBar().setCurrentGame(null);
    }
  }

  public abstract void matchStart();

  public abstract void matchEnd();

  @EventHandler(priority = EventPriority.MONITOR)
  public void matchStateChange(final MatchStateChangeEvent event) {
    if (event.isChangeToPlaying()) {
      matchStart();
    }

    if (event.isChangeToNotPlaying()) {
      matchEnd();
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onJoin(PlayerJoinDelayedEvent e) {
    if (getMatch().getRequiredModule(StatesModule.class).isStarting()) {
      findDesiredGroup(e.getPlayer(), getGroups())
          .ifPresent(g -> getGroupsModule().changeGroup(e.getPlayer(), g, false, false));
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onOpen(MatchOpenEvent event) {
    event.getMatch().getPlayers().forEach(p -> {
      findDesiredGroup(p, groups).ifPresent(g -> this.groupsModule.changeGroup(p, g, false, false));
    });
  }

  private Optional<Group> findDesiredGroup(Player player, Collection<? extends Group> groups) {
    Optional<Group> res = Optional.empty();
    for (Group group : groups) {
      if (this.groupSelector.test(player, group)) {
        res = Optional.of(group);
        break;
      }
    }
    return res;
  }

  protected boolean anyAlive() {
    return !this.playingPlayers.isEmpty();
  }

  public abstract List<String> getRows(Player player, GroupsModule groups, Sidebar sidebar,
      ObjectivesModule module);

  protected void updateSideBar() {
    sideBar().getComponent().update();
  }

  protected void eliminateIf(Predicate<Player> eliminateIf, Consumer<Player> onEliminate) {
    this.playingPlayers.forEach(p -> {
      if (eliminateIf.test(p)) {
        this.eliminationModule.eliminate(p);
        onEliminate.accept(p);
      }
    });
  }

  protected void playSound(Sound sound, float pitch) {
    getPlayingPlayers().forEach(p -> p.playSound(p.getLocation(), sound, 1.4f, pitch));
  }

  protected void reSpawn() {
    this.playingPlayers.forEach(this.spawnsModule::spawn);
  }
}
