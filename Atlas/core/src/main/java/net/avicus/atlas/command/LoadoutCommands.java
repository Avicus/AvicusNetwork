package net.avicus.atlas.command;

import com.google.common.collect.Lists;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import java.util.List;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.command.exception.CommandMatchException;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.registry.RegisteredObject;
import net.avicus.atlas.module.loadouts.Loadout;
import net.avicus.atlas.module.loadouts.modmenu.LoadoutModificationMenu;
import net.avicus.atlas.module.loadouts.type.ItemLoadout;
import net.avicus.atlas.module.loadouts.type.LoadoutNode;
import net.avicus.compendium.StringUtil;
import net.avicus.compendium.commands.exception.MustBePlayerCommandException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LoadoutCommands {

  @Command(aliases = {"mod", "modify"}, desc = "Modify an item loadout", usage = "<id>", max = 1)
  @CommandPermissions("atlas.loadout.modify")
  public static void mod(CommandContext args, CommandSender sender)
      throws CommandException {
    MustBePlayerCommandException.ensurePlayer(sender);

    Match match = Atlas.getMatch();

    if (match == null) {
      throw new CommandMatchException();
    }

    String id = args.getString(0);
    Loadout found = match.getRegistry().get(Loadout.class, id, false).orElseThrow(() -> new CommandException("Loadout not found for id \"" + id + " \""));
    ItemLoadout itemLoadout = null;
    if (found instanceof ItemLoadout)
      itemLoadout = (ItemLoadout) found;
    else if (found instanceof LoadoutNode)
      itemLoadout = (ItemLoadout) ((LoadoutNode) found).getLoadouts().stream().filter(l -> l instanceof ItemLoadout).findFirst().orElseThrow(() -> new CommandException("Loadout not found for id \"" + id + " \""));

    if (itemLoadout == null) {
      throw new CommandException("Loadout with id \"" + id + "\" is a " + found.getClass().getSimpleName() + " and cannot be edited.");
    }

    new LoadoutModificationMenu((Player) sender, itemLoadout, match, id).open();
  }

  @Command(aliases = "list", desc = "List all registered loadouts", max = 0)
  public static void list(CommandContext args, CommandSender sender)
      throws CommandException {
    MustBePlayerCommandException.ensurePlayer(sender);

    Match match = Atlas.getMatch();

    if (match == null) {
      throw new CommandMatchException();
    }

    List<String> ids = Lists.newArrayList();
    match.getRegistry().getObjects().forEach((id, obj) -> {
      if (obj.getObject() instanceof Loadout)
        ids.add(id);
    });

    sender.sendMessage(StringUtil.listToEnglishCompound(ids));
  }

  public static class ParentCommand {

    @Command(aliases = "loadout", usage = "<>", desc = ".", min = 1)
    @NestedCommand(LoadoutCommands.class)
    public static void loadout(CommandContext cmd, CommandSender sender) {
      // Never called
    }
  }
}