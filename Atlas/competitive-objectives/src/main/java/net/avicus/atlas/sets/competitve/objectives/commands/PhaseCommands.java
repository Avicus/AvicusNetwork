package net.avicus.atlas.sets.competitve.objectives.commands;

import com.google.common.collect.Lists;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.CommandUsageException;
import com.sk89q.minecraft.util.commands.NestedCommand;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.command.exception.CommandMatchException;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.registry.MatchRegistry;
import net.avicus.atlas.module.locales.LocalesModule;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.module.objectives.ObjectivesModule;
import net.avicus.atlas.module.states.StatesModule;
import net.avicus.atlas.sets.competitve.objectives.bridges.ObjectivesBridge;
import net.avicus.atlas.sets.competitve.objectives.destroyable.DestroyableObjective;
import net.avicus.atlas.sets.competitve.objectives.phases.DestroyablePhase;
import net.avicus.atlas.sets.competitve.objectives.phases.PhaseApplyCountdown;
import net.avicus.compendium.StringUtil;
import net.avicus.compendium.commands.exception.MustBePlayerCommandException;
import net.avicus.compendium.commands.exception.TranslatableCommandErrorException;
import net.avicus.compendium.commands.exception.TranslatableCommandWarningException;
import net.avicus.compendium.countdown.Countdown;
import net.avicus.compendium.countdown.CountdownManager;
import net.avicus.compendium.countdown.CountdownTask;
import net.avicus.compendium.inventory.MultiMaterialMatcher;
import net.avicus.compendium.inventory.SingleMaterialMatcher;
import net.avicus.compendium.locale.text.UnlocalizedFormat;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.compendium.plugin.CompendiumPlugin;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.joda.time.Duration;

public class PhaseCommands {

  private static final String LINE = ChatColor.GOLD + ChatColor.STRIKETHROUGH.toString() + "------" + ChatColor.RESET;
  private static final String INDENT = "  ";

  private static Match getMatch(CommandSender sender) throws CommandException {
    MustBePlayerCommandException.ensurePlayer(sender);

    Match match = Atlas.getMatch();

    if (match == null) {
      throw new CommandMatchException();
    }

    return match;
  }

  private static ObjectivesBridge getBridge(CommandSender sender) throws CommandException {
    Match match = getMatch(sender);

    if (!match.hasModule(ObjectivesModule.class)) {
      throw new TranslatableCommandErrorException(new UnlocalizedFormat("Match has no objectives"));
    }

    return match.getRequiredModule(ObjectivesModule.class).getBridge(ObjectivesBridge.class);
  }

  @Command(aliases = "list", desc = "List all registered phases", max = 0)
  public static void list(CommandContext args, CommandSender sender)
      throws CommandException {
    List<BaseComponent> rows = Lists.newArrayList();
    Map<DestroyablePhase, List<DestroyableObjective>> phases = getBridge(sender).getPhases();
    if (phases.keySet().isEmpty()) throw new TranslatableCommandWarningException(new UnlocalizedFormat("This map doesn't have any registered phases"));

    rows.add(new TextComponent(LINE + ChatColor.YELLOW + " Destroyable Phases " + LINE));
    phases.forEach((phase, obs) -> {
      rows.addAll(describePhase("", phase, sender));
      rows.add(new TextComponent(INDENT + ChatColor.GOLD + "Applies to: " + StringUtil
          .listToEnglishCompound(obs.stream().map(o -> o.getName(sender)).collect(Collectors.toList()))));
    });

    rows.forEach(sender::sendMessage);
  }

