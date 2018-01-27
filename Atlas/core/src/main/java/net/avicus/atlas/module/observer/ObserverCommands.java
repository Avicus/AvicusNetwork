package net.avicus.atlas.module.observer;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import net.avicus.atlas.module.observer.menu.ObserverMenu;
import net.avicus.atlas.util.Translations;
import net.avicus.compendium.commands.exception.MustBePlayerCommandException;
import net.avicus.compendium.commands.exception.TranslatableCommandErrorException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ObserverCommands {

  @Command(aliases = {"obs"}, desc = "Observer options", min = 0, max = 0)
  public static void options(final CommandContext args, final CommandSender source)
      throws TranslatableCommandErrorException {
    MustBePlayerCommandException.ensurePlayer(source);

    if (!ObserverMenu.canOpen((Player) source)) {
      throw new TranslatableCommandErrorException(
          Translations.MODULE_OBSERVER_MENU_ERROR_OPEN_PARTICIPANT);
    }

    ObserverMenu.create((Player) source).open();
  }
}
