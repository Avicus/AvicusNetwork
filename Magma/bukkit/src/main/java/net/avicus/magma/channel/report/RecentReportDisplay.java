package net.avicus.magma.channel.report;

import java.util.List;
import java.util.Optional;
import net.avicus.compendium.Paginator;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.commands.exception.InvalidPaginationPageException;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizableFormat;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.compendium.locale.text.LocalizedTime;
import net.avicus.compendium.locale.text.UnlocalizedFormat;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.compendium.utils.Strings;
import net.avicus.magma.database.Database;
import net.avicus.magma.database.model.impl.Report;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.network.user.Users;
import net.avicus.magma.util.MagmaTranslations;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
final class RecentReportDisplay extends BukkitRunnable {

  private static final LocalizableFormat HEADER_FORMAT = new UnlocalizedFormat("{0} §6 ({1}/{2})");
  private static final LocalizableFormat REPORT_FORMAT = new UnlocalizedFormat(
      "§r{0}§c » §r{1}§c §a({2}§a)§c » {3} §1({4}§1)");
  private static final int REPORTS_PER_PAGE = 5;
  private final Database database;
  private final CommandSender source;
  private final int page;
  private final Optional<Server> server;
  private final Optional<User> user;

  RecentReportDisplay(Database database, CommandSender source, int page, Optional<Server> server,
      Optional<User> user) {
    this.database = database;
    this.source = source;
    this.page = page;
    this.server = server;
    this.user = user;
  }

  @Override
  public void run() {
    final List<Report> reports = this.database.getReports()
        .getRecentReports(this.server, this.user);

    final Paginator<Report> paginator = new Paginator<>(reports, REPORTS_PER_PAGE);

    if (paginator.getPageCount() == 0) {
      this.source.sendMessage(MagmaTranslations.REPORT_RECENT_NONE.with(ChatColor.RED));
      return;
    }

    if (!paginator.hasPage(this.page)) {
      this.source.sendMessage(
          InvalidPaginationPageException.format(new InvalidPaginationPageException(paginator)));
      return;
    }

    this.source.sendMessage(Strings.padChatComponent(HEADER_FORMAT
            .with(this.getTitle(), new LocalizedNumber(this.page + 1),
                new LocalizedNumber(paginator.getPageCount())).translate(this.source), "-",
        ChatColor.YELLOW, ChatColor.AQUA));

    paginator.getPage(this.page).forEach(report -> {
      final User reporter = this.database.getUsers().findById(report.getCreatorId())
          .orElse(User.CONSOLE);
      final User reported = this.database.getUsers().findById(report.getReportedId()).orElse(null);
      if (reported == null) {
        return;
      }

      this.source.sendMessage(REPORT_FORMAT.with(
          Users.getLocalizedDisplay(reporter),
          Users.getLocalizedDisplay(reported),
          new UnlocalizedText(report.getServer(), ChatColor.DARK_AQUA),
          new UnlocalizedText(report.getReason(), ChatColor.DARK_GREEN),
          new LocalizedTime(report.getCreatedAt(), TextStyle.create().color(ChatColor.BLUE))
      ));
    });
  }

  @SuppressWarnings("OptionalIsPresent")
  private Localizable getTitle() {
    if (this.user.isPresent()) {
      return MagmaTranslations.REPORT_RECENT_TITLE_PLAYER
          .with(ChatColor.YELLOW, new UnlocalizedText(this.user.get().getName()));
    }
    if (this.server.isPresent()) {
      return MagmaTranslations.REPORT_RECENT_TITLE_SERVER
          .with(ChatColor.YELLOW, new UnlocalizedText(this.server.get().getName()));
    }
    return MagmaTranslations.REPORT_RECENT_TITLE_GLOBAL.with(ChatColor.YELLOW);
  }
}
