package net.avicus.atlas.command;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.command.exception.CommandMatchException;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.registry.WeakReference;
import net.avicus.atlas.module.loadouts.Loadout;
import net.avicus.atlas.module.loadouts.LoadoutsFactory;
import net.avicus.atlas.module.projectiles.CustomProjectile;
import net.avicus.atlas.module.projectiles.ProjectilesModule;
import net.avicus.atlas.runtimeconfig.ConfigurableWrapper;
import net.avicus.compendium.inventory.SingleMaterialMatcher;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;
import org.joda.time.Duration;

public class RuntimeConfigurableCommands {

  @Command(aliases = {"list", "l"}, desc = "List all runtime configurables", max = 0)
  @CommandPermissions("atlas.rt.list")
  public static void list(CommandContext args, CommandSender sender)
      throws CommandException {
    Match match = Atlas.getMatch();

    if (match == null) {
      throw new CommandMatchException();
    }

    match.getConfigurablesManager().describeAll(sender);
  }

  @Command(aliases = {"addprod", "ap"}, desc = "Add a default projectile to an item", min = 1)
  @CommandPermissions("atlas.rt.list")
  public static void add(CommandContext args, CommandSender sender)
      throws CommandException {
    Match match = Atlas.getMatch();

    if (match == null) {
      throw new CommandMatchException();
    }

    Material material = Material.valueOf(args.getJoinedStrings(0).replaceAll(" ", "_").toUpperCase());

    CustomProjectile projectile = new CustomProjectile(UUID.randomUUID(), material.name() + " default projectile", true, EntityType.ARROW, 0.0, 1.0, Optional.empty(),
        Optional.empty(), false, false, Optional.empty(), new ArrayList<>());
    match.getRequiredModule(ProjectilesModule.class).registerProjectile(projectile);
    match.getRequiredModule(ProjectilesModule.class).getDefaultProjectiles().put(material, projectile);
    match.reRegisterConfigurables();
  }

  @Command(aliases = {"view", "v"}, desc = "View a configurable by ID", usage = "<id>", min = 1, max = 1)
  @CommandPermissions("atlas.rt.view")
  public static void view(CommandContext args, CommandSender sender)
      throws CommandException {
    Match match = Atlas.getMatch();

    if (match == null) {
      throw new CommandMatchException();
    }

    ConfigurableWrapper wrapper = match.getConfigurablesManager().getWrapper(args.getString(0));
    match.getConfigurablesManager().describe(sender, wrapper, true);
  }

  @Command(aliases = {"config", "c", "configure"}, desc = "Config a configurable", usage = "<id> <field> <data...>", min = 3)
  @CommandPermissions("atlas.rt.modify")
  public static void modify(CommandContext args, CommandSender sender)
      throws CommandException {
    Match match = Atlas.getMatch();

    if (match == null) {
      throw new CommandMatchException();
    }

    ConfigurableWrapper wrapper = match.getConfigurablesManager().getWrapper(args.getString(0));
    String edited = wrapper.configure(args.getString(1), args.getParsedSlice(2));
    sender.sendMessage(ChatColor.GREEN + "Object reconfigured!");
    Atlas.get().getMapErrorLogger().info(sender.getName() + ChatColor.RESET + " updated " + edited + ChatColor.RESET + " of " + wrapper.getConfigurable().getDescription(sender));
  }

  public static class ParentCommand {

    @Command(aliases = "rt", usage = "<>", desc = ".", min = 1)
    @NestedCommand(RuntimeConfigurableCommands.class)
    public static void loadout(CommandContext cmd, CommandSender sender) {
      // Never called
    }
  }
}