  private static List<BaseComponent> describePhase(String prefix, DestroyablePhase phase, CommandSender viewer) {
    List<BaseComponent> rows = Lists.newArrayList();

    rows.add(new UnlocalizedFormat(prefix + "{0} ({1})").with(phase.getName().toText(), new UnlocalizedText(phase.getId())).translate(viewer));
    rows.add(new TextComponent(prefix + INDENT + ChatColor.YELLOW + StringUtil.listToEnglishCompound(phase.describeReplacementStrategy())));
    rows.add(new TextComponent(prefix + INDENT + ChatColor.BLUE + "Applied After: " + ChatColor.AQUA + StringUtil.secondsToClock((int) phase.getDelay().getStandardSeconds())));
    if (phase.getPassPhase().isPresent()) {
      rows.add(new TextComponent(prefix + INDENT + ChatColor.GREEN + "Phase Applied if Application Check Passes:"));
      rows.addAll(describePhase(prefix + INDENT + INDENT, phase.getPassPhase().get(), viewer));
    }
    if (phase.getFailPhase().isPresent()) {
      rows.add(new TextComponent(prefix + INDENT + ChatColor.RED + "Phase Applied if Application Check Fails:"));
      rows.addAll(describePhase(prefix + INDENT + INDENT, phase.getFailPhase().get(), viewer));
    }
    return rows;
  }

  private static DestroyablePhase getPhase(CommandSender sender, String id) throws CommandException {
    Match match = getMatch(sender);
    MatchRegistry registry = match.getRegistry();

    if (!registry.has(id)) throw new TranslatableCommandErrorException(new UnlocalizedFormat("Object with ID \"" + id + "\" not found."));

    if (!registry.isOfType(id, DestroyablePhase.class)) throw new TranslatableCommandErrorException(new UnlocalizedFormat("Object with ID \"" + id + "\" is not a destroyable phase."));

    return registry.get(DestroyablePhase.class, id, true).get();
  }

  @Command(aliases = "remove", desc = "Remove a phase by ID", max = 1, min = 1, usage = "<id>")
  @CommandPermissions("atlas.phases.manage")
  public static void remove(CommandContext args, CommandSender sender) throws CommandException {
    String id = args.getString(0);

    DestroyablePhase found = getPhase(sender, id);
    CompendiumPlugin.getInstance().getCountdownManager().cancelAll(c -> c instanceof PhaseApplyCountdown && ((PhaseApplyCountdown) c).getPhase().equals(found));

    ObjectivesBridge bridge = getBridge(sender);
    bridge.getPhases().remove(found);
    getMatch(sender).getRegistry().getObjects().remove(id);
    for (DestroyablePhase phase : bridge.getPhases().keySet()) {
      phase.removePhase(found);
    }

    for (DestroyableObjective objective : bridge.getObjectivesWithPhases()) {
      if (objective.getPhase().get().equals(found)) objective.setPhase(Optional.empty());
    }

    bridge.populatePhaseCache();

    sender.sendMessage(ChatColor.GREEN + "Phase removed");
  }

  @Command(aliases = "modtime", desc = "Modify a phase's application time", max = 2, min = 2, usage = "<id> <time>")
  @CommandPermissions("atlas.phases.manage")
  public static void modtime(CommandContext args, CommandSender sender) throws CommandException {
    String id = args.getString(0);

    DestroyablePhase found = getPhase(sender, id);
    Duration delay = net.avicus.magma.util.StringUtil.parsePeriod(args.getString(1)).toStandardDuration();
    found.setDelay(delay);
    for (Entry<Countdown, CountdownTask> entry : CompendiumPlugin
        .getInstance().getCountdownManager().getCountdowns().entrySet()) {
      if (!(entry.getKey() instanceof PhaseApplyCountdown)) continue;
      if (!((PhaseApplyCountdown) entry.getKey()).getPhase().equals(found)) continue;

      entry.getValue().setNewDuration(delay);
    }
    sender.sendMessage(
        ChatColor.GREEN + "Set delay of " +
            ChatColor.GOLD + id +
            ChatColor.GREEN + " to " +
            ChatColor.AQUA + StringUtil.secondsToClock((int) delay.getStandardSeconds())
    );
  }

  private static final String ADD_ARGS = "id|name|success message|delay|find|replace";

