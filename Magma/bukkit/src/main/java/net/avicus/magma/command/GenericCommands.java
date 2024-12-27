package net.avicus.magma.command;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import net.avicus.compendium.utils.Strings;
import net.avicus.magma.Magma;
import net.avicus.magma.database.model.impl.Rank;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.network.rtp.RTPHelpers;
import net.avicus.magma.network.server.ServerStatus;
import net.avicus.magma.network.server.Servers;
import net.avicus.magma.network.user.UserRankEntry;
import net.avicus.magma.text.Components;
import net.avicus.magma.util.MagmaTranslations;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class GenericCommands {

  @Command(aliases = {"staff", "stafflist"}, desc = "View a list of online staff", max = 0)
  public static void staffList(final CommandContext context, final CommandSender sender) {
    final class StaffList extends BukkitRunnable {

      @Override
      public void run() {
        final ListMultimap<Server, UserRankEntry> online = ArrayListMultimap.create();
        for (final ServerStatus status : Servers.getAllStatuses()) {
          final Server server = status.getServer();
          for (final User user : status.getPlayers()) {
            final Rank rank = user.getHighestRank(Magma.get().database());
            if (rank.isStaff()) {
              online.put(server, new UserRankEntry(user, rank));
            }
          }
        }
        sender.sendMessage(Strings.padChatComponent(
            MagmaTranslations.COMMANDS_STAFF_TITLE.with(ChatColor.GREEN)
                .render(sender), "-", ChatColor.YELLOW, ChatColor.AQUA));
        if (online.values().isEmpty()) {
          sender.sendMessage(MagmaTranslations.COMMANDS_STAFF_NONE.with(ChatColor.GRAY));
          return;
        }
        for (final Server server : online.keySet()) {
          final List<UserRankEntry> users = online.get(server);
          users.sort(UserRankEntry.COMPARATOR);
          final TextComponent component = new TextComponent();
          component.addExtra(RTPHelpers.clickableServer(server, Locale.US));
          component.addExtra(Components.simple(": ", ChatColor.WHITE));
          final Iterator<UserRankEntry> it = users.iterator();
          while (it.hasNext()) {
            component.addExtra(RTPHelpers
                .permissibleClickablePlayer(sender, server, it.next().user, Locale.US, true));
            if (it.hasNext()) {
              component.addExtra(Components.simple(", ", ChatColor.WHITE));
            }
          }
          sender.sendMessage(component);
        }
      }
    }
    new StaffList().runTaskAsynchronously(Magma.get());
  }
}
