package net.avicus.atlas.command;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.command.exception.CommandMatchException;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.registry.RegisteredObject;
import net.avicus.compendium.commands.exception.MustBePlayerCommandException;
import net.avicus.magma.util.region.shapes.CuboidRegion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;


public class WorldEditCommands {

  @Command(aliases = {"register-region",
      "rr"}, desc = "Register current WE selection in match registry.", usage = "<id>", min = 1)
  @CommandPermissions("atlas.register.region")
  public static void registerRegion(CommandContext cmd, CommandSender sender)
      throws CommandException {
    Match match = Atlas.getMatch();
    if (match == null) {
      throw new CommandMatchException();
    }

    String id = cmd.getJoinedStrings(0);

    MustBePlayerCommandException.ensurePlayer(sender);

    Player player = (Player) sender;

    WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager()
        .getPlugin("WorldEdit");
    Selection selection = worldEdit.getSelection(player);

    if (selection != null) {
      Vector min = selection.getMinimumPoint().toVector();
      Vector max = selection.getMaximumPoint().toVector();

      CuboidRegion region = new CuboidRegion(min, max);

      match.getRegistry().add(new RegisteredObject<>(id, region));
      sender.sendMessage("Registered region with ID " + id);
    } else {
      // TODO: Translate
      sender.sendMessage(ChatColor.RED + "You must make a selection first!");
    }
  }
}
