package net.avicus.magma.channel.report;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandNumberFormatException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import java.util.Optional;
import net.avicus.compendium.commands.exception.MustBePlayerCommandException;
import net.avicus.compendium.commands.exception.TranslatableCommandErrorException;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.compendium.settings.PlayerSettings;
import net.avicus.magma.Magma;
import net.avicus.magma.database.Database;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.module.CommandModule;
import net.avicus.magma.util.MagmaTranslations;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Reports implements CommandModule {

  private final ReportChannel channel = new ReportChannel();

  private static ReportChannel getChannel() {
    return Magma.get().getChannelManager().getChannel(ReportChannel.ID, ReportChannel.class);
  }

  @Command(aliases = {"report"}, desc = "Report a player.", usage = "<player> <reason...>", min = 2)
  public static void report(final CommandContext context, final CommandSender sender)
      throws TranslatableCommandErrorException {
    final Player source = MustBePlayerCommandException.ensurePlayer(sender);
    final Player target = Bukkit.getPlayer(context.getString(0));
    if (target == null) {
      sender.sendMessage(MagmaTranslations.ERROR_UNKNOWN_PLAYER
          .with(ChatColor.RED, new UnlocalizedText(context.getString(0))));
      return;
    }

    getChannel().report(source, target, context.getJoinedStrings(1));
    sender.sendMessage(MagmaTranslations.REPORT_SENT.with(ChatColor.YELLOW));
  }

  @Command(aliases = {"reports",
      "reportlist"}, desc = "View recent reports", usage = "-s <server> | -p <player>", min = 0, max = 2, flags = "s:p:")
  @CommandPermissions(value = "hook.reports")
  public static void reports(CommandContext ctx, CommandSender sender)
      throws CommandNumberFormatException {
    final Magma magma = Magma.get();
    final Database database = magma.database();

    Optional<Server> server = Optional.empty();
    if (ctx.hasFlag('s')) {
      server = database.getServers().findByName(ctx.getFlag('s', magma.localServer().getName()));
      if (!server.isPresent()) {
        sender.sendMessage(MagmaTranslations.COMMANDS_SERVER_QUERY_NONE
            .with(ChatColor.RED, new UnlocalizedText(ctx.getFlag('s'))));
        return;
      }
    }

    Optional<User> user = Optional.empty();
    if (ctx.hasFlag('p')) {
      user = database.getUsers().findByName(ctx.getFlag('p'));
      if (!user.isPresent()) {
        sender.sendMessage(MagmaTranslations.ERROR_UNKNOWN_PLAYER
            .with(ChatColor.RED, new UnlocalizedText(ctx.getFlag('p'))));
        return;
      }
    }

    final int page = ctx.argsLength() == 0 ? 0 : ctx.getInteger(0) - 1;
    new RecentReportDisplay(database, sender, page, server, user).runTaskAsynchronously(magma);
  }

  @Override
  public void enable() {
    PlayerSettings.register(ReportChannel.REPORT_NOTIFICATION_SETTING);
    Magma.get().getChannelManager().register(this.channel);
  }

  @Override
  public void registerCommands(CommandsManagerRegistration registrar) {
    registrar.register(Reports.class);
  }
}
