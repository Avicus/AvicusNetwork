package net.avicus.hook.group;

import com.github.theholywaffle.teamspeak3.api.CommandFuture;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerGroup;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import net.avicus.hook.Hook;
import net.avicus.hook.HookConfig;
import net.avicus.hook.Main;
import net.avicus.hook.wrapper.HookClient;
import net.avicus.hook.wrapper.HookGroup;
import net.avicus.magma.database.model.impl.Rank;
import org.joda.time.Duration;

public class Groups {

  private static Hook hook = Main.getHook();

  // A map that links a TS group id to our wrappers.
  private static ConcurrentHashMap<Integer, HookGroup> rankRelations = new ConcurrentHashMap<>();

  public static void init() {
    if (!HookConfig.Groups.isEnabled()) {
      return;
    }

    mapRanks();
    Main.getHook().getCommands().put("tempassign", GroupCommands::assignTempRank);
  }

  private static void mapRanks() {
    for (ServerGroup group : hook.getApi().getServerGroups()) {
      Optional<Rank> rank = hook.getDatabase().getRanks().findByName(group.getName());

      if (rank.isPresent()) {
        rankRelations.put(group.getId(), new HookGroup(rank.get(), group));
      }
    }
  }

  public static Optional<Rank> getRankFromServerGroup(int id) {
    Optional<Rank> result = Optional.empty();
    if (rankRelations.containsKey(id)) {
      result = Optional.of(rankRelations.get(id).getRank());
    }

    return result;
  }

  public static Optional<ServerGroup> getServerGroupFromRank(int id) {
    Optional<ServerGroup> result = Optional.empty();

    List<HookGroup> found = rankRelations.values()
        .stream()
        .filter(group -> group.getRank().getId() == id)
        .collect(Collectors.toList());

    if (!found.isEmpty()) {
      result = Optional.of(found.get(0).getServerGroup());
    }

    return result;
  }

  public static void assignGroupTimed(HookGroup group, HookClient client, Duration time) {
    CommandFuture<Boolean> result = hook.getApiAsync()
        .addClientToServerGroup(group.getServerGroup().getId(), client.getClient().getDatabaseId());

    result.onSuccess(new CommandFuture.SuccessListener<Boolean>() {
      @Override
      public void handleSuccess(Boolean aBoolean) {
        Main.getExecutor().schedule(() -> {
          hook.getApi().removeClientFromServerGroup(group.getServerGroup().getId(),
              client.getClient().getDatabaseId());
        }, time.getStandardSeconds(), TimeUnit.SECONDS);
      }
    });
  }
}
