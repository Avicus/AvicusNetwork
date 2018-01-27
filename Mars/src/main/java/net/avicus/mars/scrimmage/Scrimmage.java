package net.avicus.mars.scrimmage;

import com.lambdaworks.com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.ToString;
import net.avicus.magma.database.model.impl.ReservedSlot;
import net.avicus.magma.database.model.impl.TeamMember;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.network.user.Users;
import net.avicus.magma.network.user.rank.Ranks;
import net.avicus.mars.CompetitiveEvent;
import net.avicus.mars.MarsTeam;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@ToString
public class Scrimmage implements CompetitiveEvent {

  private final ReservedSlot reservation;
  private final MarsTeam team;
  private final List<MarsTeam> invitedTeams;
  private final List<Integer> invitedUsers;

  public Scrimmage(ReservedSlot reservation, MarsTeam team) {
    this.reservation = reservation;
    this.team = team;
    this.invitedTeams = new ArrayList<>();
    this.invitedUsers = new ArrayList<>();
  }

  public List<String> permissions(Player player) {
    User user = Users.user(player);

    if (user == null) {
      return Lists.newArrayList();
    }

    TeamMember.Role role = this.team.getRole(user.getId()).orElse(null);

    List<String> perms = new ArrayList<>();

    if (role == null) {
      return perms;
    }

    switch (role) {
      case LEADER:
        Ranks.getPermOnly("scrim-leader").ifPresent(r -> perms.addAll(r.getPermissions()));
      case MEMBER:
        Ranks.getPermOnly("scrim-player").ifPresent(r -> perms.addAll(r.getPermissions()));
    }

    return perms;
  }

  public boolean canInvite(CommandSender sender) {
    if (!(sender instanceof Player) || sender.isOp()) {
      return true;
    }

    Player player = (Player) sender;
    return this.team.getRole(Users.user(player).getId()).orElse(null) == TeamMember.Role.LEADER;
  }

  public boolean toggleInviteTeam(MarsTeam team) {
    Optional<MarsTeam> existing = this.invitedTeams.stream()
        .filter((test) -> test.getId() == team.getId())
        .findAny();

    if (existing.isPresent()) {
      this.invitedTeams.remove(existing.get());
      return false;
    } else {
      this.invitedTeams.add(team);
      return true;
    }
  }

  public boolean toggleInviteUser(User user) {
    if (this.invitedUsers.contains(user.getId())) {
      // Cast to integer on purpose
      this.invitedUsers.remove((Integer) user.getId());
      return false;
    } else {
      this.invitedUsers.add(user.getId());
      return true;
    }
  }

  private boolean isInvited(User user) {
    if (this.invitedUsers.contains(user.getId())) {
      return true;
    }
    for (MarsTeam team : this.invitedTeams) {
      if (team.isMember(user.getId())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean canJoinServer(User user) {
    boolean member = this.team.isMember(user.getId());
    return member || isInvited(user);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Scrimmage)) {
      return false;
    }
    Scrimmage other = (Scrimmage) obj;
    return other.reservation.getId() == this.reservation.getId();
  }
}
