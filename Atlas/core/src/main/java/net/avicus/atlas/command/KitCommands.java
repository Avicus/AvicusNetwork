package net.avicus.atlas.command;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import java.util.List;
import java.util.Optional;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.command.exception.CommandMatchException;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.kits.Kit;
import net.avicus.atlas.module.kits.KitsModule;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.commands.exception.MustBePlayerCommandException;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.UnlocalizedFormat;
import net.avicus.compendium.utils.Strings;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitCommands {

  private static Optional<KitsModule> getModuleFromSender(CommandSender sender)
      throws CommandMatchException {
    Match match = Atlas.getMatch();

    if (match == null) {
      throw new CommandMatchException();
    }

    if (!match.hasModule(KitsModule.class)) {
      sender.sendMessage(Messages.ERROR_KITS_NOT_ENABLED.with(ChatColor.RED));
    }

    return match.getModule(KitsModule.class);
  }

  @Command(aliases = {"kits"}, desc = "List available kits in this match.", min = 0, max = 0)
  public static void kits(CommandContext cmd, CommandSender sender) throws Exception {
    KitsModule kits = getModuleFromSender(sender).orElse(null);

    if (kits == null) {
      return;
    }

    if (sender instanceof Player) {
      if (!kits.isEnabled((Player) sender)) {
        sender.sendMessage(Messages.ERROR_KITS_NOT_ENABLED_PLAYER.with(ChatColor.RED));
      }
    }

    sender.sendMessage(Strings
        .padChatComponent(Messages.UI_KITS.with(ChatColor.GOLD).translate(sender.getLocale()), "-",
            ChatColor.BLUE, ChatColor.AQUA));

    List<Kit> kitList = kits.getKits();
    if (sender instanceof Player) {
      kitList = kits.getKits((Player) sender);
    }

    for (Kit kit : kitList) {
      // Name
      Localizable name = kit.getName().toText(ChatColor.BLUE);

      Localizable localized;

      if (kit.getDescription().isPresent()) {
        Localizable desc = kit.getDescription().get().toText();
        desc.style().color(ChatColor.GRAY).italic();
        localized = new UnlocalizedFormat("{0}: {1}").with(name, desc);
      } else {
        localized = new UnlocalizedFormat("{0}").with(name);
      }

      sender.sendMessage(localized);
    }
  }

  @Command(aliases = {
      "kit"}, desc = "Choose a kit or display current kit.", usage = "<kit>", min = 0, max = -1)
  public static void kit(CommandContext cmd, CommandSender sender) throws Exception {
    MustBePlayerCommandException.ensurePlayer(sender);

    KitsModule kits = getModuleFromSender(sender).orElse(null);

    if (kits == null) {
      return;
    }

    Player player = (Player) sender;

    if (cmd.argsLength() == 0) {
      Kit currentKit = kits.getCurrentKit(player).orElse(null);

      if (currentKit == null) {
        sender.sendMessage(Messages.ERROR_NO_KIT.with(ChatColor.AQUA));
        return;
      }

      Localizable name = currentKit.getName().toText();
      name.style().color(ChatColor.GOLD).bold();
      sender.sendMessage(Messages.UI_CURRENT_KIT.with(ChatColor.YELLOW, name));
    } else {
      List<Kit> search = kits.search(sender, cmd.getJoinedStrings(0));

      if (search.isEmpty()) {
        sender.sendMessage(Messages.ERROR_KIT_NOT_FOUND.with(ChatColor.RED));
        return;
      }

      Kit result = search.get(0);

      // Set player's next kit
      kits.setUpcomingKit(player, result);

      result.displaySelectedMessage(player);
    }
  }
}
