package net.avicus.hook.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import java.util.Date;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.commands.exception.MustBePlayerCommandException;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizableFormat;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.compendium.locale.text.LocalizedTime;
import net.avicus.compendium.locale.text.UnlocalizedFormat;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.hook.Hook;
import net.avicus.hook.utils.Commands;
import net.avicus.hook.utils.HookTask;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.network.user.Users;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Period;

public class StatsCommand {

  @Command(aliases = {"stats",
      "kd"}, desc = "View your PvP statistics.", min = 1, max = 1, usage = "<time in 1m/1h/1d format>")
  public static void stats(CommandContext cmd, CommandSender sender)
      throws MustBePlayerCommandException {
    MustBePlayerCommandException.ensurePlayer(sender);

    Player player = (Player) sender;
    User user = Users.user(player);
    Period period;

    try {
      period = Commands.parsePeriod(cmd.getString(0));
    } catch (Exception e) {
      Localizable details = new UnlocalizedText("Time must be in 1m/1h/1d format.");
      player.sendMessage(Messages.ERROR_ERROR_OCCURRED.with(details));
      return;
    }

    DateTime now = DateTime.now();
    now.minus(period);

    Date from = now.minus(period).toDate();
    Date to = now.toDate();

    long millisAgo = to.getTime() - from.getTime();
    long oneDay = Days.ONE.toStandardDuration().getMillis();

    if (millisAgo > oneDay && !player.hasPermission("hook.stats.history")) {
      player.sendMessage(Messages.ERROR_NO_PERMISSION_STATS_HISTORY.with(ChatColor.RED));
      return;
    }

    HookTask.of(() -> {
      int kills = Hook.database().getDeaths().kills(user.getId(), from, to);
      int deaths = Hook.database().getDeaths().deaths(user.getId(), from, to);

      double kd = (double) kills / (double) (deaths == 0 ? 1 : deaths);

      TextStyle numberColor = TextStyle.ofColor(ChatColor.RED).bold();

      Localizable killsText = Messages.UI_KILLS
          .with(ChatColor.WHITE, new LocalizedNumber(kills, numberColor));
      Localizable deathsText = Messages.UI_DEATHS
          .with(ChatColor.WHITE, new LocalizedNumber(deaths, numberColor));
      Localizable kdText = Messages.UI_KD
          .with(ChatColor.WHITE, new LocalizedNumber(kd, 2, 2, numberColor));

      LocalizableFormat format = new UnlocalizedFormat("{0} / {1} / {2}");
      Localizable numbers = format.with(ChatColor.GOLD, killsText, deathsText, kdText);

      Localizable since = new LocalizedTime(from);
      sender.sendMessage(Messages.GENERIC_STATS_SINCE.with(ChatColor.GOLD, since, numbers));
    }).nowAsync();
  }
}
