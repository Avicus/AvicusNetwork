package net.avicus.hook.friends;

import com.google.common.collect.ArrayListMultimap;
import com.sk89q.bukkit.util.CommandsManagerRegistration;
import java.util.List;
import java.util.Optional;
import net.avicus.hook.Hook;
import net.avicus.hook.HookConfig;
import net.avicus.hook.friends.FriendSessionHandler.FriendJoinMessage;
import net.avicus.hook.friends.FriendSessionHandler.FriendLeaveMessage;
import net.avicus.hook.utils.Events;
import net.avicus.magma.Magma;
import net.avicus.magma.database.model.impl.ServerCategory;
import net.avicus.magma.database.model.impl.User;

public class Friends {

  // user id -> list of friends
  private static ArrayListMultimap<Integer, User> friends = ArrayListMultimap.create();
  private static FriendSessionHandler handler = new FriendSessionHandler();

  public static void init(CommandsManagerRegistration cmds) {
    Events.register(new FriendsListener());
    cmds.register(FriendCommands.class);
    if (HookConfig.Friends.isRedis()) {
      Hook.redis().register(handler);
    }
  }

  public static List<User> get(User user) {
    return friends.get(user.getId());
  }

  public static void join(User user) {
    if (HookConfig.Friends.isRedis()) {
      Optional<ServerCategory> local = Magma.get().localServer()
          .getCategory(Magma.get().database().getServerCategories());
      if (local.isPresent()) {
        if (local.get().getOptions().isPublishFriends()) {
          return;
        }
      }
      Hook.redis().publish(new FriendJoinMessage(user, Hook.server()));
    }
  }

  public static void leave(User user) {
    if (HookConfig.Friends.isRedis()) {
      Hook.redis().publish(new FriendLeaveMessage(user, Hook.server()));
    }
  }

  public static void reload(User user) {
    friends.removeAll(user.getId());
    for (int friendId : user.friendIds(Hook.database(), true)) {
      Optional<User> friend = Hook.database().getUsers().findById(friendId);
      if (friend.isPresent()) {
        friends.put(user.getId(), friend.get());
      }
    }
  }

  public static boolean isFriend(User user, int friendId) {
    if (!friends.containsKey(user.getId())) {
      return false;
    }

    for (User friend : friends.get(user.getId())) {
      if (friend.getId() == friendId) {
        return true;
      }
    }
    return false;
  }

  public static void unload(User user) {
    friends.removeAll(user.getId());
  }
}
