package net.avicus.hook.credits;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.commands.exception.MustBePlayerCommandException;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.hook.utils.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreditCommand {

  @Command(aliases = {"credits", "c", "bal",
      "balance"}, desc = "Check your credit balance.", max = 0)
  public static void credits(CommandContext cmd, CommandSender sender)
      throws MustBePlayerCommandException {
    MustBePlayerCommandException.ensurePlayer(sender);

    Player player = (Player) sender;

    LocalizedNumber number = new LocalizedNumber(Credits.getCredits(player),
        TextStyle.ofColor(ChatColor.GOLD).bold());
    player.sendMessage(Messages.GENERIC_CREDITS.with(ChatColor.YELLOW, number));
  }
}
