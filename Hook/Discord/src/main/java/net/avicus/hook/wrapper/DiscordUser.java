package net.avicus.hook.wrapper;

import com.google.common.collect.Sets;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import net.avicus.compendium.StringUtil;
import net.avicus.hook.Hook;
import net.avicus.hook.HookConfig;
import net.avicus.hook.Main;
import net.avicus.hook.RoleManagementService;
import net.avicus.magma.database.Database;
import net.avicus.magma.database.model.impl.Rank;
import net.avicus.magma.database.model.impl.RankMember;
import net.avicus.magma.database.model.impl.Team;
import net.avicus.magma.database.model.impl.TeamMember;
import net.avicus.magma.database.model.impl.Tournament;
import net.avicus.magma.database.model.impl.User;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.managers.GuildController;
import net.dv8tion.jda.core.utils.PermissionUtil;

/**
 * Wrapper class that represents a discord {@link net.dv8tion.jda.core.entities.User}
 * that is connected to a {@link User}.
 */
public class DiscordUser {

  private final Logger logger;
  @Getter
  private final net.dv8tion.jda.core.entities.User discordUser;
  @Getter
  private final List<Rank> ranks = new ArrayList<>();
  @Getter
  private final PrivateChannel privateChannel;
  private Database database = Main.getHook().getDatabase();
  private Hook hook = Main.getHook();
  @Getter
  @Setter
  private User user;

  public DiscordUser(net.dv8tion.jda.core.entities.User discordUser) {
    this.logger = Main.getLogger("DiscordUser " + discordUser.getName());
    this.discordUser = discordUser;
    this.privateChannel = this.discordUser.openPrivateChannel().complete();
  }

  public void message(Message message) {
    this.privateChannel.sendMessage(message).submit();
  }

  public boolean message(String message) {
    try {
      this.privateChannel.sendMessage(message).submit();
    } catch (ErrorResponseException e) {
      this.logger.warning("Failed to send message to user (Blocked?).");
      this.hook.getMainGuild().getTextChannelById("330204192817938433").sendMessage(
          "Failed to send message to user `" + this.discordUser.getName() + "` (Blocked?).")
          .complete();
      return false;
    }
    return true;
  }

  public boolean shouldMessage() {
    try {
      long last = this.privateChannel.getLatestMessageIdLong();
      if (last > 0) {
        Message lastMessage = this.privateChannel.getMessageById(last).complete();
        return lastMessage != null && lastMessage.getCreationTime()
            .isBefore(OffsetDateTime.now().minusHours(12));
      }
    } catch (Exception ignored) {
    }
    return false;
  }

  public void loadRanks() {
    this.ranks.clear();
    for (RankMember member : user.memberships(database)) {
      Rank rank = database.getRanks().findById(member.getRankId()).get();
      this.ranks.add(rank);
    }
  }

  public void updateRoles(Guild guild) {
    final Set<Role> toAdd = Sets.newHashSet();
    final Set<Role> toRemove = Sets.newHashSet();

    Member member = guild.getMember(this.discordUser);

    List<Role> roles = guild.getRoles();

    GuildController controller = guild.getController();

    RoleManagementService roleManager = hook.getRoleManager(guild);

    if (guild.getIdLong() == hook.getMainGuild().getIdLong()) {
      Role registered = guild.getRolesByName("Registered", true).get(0);
      Role donor = guild.getRolesByName("Donator", true).get(0);
      boolean isDonor = this.ranks.stream().anyMatch(r ->
          r.getName().equalsIgnoreCase("Gold") ||
              r.getName().equalsIgnoreCase("Emerald") ||
              r.getName().equalsIgnoreCase("Diamond")
      );

      if (!member.getRoles().contains(registered)) {
        controller.addRolesToMember(member, registered).complete();
      }

      if (isDonor) {
        controller.addRolesToMember(member, donor).complete();
      } else {
        controller.removeRolesFromMember(member, donor).complete();
      }
    } else if (guild.getIdLong() == hook.getTmGuild().getIdLong()) {
      Tournament tm = hook.getDatabase().getTournaments().select()
          .where("id", HookConfig.getTournament()).execute().first();

      if (tm != null) {
        // Add Team Role
        TeamMember teamMember = hook.getDatabase().getTeamMembers()
            .findAcceptedByUser(this.user.getId()).orElse(null);
        if (teamMember != null) {
          Team team = hook.getDatabase().getTeams().findById(teamMember.getTeamId()).get();
          if (hook.getDatabase().getTournaments().isUserPlaying(tm, team, this.user.getId())) {
            logger.info("Registered for the tournament with " + team.getName());
            List<Role> roleList = new ArrayList<>(guild.getRolesByName(team.getName(), true));
            if (!roleList.isEmpty() && teamMember.getRole() == TeamMember.Role.LEADER) {
              roleList.addAll(guild.getRolesByName("Team-Leader", true));
            }

            if (!roleList.isEmpty()) {
              guild.getController().addRolesToMember(member, roleList.stream()
                  .filter(r -> PermissionUtil.canInteract(guild.getSelfMember(), r))
                  .collect(Collectors.toSet())).complete();
            }
          }
        }
      }
    }

    for (Rank rank : ranks) {
      Optional<HookRole> role = roleManager.getRoleFromRank(rank);

      // This means a user has a rank which is not a role.
      if (!role.isPresent() ||
          !PermissionUtil.canInteract(guild.getSelfMember(), role.get().getRole())) {
        continue;
      }

      toAdd.add(role.get().getRole());
    }

    for (Role role : roles) {
      // This means a user has a role that does not match any DB ranks, we ignore these.
      if (!roleManager.getRankFromRole(role).isPresent() ||
          !PermissionUtil.canInteract(guild.getSelfMember(), role)) {
        continue;
      }

      if (!member.getRoles().contains(role)) {
        continue;
      }

      // User no longer has this rank.
      if (!toAdd.contains(role)) {
        toRemove.add(role);
      }
    }

    Set<Role> add = toAdd.stream().filter(r -> !member.getRoles().contains(r))
        .collect(Collectors.toSet());

    if (!toRemove.isEmpty()) {
      controller.removeRolesFromMember(member, toRemove).complete();
    }

    if (!add.isEmpty()) {
      controller.addRolesToMember(member, add).complete();
    }

    if (!toRemove.isEmpty()) {
      logger.info("Removed " + StringUtil
          .join(new ArrayList<>(toRemove), " - ", new StringUtil.Stringify<Role>() {
            @Override
            public String on(Role object) {
              return object.getName();
            }
          }) + " from user.");
    }
  }
}
