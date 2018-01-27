package net.avicus.hook.commands;

import com.google.common.base.Joiner;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.network.user.Users;
import net.avicus.magma.network.user.rank.BukkitRank;
import net.avicus.magma.network.user.rank.Ranks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OnlineCommand {

  @Command(aliases = {"list", "online", "who"}, desc = "Show online users.", max = 0)
  public static void online(CommandContext cmd, CommandSender sender) {
    List<Player> players = new ArrayList<>();
    players.addAll(Bukkit.getOnlinePlayers());

    Collections.sort(players, (p1, p2) -> {
      User u1 = Users.user(p1);
      User u2 = Users.user(p2);
      BukkitRank r1 = Ranks.getHighest(u1).orElse(null);
      BukkitRank r2 = Ranks.getHighest(u2).orElse(null);

      int priority1 = r1 == null ? 0 : r1.getRank().getPriority();
      int priority2 = r2 == null ? 0 : r2.getRank().getPriority();

      int priorityCompare = -Integer.compare(priority1, priority2);

      if (priorityCompare != 0) {
        return priorityCompare;
      }

      return p1.getName().compareTo(p2.getName());
    });

    List<String> usernames = new ArrayList<>();
    for (Player player : players) {
      User user = Users.user(player);
      usernames.add(Users.getDisplay(user));
    }

    String counts = MessageFormat.format("({0} {1} {2}" + ChatColor.GRAY + ")",
        ChatColor.GREEN.toString() + players.size(),
        ChatColor.DARK_RED + "/",
        ChatColor.AQUA.toString() + Bukkit.getMaxPlayers());

    UnlocalizedText list = new UnlocalizedText(Joiner.on(ChatColor.WHITE + ", ").join(usernames));

    sender.sendMessage(
        Messages.UI_ONLINE_USERS.with(ChatColor.GRAY, new UnlocalizedText(counts), list));
  }
}
