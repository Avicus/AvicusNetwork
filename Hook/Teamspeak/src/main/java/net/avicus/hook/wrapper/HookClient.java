package net.avicus.hook.wrapper;

import com.github.theholywaffle.teamspeak3.api.CommandFuture;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerGroup;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import lombok.Getter;
import net.avicus.compendium.StringUtil;
import net.avicus.hook.Hook;
import net.avicus.hook.Main;
import net.avicus.hook.client.Clients;
import net.avicus.hook.group.Groups;
import net.avicus.magma.database.Database;
import net.avicus.magma.database.model.impl.Rank;
import net.avicus.magma.database.model.impl.RankMember;
import net.avicus.magma.database.model.impl.User;

/**
 * Wrapper class that represents a TS {@link com.github.theholywaffle.teamspeak3.api.wrapper.Client}
 * that is connected to a {@link User}.
 */
public class HookClient {

  private final Logger logger;
  @Getter
  private final String clientUid;
  @Getter
  private final Set<String> permissions = new HashSet<>();
  private Database database = Main.getHook().getDatabase();
  private Hook hook = Main.getHook();
  @Getter
  private User user;
  @Getter
  private List<Rank> ranks = new ArrayList<>();

  public HookClient(String tsClientUId, int userId) {
    this.logger = Main.getLogger("Client " + tsClientUId);
    this.clientUid = tsClientUId;
    CommandFuture<ClientInfo> infoFuture = hook.getApiAsync().getClientByUId(tsClientUId);

    infoFuture.onSuccess(new CommandFuture.SuccessListener<ClientInfo>() {
      @Override
      public void handleSuccess(ClientInfo clientInfo) {
        Optional<User> user = database.getUsers().findById(userId);
        if (!user.isPresent()) {
          logger.warning("Could not find user with id " + userId + ". Kicking from server.");
          hook.getApi().kickClientFromServer(
              "An unknown error has occurred, please try logging in again in a moment.",
              clientInfo);
          return;
        }
        setUser(user.get());

        loadRanks();
        updateGroups();
      }
    });
  }

  public Client getClient() {
    return hook.getApi().getClientByUId(this.clientUid);
  }

  public void message(String message) {
    Clients.messageClient(getClient().getId(), message);
  }

  private void setUser(User user) {
    this.user = user;
  }

  private void loadRanks() {
    for (RankMember member : user.memberships(database)) {
      Rank rank = database.getRanks().findById(member.getRankId()).get();
      this.ranks.add(rank);
      this.permissions.addAll(database.getRanks().getTSPermissions(rank));
    }
    logger.info("Associated Ranks (DB): " + StringUtil
        .join(this.ranks, ", ", new StringUtil.Stringify<Rank>() {
          @Override
          public String on(Rank object) {
            return object.getName();
          }
        }));
  }

  private void updateGroups() {
    final List<ServerGroup> toAdd = new ArrayList<>();
    final List<Integer> toAddIds = new ArrayList<>();
    final List<ServerGroup> toRemove = new ArrayList<>();

    CommandFuture<List<ServerGroup>> commandFuture = hook.getApiAsync()
        .getServerGroupsByClient(getClient());
    commandFuture.onSuccess(new CommandFuture.SuccessListener<List<ServerGroup>>() {
      @Override
      public void handleSuccess(List<ServerGroup> serverGroups) {
        List<Integer> groupIds = serverGroups.stream().map(ServerGroup::getId)
            .collect(Collectors.toList());

        for (Rank rank : ranks) {
          Optional<ServerGroup> group = Groups.getServerGroupFromRank(rank.getId());

          // This means a user has a rank which is not a TS group.
          if (!group.isPresent()) {
            continue;
          }

          toAdd.add(group.get());
          toAddIds.add(group.get().getId());
        }

        for (ServerGroup group : serverGroups) {
          // This means a user has a group on TS that does not match any DB ranks, we ignore these.
          if (!Groups.getRankFromServerGroup(group.getId()).isPresent()) {
            continue;
          }

          // User no longer has this rank.
          if (!toAddIds.contains(group.getId())) {
            toRemove.add(group);
          }
        }

        // Remove all of the groups a user already has.
        toAddIds.removeAll(groupIds);

        toAdd.removeAll(
            toAdd.stream().filter(serverGroup -> !toAddIds.contains(serverGroup.getId()))
                .collect(Collectors.toList()));

        for (ServerGroup remove : toRemove) {
          if (!hook.getApi()
              .removeClientFromServerGroup(remove.getId(), getClient().getDatabaseId())) {
            logger.warning("Cannot remove " + remove.getName());
          }
        }

        for (ServerGroup add : toAdd) {
          if (!hook.getApi().addClientToServerGroup(add.getId(), getClient().getDatabaseId())) {
            logger.warning("Cannot add " + add.getName());
          }
        }
      }
    });

    if (!toAdd.isEmpty()) {
      logger.info(
          "Added Groups: " + StringUtil
              .join(toAdd, ", ", new StringUtil.Stringify<ServerGroup>() {
                @Override
                public String on(ServerGroup object) {
                  return object.getName();
                }
              }));
    }

    if (!toRemove.isEmpty()) {
      logger.info("Removed Groups: " + StringUtil
          .join(toRemove, ", ", new StringUtil.Stringify<ServerGroup>() {
            @Override
            public String on(ServerGroup object) {
              return object.getName();
            }
          }));
    }
  }

  public boolean hasPermission(String perm) {
    return this.permissions.contains(perm);
  }
}
