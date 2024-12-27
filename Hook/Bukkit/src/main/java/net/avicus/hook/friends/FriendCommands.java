package net.avicus.hook.friends;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandNumberFormatException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.avicus.compendium.Paginator;
import net.avicus.compendium.commands.exception.InvalidPaginationPageException;
import net.avicus.compendium.commands.exception.MustBePlayerCommandException;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizableFormat;
import net.avicus.compendium.locale.text.LocalizedFormat;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.compendium.utils.Strings;
import net.avicus.hook.Hook;
import net.avicus.hook.sessions.Sessions;
import net.avicus.hook.utils.HookTask;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.Magma;
import net.avicus.magma.database.model.impl.Friend;
import net.avicus.magma.database.model.impl.Session;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.network.user.Users;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FriendCommands {

  @Command(aliases = "friends", desc = "View your friends.", usage = "(page)", min = 0, max = 1)
  public static void friends(CommandContext cmd, CommandSender sender)
      throws MustBePlayerCommandException, CommandNumberFormatException {
    MustBePlayerCommandException.ensurePlayer(sender);

    Player player = (Player) sender;
    User user = Users.user(player);

    List<User> friends = Friends.get(user);

    int page = cmd.getInteger(0, 1) - 1;
    new HookTask() {
      @Override
      public void run() throws Exception {
        int onlineCount = 0;
        Map<User, Session> latestSessions = new HashMap<>();
        for (User friend : friends) {
          Optional<Session> session = Hook.database().getSessions().findLatest(friend.getId());
          if (session.isPresent()) {
            latestSessions.put(friend, session.get());
            if (session.get().isActive()) {
              onlineCount++;
            }
          }
        }

        Collections.sort(friends, (o1, o2) -> {
          Session s1 = latestSessions.get(o1);
          Session s2 = latestSessions.get(o2);

          // Todo: Check if this is right
          if (s1 != null && s2 != null) {
            // Put online friends first
            int activeCompare = -Boolean.compare(s1.isActive(), s2.isActive());
            if (activeCompare != 0) {
              return activeCompare;
            }
          }

          // Alphabetical if both online, otherwise last seen
          if (s1 != null && s1.isActive() && s2 != null && s2.isActive()) {
            return o1.getName().compareTo(o2.getName());
          } else {
            Date expire1 = s1 == null ? new Date(0) : s1.getExpiredAt();
            Date expire2 = s2 == null ? new Date(0) : s2.getExpiredAt();

            return expire2.compareTo(expire1);
          }
        });

        Paginator<User> paginated = new Paginator<>(friends, 8);

        if (paginated.getPageCount() == 0) {
          sender.sendMessage(Messages.ERROR_NO_FRIENDS.with(ChatColor.RED));
          return;
        }

        if (!paginated.hasPage(page)) {
          sender.sendMessage(
              InvalidPaginationPageException.format(new InvalidPaginationPageException(paginated)));
          return;
        }

        // Header
        Localizable onlineText = new LocalizedNumber(onlineCount);
        Localizable pageText = new LocalizedNumber(page + 1);
        Localizable pageCount = new LocalizedNumber(paginated.getPageCount());
        LocalizableFormat titleFormat = Messages.UI_FRIENDS_ONLINE_PLURAL;
        if (onlineCount == 1) {
          titleFormat = Messages.UI_FRIENDS_ONLINE;
        }
        Localizable title = titleFormat.with(ChatColor.GREEN, onlineText, pageText, pageCount);
        sender.sendMessage(Strings
            .padChatComponent(title.render(sender), "-", ChatColor.YELLOW,
                ChatColor.AQUA));

        for (User friend : paginated.getPage(page)) {
          Localizable lastSeen = Sessions
              .formatLastSeen(player, friend, Optional.ofNullable(latestSessions.get(friend)));
          sender.sendMessage(lastSeen);
        }

      }
    }.nowAsync();
  }

  @Command(aliases = {"addfriend",
      "add"}, desc = "Add a friend.", usage = "<username>", min = 1, max = 1)
  public static void add(CommandContext cmd, CommandSender sender)
      throws MustBePlayerCommandException {
    MustBePlayerCommandException.ensurePlayer(sender);

    String query = cmd.getString(0);

    User user = Users.user((Player) sender);

    new HookTask() {
      @Override
      public void run() throws Exception {
        Optional<User> search = Hook.database().getUsers().findByName(query);

        if (!search.isPresent()) {
          sender.sendMessage(Messages.ERROR_NO_PLAYERS.with(ChatColor.RED));
          return;
        }

        User friend = search.get();

        if (friend.getId() == user.getId()) {
          sender.sendMessage(Messages.ERROR_FRIEND_YOURSELF.with(ChatColor.RED));
          return;
        }

        Optional<Friend> row = Hook.database().getFriends()
            .findByAssociation(user.getId(), friend.getId());

        if (row.isPresent()) {
          if (row.get().isAccepted()) {
            sender
                .sendMessage(Messages.ERROR_ALREADY_FRIENDS
                    .with(ChatColor.RED, friend.getName()));
          } else {
            sender.sendMessage(
                Messages.ERROR_ALREADY_REQUESTED_FRIENDS
                    .with(ChatColor.RED, friend.getName()));
          }
          return;
        }

        Optional<Friend> existingRequest = Hook.database().getFriends()
            .findByAssociation(friend.getId(), user.getId());

        if (existingRequest.isPresent()) {
          Friend request = existingRequest.get();
          request.setAccepted(Hook.database());

          Friend association = new Friend(user.getId(), friend.getId(), true);
          Hook.database().getFriends().insert(association).execute();

          // Alert the friend of his new friendship <3
          Magma.get().getApiClient().getAlerts().createFriendAccept(friend, user, association);

          sender
              .sendMessage(Messages.GENERIC_ACCEPTED_FRIEND.with(ChatColor.GOLD, friend.getName()));
        } else {
          Friend association = new Friend(user.getId(), friend.getId(), false);
          Hook.database().getFriends().insert(association).execute();

          // Alert the friend of the request
          Magma.get().getApiClient().getAlerts().createFriendRequest(friend, user, association);

          sender.sendMessage(
              Messages.GENERIC_REQUESTED_FRIEND.with(ChatColor.GOLD, friend.getName()));
        }

        // Reload
        Friends.reload(user);
        Friends.reload(friend);
      }
    }.nowAsync();
  }

  @Command(aliases = {"removefriend",
      "remove"}, desc = "Remove a friend or cancel a request.", usage = "<username>", min = 1, max = 1)
  public static void remove(CommandContext cmd, CommandSender sender)
      throws MustBePlayerCommandException {
    MustBePlayerCommandException.ensurePlayer(sender);

    String query = cmd.getString(0);

    User user = Users.user((Player) sender);

    new HookTask() {
      @Override
      public void run() throws Exception {
        Optional<User> search = Hook.database().getUsers().findByName(query);

        if (!search.isPresent()) {
          sender.sendMessage(Messages.ERROR_NO_PLAYERS.with(ChatColor.RED));
          return;
        }

        User friend = search.get();

        Optional<Friend> existingFriend = Hook.database().getFriends()
            .findByAssociation(user.getId(), friend.getId());

        if (!existingFriend.isPresent()) {
          sender.sendMessage(Messages.ERROR_NOT_FRIENDS.with(ChatColor.RED, friend.getName()));
          return;
        }

        // Remove, reload
        Hook.database().getFriends().destroyAllAssociations(user.getId(), friend.getId());
        Friends.reload(user);
        Friends.reload(friend);

        // Delete alerts if they are there
        Magma.get().getApiClient().getAlerts().destroyFriendRequest(existingFriend.get());

        LocalizedFormat message = Messages.GENERIC_REMOVED_FRIEND;
        if (!existingFriend.get().isAccepted()) {
          message = Messages.GENERIC_CANCELLED_FRIEND;
        }
        sender.sendMessage(message.with(ChatColor.GOLD, friend.getName()));
      }
    }.nowAsync();
  }
}
