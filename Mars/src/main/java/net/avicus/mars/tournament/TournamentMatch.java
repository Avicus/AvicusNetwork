package net.avicus.mars.tournament;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.magma.database.model.impl.Tournament;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.network.user.Users;
import net.avicus.mars.CompetitiveEvent;
import net.avicus.mars.MarsTeam;
import org.bukkit.entity.Player;

public class TournamentMatch implements CompetitiveEvent {

  @Getter
  private final Tournament tournament;
  private final Match match;

  @Getter
  private final List<MarsTeam> teams;
  private final List<Integer> ignoredUsers;
  @Getter
  private final GroupsModule groupsModule;
  @Getter
  @Setter
  boolean ongoing;
  private HashMap<MarsTeam, Group> teamsToGroups = new HashMap<>();
  // Ready
  @Getter
  @Setter
  private boolean refsReady = false;
  private HashMap<MarsTeam, Boolean> teamReadyStatuses = new HashMap<>();

  public TournamentMatch(Tournament tournament, Match match) {
    this.tournament = tournament;
    this.match = match;
    this.teams = new ArrayList<>();
    this.ignoredUsers = new ArrayList<>();
    this.groupsModule = match.getRequiredModule(GroupsModule.class);
    this.ongoing = true;
  }

  public TournamentMatch copyAndSetup(Match newMatch) {
    this.ongoing = false;

    TournamentMatch copy = new TournamentMatch(this.tournament, newMatch);
    copy.ignoredUsers.addAll(this.ignoredUsers);

    for (MarsTeam team : this.teams) {
      copy.toggleRegisterTeam(team);
      copy.assignTeamToGroup(team);
      copy.renameAtlasTeam(team);
    }

    for (User user : Users.list()) {
      copy.assignPlayerToGroup(user.getId());
    }

    return copy;
  }

  public void reset() {
    this.teams.clear();
    this.ignoredUsers.clear();
    this.teamsToGroups.clear();
    this.refsReady = false;
    this.teamReadyStatuses.clear();
  }

  // Registration

  public boolean toggleRegisterTeam(MarsTeam team) {
    Optional<MarsTeam> existing = this.teams.stream()
        .filter((test) -> test.getId() == team.getId())
        .findAny();

    if (existing.isPresent()) {
      this.teams.remove(existing.get());
      this.teamReadyStatuses.remove(existing.get());
      this.refsReady = false;
      if (this.teamsToGroups.containsKey(existing.get())) {
        this.groupsModule.renameGroup(this.teamsToGroups.get(existing.get()),
            this.teamsToGroups.get(existing.get()).getOriginalName());
      }
      this.teamsToGroups.remove(existing.get());
      return false;
    } else {
      this.teams.add(team);
      this.teamReadyStatuses.put(team, false);
      assignTeamToGroup(team);
      renameAtlasTeam(team);
      match.getPlayers().forEach((p) -> assignPlayerToGroup(Users.user(p).getId()));
      return true;
    }
  }

  public boolean toggleIgnoreUser(User user) {
    if (this.ignoredUsers.contains(user.getId())) {
      // Cast to integer on purpose
      this.ignoredUsers.remove((Integer) user.getId());
      return false;
    } else {
      this.ignoredUsers.add(user.getId());
      return true;
    }
  }

  private boolean isRegistered(User user) {
    for (MarsTeam team : this.teams) {
      if (team.isMember(user.getId())) {
        return true;
      }
    }
    return false;
  }

  // Atlas links

  public int getRemainingUnregistered() {
    return groupsModule.getCompetitors().size() - this.teams.size();
  }

  public void renameAtlasTeam(MarsTeam team) {
    for (Group group : this.groupsModule.getGroups()) {
      if (group.isSpectator() || this.teamsToGroups.containsValue(group)) {
        continue;
      }

      this.teamsToGroups.put(team, group);
      this.groupsModule.renameGroup(group, new LocalizedXmlString(team.getName()));
      return;
    }
  }

  public Optional<Group> getIntendedPlayerGroup(int userId) {
    if (ignoredUsers.contains(userId)) {
      return Optional.empty();
    }

    Optional<Player> player = Users.player(userId);
    Optional<MarsTeam> playerTeam = Optional.empty();

    for (MarsTeam team : this.teams) {
      if (team.isMember(userId)) {
        playerTeam = Optional.of(team);
      }
    }

    if (!player.isPresent() || !playerTeam.isPresent()) {
      return Optional.empty();
    }

    Group target = this.teamsToGroups.get(playerTeam.get());
    return Optional.of(target);
  }

  public void assignPlayerToGroup(int userId) {
    Optional<Player> player = Users.player(userId);
    Optional<Group> target = getIntendedPlayerGroup(userId);

    if (!player.isPresent() || !target.isPresent()) {
      return;
    }

    this.groupsModule
        .changeGroup(player.get(), Optional.of(this.groupsModule.getGroup(player.get())),
            target.get(), false, false);
  }

  public Group assignTeamToGroup(MarsTeam team) {
    List<Group> assignable = this.groupsModule.getGroups().stream()
        .filter((group) -> !group.isSpectator() && this.teamsToGroups.containsValue(group))
        .collect(Collectors.toList());

    if (assignable.isEmpty()) {
      return null;
    }

    this.teamsToGroups.remove(team);
    this.teamsToGroups.put(team, assignable.get(0));
    return assignable.get(0);
  }

  // Readiness management

  public boolean toggleReadyTeam(MarsTeam team) {
    Optional<MarsTeam> existing = this.teamReadyStatuses.keySet().stream()
        .filter((test) -> test.getId() == team.getId())
        .findAny();

    if (existing.isPresent()) {
      boolean ready = this.teamReadyStatuses.get(existing.get());
      this.teamReadyStatuses.remove(existing.get());
      this.teamReadyStatuses.put(existing.get(), !ready);

      return !ready;
    }

    return false;
  }

  public boolean canStart() {
    Optional<Boolean> unReady = this.teamReadyStatuses.values().stream()
        .filter((test) -> !test)
        .findAny();

    return !unReady.isPresent() && this.refsReady;
  }

  @Override
  public boolean canJoinServer(User user) {
    return isRegistered(user);
  }
}
