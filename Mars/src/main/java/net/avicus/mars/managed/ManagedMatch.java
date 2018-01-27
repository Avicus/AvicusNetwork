package net.avicus.mars.managed;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.avicus.magma.database.model.impl.User;
import net.avicus.mars.CompetitiveEvent;
import net.avicus.mars.MarsTeam;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@ToString
public class ManagedMatch implements CompetitiveEvent {

  private final List<MarsTeam> invitedTeams;
  private final List<Integer> invitedUsers;
  @Getter
  @Setter
  boolean ongoing;

  public ManagedMatch() {
    this.invitedTeams = new ArrayList<>();
    this.invitedUsers = new ArrayList<>();
  }

  public boolean canInvite(CommandSender sender) {
    return !(sender instanceof Player) || sender.isOp() || sender.hasPermission("mars.manage");
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
    return isInvited(user);
  }
}