  @Command(aliases = "add", desc = "Add a phase", usage = ADD_ARGS)
  @CommandPermissions("atlas.phases.manage")
  public static void add(CommandContext args, CommandSender sender) throws CommandException {
    Match match = getMatch(sender);
    ObjectivesBridge bridge = getBridge(sender);

    String[] indArgs = args.getJoinedStrings(0).split("\\|");
    if (indArgs.length != 6) throw new CommandUsageException("Wrong number of arguments", "/phases add " + ADD_ARGS);

    try {
      String id = indArgs[0];
      String nameRaw = indArgs[1];
      String successRaw = indArgs[2];
      Duration delay = net.avicus.magma.util.StringUtil.parsePeriod(indArgs[3]).toStandardDuration();
      String countdownRaw = "{phases.transition}";
      String failRaw = "{phases.monument.fail}";
      LocalizedXmlString name = match.getRequiredModule(LocalesModule.class).parse(nameRaw);
      LocalizedXmlString countdown = match.getRequiredModule(LocalesModule.class).parse(countdownRaw);
      LocalizedXmlString success = match.getRequiredModule(LocalesModule.class).parse(successRaw);
      LocalizedXmlString fail = match.getRequiredModule(LocalesModule.class).parse(failRaw);

      LinkedHashMap<MultiMaterialMatcher, SingleMaterialMatcher> materials = new LinkedHashMap<>();
      materials.put(parseMultiMatcher(indArgs[4]), parseSingleMatcher(indArgs[5]));

      DestroyablePhase phase = new DestroyablePhase(match, id, name, countdown, success, fail, materials, delay, Optional.empty(), 0,
          Optional.empty(), Optional.empty(), Optional.empty());
      match.getRegistry().add(phase);
      CountdownManager manager = CompendiumPlugin.getInstance().getCountdownManager();
      if (manager.isRunning(PhaseApplyCountdown.class)) {
        manager.getCountdowns().keySet().stream().filter(c -> c instanceof PhaseApplyCountdown).forEach(c -> {
          ((PhaseApplyCountdown)c).getPhase().addPhase(phase);
        });
        sender.sendMessage(ChatColor.GREEN + "Phase added to end of phase chain");
      } else {
        for (DestroyableObjective objective : bridge.getLeakables()) {
          objective.setPhase(Optional.of(phase));
        }
        for (DestroyableObjective objective : bridge.getMonuments()) {
          objective.setPhase(Optional.of(phase));
        }
        bridge.populatePhaseCache();
        if (match.getRequiredModule(StatesModule.class).isPlaying())
          bridge.startPhaseCountdowns(match);
        sender.sendMessage(ChatColor.GREEN + "Phase added");
      }
    } catch (Exception e) {
      // e.printStackTrace();
      throw new TranslatableCommandErrorException(new UnlocalizedFormat("Failed to add phase: " + e.getMessage()));
    }
  }

  private static MultiMaterialMatcher parseMultiMatcher(String raw) {
    List<SingleMaterialMatcher> matchers = Lists.newArrayList();
    for (String s : raw.split(",")) {
      matchers.add(parseSingleMatcher(s));
    }
    return new MultiMaterialMatcher(matchers);
  }

  private static SingleMaterialMatcher parseSingleMatcher(String raw) {
    String[] parts = raw.split(":");
    Material material = Enum.valueOf(Material.class, parts[0].toUpperCase().replace(" ", "_").replace("-", "_"));
    Optional<Byte> data = Optional.empty();
    if (parts.length > 1) data = Optional.of(Byte.valueOf(parts[1]));

    if (!material.isSolid()) throw new IllegalArgumentException("Materials must be solid");

    return new SingleMaterialMatcher(material, data);
  }

  public static class Super {
    @Command(aliases = "phase", usage = "<>", desc = ".", min = 1)
    @NestedCommand(PhaseCommands.class)
    public static void phase(CommandContext cmd, CommandSender sender) {
      // Never called
    }
  }
}
