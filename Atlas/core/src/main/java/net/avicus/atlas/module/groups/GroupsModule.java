package net.avicus.atlas.module.groups;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.sk89q.minecraft.util.commands.ChatColor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.AtlasConfig;
import net.avicus.atlas.event.competitor.PlayerChangeCompetitorEvent;
import net.avicus.atlas.event.group.GroupRenameEvent;
import net.avicus.atlas.event.group.PlayerChangeGroupEvent;
import net.avicus.atlas.event.group.PlayerChangedGroupEvent;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.BridgeableModule;
import net.avicus.atlas.module.Module;
import net.avicus.atlas.module.ModuleBridge;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.module.spawns.SpawnsModule;
import net.avicus.atlas.util.Events;
import net.avicus.atlas.util.color.TeamColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

@ToString(exclude = "match")
public abstract class GroupsModule extends BridgeableModule<ModuleBridge<GroupsModule>> implements
    Module {

  @Getter
  private final Match match;
  private final ObserverTask observerTask;
  private final List<Listener> listeners;
  private final Optional<PersistenceListener> persistenceListener;
  private boolean lockPlayers;

  public GroupsModule(Match match, boolean lockPlayers) {
    this.match = match;
    this.lockPlayers = lockPlayers;
    this.observerTask = new ObserverTask(match, this);
    this.listeners = Arrays.asList(
        new ObserverListener(this),
        new GroupsListener(this),
        new SpectatorListener(this));

    if (this.lockPlayers) {
      this.persistenceListener = Optional.of(new PersistenceListener(this).init());
    } else {
      this.persistenceListener = Optional.empty();
    }
    buildBridges(this);
  }

  /**
   * Determine the expected group size ratio based on min player counts.
   *
   * @param group The group.
   */
  private double groupMinSizeRatio(Group group) {
    int min = group.getMinPlayers();
    int totalMin = getGroups().stream().filter(g -> !g.isSpectator()).mapToInt(Group::getMinPlayers)
        .sum();
    return totalMin == 0 ? 0 : (double) min / (double) totalMin;
  }

  /**
   * Determine the expected group size ratio based on max player counts.
   *
   * @param group The group.
   */
  private double groupMaxSizeRatio(Group group) {
    int max = group.getMaxPlayers();
    int totalMax = getGroups().stream().filter(g -> !g.isSpectator()).mapToInt(Group::getMaxPlayers)
        .sum();
    return totalMax == 0 ? 0 : (double) max / (double) totalMax;
  }

  /**
   * Determine the current group size ratio (A team of 4 versus a team of 3 has a ratio of 1.75)
   *
   * @param group The group to check.
   * @param additionalPlayers Players to add include into the size of the group.
   */
  private double currentGroupSizeRatio(Group group, int additionalPlayers) {
    int size = group.size() + additionalPlayers;
    int totalSize = getGroups().stream().filter(g -> !g.isSpectator()).mapToInt(Group::size).sum()
        + additionalPlayers;
    return totalSize == 0 ? 0 : (double) size / (double) totalSize;
  }

  /**
   * Check if a group has enough players and is balanced, based on its size ratio.
   *
   * @param additionalPlayers Players to include into the size of the group.
   */
  public boolean isGroupBalanced(Group group, int additionalPlayers) {
    // Ignore spectators
    if (group.isSpectator()) {
      return true;
    }

    // Ignore if scrimmage
    if (AtlasConfig.isScrimmage()) {
      return true;
    }

    int newSize = group.size() + additionalPlayers;

    // Counts below min players is acceptable no matter what
    if (newSize < group.getMinPlayers()) {
      return true;
    }

    // Group is always balanced if its the only group
    if (getGroups().stream().filter(g -> !g.isSpectator()).count() == 1) {
      return true;
    }

    // Only check group size ratios if there is a difference in group sizes
    // greater than 1. This is equivalent to: groups are considered balanced
    // if the the group sizes are equal.
    boolean checkRatios = false;
    for (Group test : getGroups()) {
      if (test.isSpectator()) {
        continue;
      }

      int difference = Math.abs(newSize - test.size());

      if (difference > 1) {
        checkRatios = true;
        break;
      }
    }

    if (checkRatios) {
      double minExpected = groupMinSizeRatio(group);
      double maxExpected = groupMaxSizeRatio(group);
      double low = Math.min(minExpected, maxExpected);
      double high = Math.max(minExpected, maxExpected);

      double current = currentGroupSizeRatio(group, additionalPlayers);
      for (double x = low; x <= high; x += AtlasConfig.getMaxGroupImbalance() / 2.0) {
        double diff = current - x;

        // Needs more players
        if (diff < 0) {
          return true;
        }

        // This team has more players, but it's okay if some more join
        if (diff < AtlasConfig.getMaxGroupImbalance()) {
          return true;
        }
      }
      return false;
    } else {
      return true;
    }
  }

  public void refreshObservers() {
    this.observerTask.execute();
  }

  public boolean isObserving(Player player) {
    return getGroup(player).isObserving();
  }

  public boolean isObservingOrDead(Player player) {
    return isObserving(player) || isDead(player) || isSpawning(player);
  }

  private boolean isDead(Player player) {
    return this.match.getRequiredModule(SpawnsModule.class).isDead(player);
  }

  private boolean isSpawning(Player player) {
    return this.match.getRequiredModule(SpawnsModule.class).isSpawning(player);
  }

  @Override
  public void open() {
    for (Group groups : getGroups()) {
      groups.setObserving(true);
    }

    this.observerTask.start();
    Events.register(this.listeners);
    if (this.lockPlayers && this.persistenceListener.isPresent()) {
      Events.register(this.persistenceListener.get());
    }
    getBridges().values().forEach(b -> b.onOpen(this));
  }

  @Override
  public void close() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      player.setDisplayName(player.getName());
    }
    this.observerTask.cancel0();
    Events.unregister(this.listeners);
    if (this.lockPlayers && this.persistenceListener.isPresent()) {
      Events.unregister(this.persistenceListener.get());
    }
    getBridges().values().forEach(b -> b.onClose(this));
  }

  /**
   * Used for displays.
   */
  public abstract CompetitorRule getCompetitorRule();

  /**
   * The complete collection of groups in this match.
   */
  public abstract Collection<? extends Group> getGroups();

  /**
   * The complete collection of competitors engaged in the match.
   */
  public abstract Collection<? extends Competitor> getCompetitors();


  public abstract Collection<? extends Competitor> getCompetitors(Group group);

  /**
   * Try to getFirst the object that represents who the player is fighting for.
   */
  public Optional<Competitor> getCompetitorOf(Player player) {
    for (Competitor competitor : getCompetitors()) {
      if (competitor.hasPlayer(player)) {
        return Optional.of(competitor);
      }
    }
    return Optional.empty();
  }

  public Group getGroup(Player player) {
    for (Group group : getGroups()) {
      if (group.isMember(player)) {
        return group;
      }
    }
    throw new RuntimeException(
        "getGroup(player) called on player while not on team. Player: " + player.getName());
  }

  public boolean renameGroup(Group group, LocalizedXmlString newName) {
    GroupRenameEvent call = Events.call(new GroupRenameEvent(group, newName));
    if (call.isCancelled()) {
      return false;
    }
    Preconditions.checkArgument(call.getName() != null, "Group name cannot be null.");
    group.setName(call.getName());
    return true;
  }

  public Optional<Group> changeGroup(Player player, Group newGroup, boolean triggerSpawn,
      boolean triggerTeleport) {
    return changeGroup(player, Optional.of(getGroup(player)), newGroup, triggerSpawn,
        triggerTeleport);
  }

  public Optional<Group> changeGroup(Player player, Optional<Group> oldGroup, Group newGroup,
      boolean triggerSpawn, boolean triggerTeleport) {
    return changeGroup(player, oldGroup, newGroup, triggerSpawn, triggerTeleport, true);
  }

  public Optional<Group> changeGroup(Player player, Optional<Group> oldGroup, Group newGroup,
      boolean triggerSpawn, boolean triggerTeleport, boolean callEvent) {
    return changeGroup(player, oldGroup, newGroup, triggerSpawn, triggerTeleport, callEvent, false);
  }

  public Optional<Group> changeGroup(Player player, Optional<Group> oldGroup, Group newGroup,
      boolean triggerSpawn, boolean triggerTeleport, boolean callEvent, boolean force) {
    Optional<Competitor> oldCompetitor = getCompetitorOf(player);

    PlayerChangeGroupEvent call = new PlayerChangeGroupEvent(player, oldGroup, newGroup,
        triggerSpawn, triggerTeleport, force);

    if (callEvent) {
      oldGroup.ifPresent((group) -> {
        Events.call(call);
      });
    }

    if (call.isCancelled()) {
      return Optional.empty();
    }

    if (oldGroup.isPresent()) {
      oldGroup.get().remove(player);
    }
    call.getGroup().add(player);

    TeamColor color = call.getGroup().getTeamColor();

    Optional<Competitor> competitor = getCompetitorOf(player);
    if (competitor.isPresent()) {
      color = competitor.get().getTeamColor();
    }

    player.setDisplayName(color.getChatColor() + player.getName() + ChatColor.RESET);

    if (!oldCompetitor.equals(competitor)) {
      PlayerChangeCompetitorEvent callCompetitor = new PlayerChangeCompetitorEvent(player,
          oldCompetitor, competitor);
      Events.call(callCompetitor);
    }

    if (callEvent) {
      PlayerChangedGroupEvent change = new PlayerChangedGroupEvent(player, oldGroup, newGroup,
          triggerSpawn, triggerTeleport);
      Events.call(change);
    }

    return Optional.of(call.getGroup());
  }

  public void shuffle() {
    shuffle(getGroups());
  }

  public void shuffle(Collection<? extends Group> groups) {
    // Get groups that are larger than one person and are playing in the match.
    List<Group> shuffleable = groups.stream()
        .filter(group -> !group.isObserving() && group.getMembers()
            .size() > 1)
        .collect(Collectors.toList());

    List<Player> playingPlayers = new ArrayList<>();

    for (Group group : shuffleable) {
      playingPlayers.addAll(group.getPlayers());
      group.getPlayers().clear();
    }

    Random rand = new Random();

    for (Player player : playingPlayers) {
      changeGroup(player, shuffleable.get(rand.nextInt(shuffleable.size())), true, true);
    }
  }

  public Spectators getSpectators() {
    for (Group group : getGroups()) {
      if (group instanceof Spectators) {
        return (Spectators) group;
      }
    }
    throw new RuntimeException("No spectator team found.");
  }

  public List<Group> search(CommandSender viewer, String query) {
    if (viewer.hasPermission("atlas.groups.query.all") && query.equals("@all")) {
      return Lists.newArrayList(this.getGroups());
    }

    // Preliminary check by id
    for (Group group : getGroups()) {
      if (group.getId().equals(query)) {
        return Collections.singletonList(group);
      }
    }

    List<Group> result = new ArrayList<>();
    Locale locale = viewer.getLocale();

    for (Group group : getGroups()) {
      String translated = group.getName().toText().translate(locale).toPlainText();
      if (translated.toLowerCase().startsWith(query.toLowerCase())) {
        result.add(group);
      }
    }

    result.sort((o1, o2) -> {
      String name1 = o1.getName().translate(viewer).toLowerCase();
      String name2 = o2.getName().translate(viewer).toLowerCase();

      if (name1.equals(query.toLowerCase())) {
        return 1;
      }

      if (name2.equals(query.toLowerCase())) {
        return -1;
      }

      if (name1.startsWith(query.toLowerCase()) && !name2.startsWith(query.toLowerCase())) {
        return 1;
      }

      if (!name1.startsWith(query.toLowerCase()) && name2.startsWith(query.toLowerCase())) {
        return -1;
      }

      return 0;
    });

    return result;
  }

  public boolean isSpectator(Player player) {
    return getGroup(player).isSpectator();
  }
}
