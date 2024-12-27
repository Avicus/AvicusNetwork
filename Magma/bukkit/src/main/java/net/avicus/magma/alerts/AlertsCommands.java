package net.avicus.magma.alerts;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import java.util.Collections;
import java.util.List;
import net.avicus.compendium.Paginator;
import net.avicus.compendium.commands.exception.InvalidPaginationPageException;
import net.avicus.compendium.commands.exception.MustBePlayerCommandException;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizableFormat;
import net.avicus.compendium.locale.text.LocalizedDate;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.compendium.locale.text.UnlocalizedFormat;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.compendium.utils.Strings;
import net.avicus.magma.Magma;
import net.avicus.magma.NetworkIdentification;
import net.avicus.magma.api.graph.types.alert.Alert;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.network.user.Users;
import net.avicus.magma.util.MagmaTask;
import net.avicus.magma.util.MagmaTranslations;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AlertsCommands {

  @Command(aliases = "alerts", desc = "View your alerts.", usage = "(page)", min = 0, max = 1, flags = "dr")
  public static void alerts(CommandContext cmd, CommandSender sender) throws CommandException {
    MustBePlayerCommandException.ensurePlayer(sender);

    boolean unread = !cmd.hasFlag('r');

    Player player = (Player) sender;
    User user = Users.user(player);
    List<Alert> alerts = Alerts.get(user, unread);
    Collections.reverse(alerts);

    new MagmaTask() {
      @Override
      public void run() throws Exception {
        // Basically, backend
        if (cmd.hasFlag('d')) {
          if (cmd.argsLength() != 1) {
            return;
          }

          int id = cmd.getInteger(0);

          List<Alert> all = Alerts.get(user, false);
          for (Alert alert : all) {
            if (id == alert.getId()) {
              if (Magma.get().getApiClient().getAlerts().delete(alert)) {
                sender.sendMessage(MagmaTranslations.GENERIC_DELETED_ALERT.with(ChatColor.GOLD));
                Alerts.reload(user);
              } else {
                sender.sendMessage(MagmaTranslations.ERROR_API_CONNECT.with(ChatColor.RED));
              }
              break;
            }
          }
          return;
        }

        if (alerts.isEmpty()) {
          LocalizableFormat error = MagmaTranslations.ERROR_NO_UNREAD_ALERTS;
          if (!unread) {
            error = MagmaTranslations.ERROR_NO_ALERTS;
          }
          sender.sendMessage(error.with(ChatColor.RED));
          return;
        }

        Paginator<Alert> paginator = new Paginator<>(alerts, 5);
        int page = cmd.argsLength() == 0 ? 0 : cmd.getInteger(0) - 1;

        if (!paginator.hasPage(page)) {
          sender.sendMessage(
              InvalidPaginationPageException.format(new InvalidPaginationPageException(paginator)));
          return;
        }

        // Header
        Localizable title = MagmaTranslations.GUI_ALERTS.with(ChatColor.YELLOW);
        Localizable pageText = new LocalizedNumber(page + 1);
        Localizable pageCount = new LocalizedNumber(paginator.getPageCount());
        LocalizableFormat header = new UnlocalizedFormat("{0} ({1}/{2})");
        sender.sendMessage(Strings
            .padChatComponent(header.with(title, pageText, pageCount).render(sender),
                "-", ChatColor.YELLOW, ChatColor.AQUA));

        for (Alert alert : paginator.getPage(page)) {
          LocalizableFormat format = new UnlocalizedFormat("{0}");
          Localizable message = format
              .with(ChatColor.GRAY, new UnlocalizedText(alert.getMessage(), ChatColor.WHITE));

          LocalizableFormat subFormat = new UnlocalizedFormat("  {0} ({1} / {2})");
          Localizable date = new LocalizedDate(alert.getCreatedAt().toDate());
          Localizable link = MagmaTranslations.GUI_OPEN.with(ChatColor.AQUA);
          link.style()
              .click(new ClickEvent(Action.OPEN_URL,
                  NetworkIdentification.URL + "/" + alert.getUrl()));
          Localizable delete = MagmaTranslations.GUI_DELETE.with(ChatColor.RED);
          delete.style().click(new ClickEvent(Action.RUN_COMMAND, "/alerts -d " + alert.getId()));
          Localizable sub = subFormat.with(ChatColor.GRAY, date, link, delete);

          sender.sendMessage(message);
          sender.sendMessage(sub);
        }
      }
    }.nowAsync();
  }
}
