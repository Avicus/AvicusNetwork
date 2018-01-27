package net.avicus.magma.module.prestige;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.NestedCommand;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.commands.exception.MustBePlayerCommandException;
import net.avicus.compendium.commands.exception.TranslatableCommandErrorException;
import net.avicus.compendium.commands.exception.TranslatableCommandWarningException;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.compendium.locale.text.LocalizedTime;
import net.avicus.compendium.locale.text.UnlocalizedComponent;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.compendium.utils.Task;
import net.avicus.magma.Magma;
import net.avicus.magma.network.user.Users;
import net.avicus.magma.util.MagmaTask;
import net.avicus.magma.util.MagmaTranslations;
import net.avicus.magma.util.NMSUtils;
import net.avicus.magma.util.region.shapes.CuboidRegion;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;
import org.joda.time.Seconds;

public class PrestigeCommands {

  private static final String PRESTIGE_COMMAND = "prestige";

  private static final Random random = new Random();

  private static PrestigeModule module = Magma.get().getMm().get(PrestigeModule.class);

  private static LocalizedTime end = new LocalizedTime(Magma.get().getCurrentSeason().getEnd());

  @Command(aliases = {PRESTIGE_COMMAND, "rpresoits"}, desc = "Prestige")
  @NestedCommand(value = ChildrenCommands.class, executeBody = true)
  public static void prestige(final CommandContext ctx, final CommandSender source)
      throws CommandException {
    final Player player = MustBePlayerCommandException.ensurePlayer(source);

    int xp = module.getXP(player);

    PrestigeLevel current = PrestigeLevel.fromDB(Magma.get().database().getPrestigeLevels()
        .currentLevel(Users.user(player).getId(), Magma.get().getCurrentSeason()));
    PrestigeLevel next = current.next();

    if (current == PrestigeLevel.MAX) {
      throw new TranslatableCommandErrorException(MagmaTranslations.PRESTIGE_LEVELUP_MAX, end);
    }

    if (xp < next.getXp()) {
      int leftXp = next.getXp() - xp;
      int leftLevel = module.getXPLevelsNeeded(xp, next);
      throw new TranslatableCommandWarningException(MagmaTranslations.PRESTIGE_LEVELUP_FAIL,
          new LocalizedNumber(leftXp), new LocalizedNumber(leftLevel));
    }

    final BaseComponent cancel = MagmaTranslations.GENERIC_TYPE_CONFIRM_CANCEL_NAME
        .with(ChatColor.RED).translate(player);
    cancel.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
        MagmaTranslations.GENERIC_TYPE_CONFIRM_CANCEL_DESCRIPTION.with(ChatColor.RED).translate(
            player)
    }));
    cancel.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
        '/' + PRESTIGE_COMMAND + ' ' + ChildrenCommands.CANCEL_COMMAND));
    final BaseComponent confirm = MagmaTranslations.GENERIC_TYPE_CONFIRM_CONFIRM_NAME
        .with(ChatColor.GREEN).translate(player);
    confirm.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
        MagmaTranslations.GENERIC_TYPE_CONFIRM_CONFIRM_DESCRIPTION.with(ChatColor.GREEN).translate(
            player)
    }));
    confirm.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
        '/' + PRESTIGE_COMMAND + ' ' + ChildrenCommands.CONFIRM_COMMAND));
    player.sendMessage(MagmaTranslations.PRESTIGE_COMMANDS_PRESTIGE_CONFIRM
        .with(ChatColor.GRAY, new LocalizedNumber(next.getId()), new UnlocalizedComponent(confirm),
            new UnlocalizedComponent(cancel)).translate(player));
  }

  private static void spawnFireworks(Player player) {
    // FIREWORKS!
    World world = player.getWorld();
    Location location = player.getLocation();
    // Left
    spawnFirework(world, location.clone().add(0, 0, 4));
    // Right
    spawnFirework(world, location.clone().add(0, 0, -4));
    // Front
    spawnFirework(world, location.clone().add(4, 0, 0));
    // Back
    spawnFirework(world, location.clone().add(-4, 0, 0));
  }

  private static void spawnFirework(World world, Location location) {
    Firework firework = (Firework) world.spawnEntity(location, EntityType.FIREWORK);
    FireworkMeta meta = firework.getFireworkMeta();
    meta.addEffect(generateRandomEffect());
    firework.setFireworkMeta(meta);
  }

  private static FireworkEffect generateRandomEffect() {
    FireworkEffect.Builder builder = FireworkEffect.builder();

    builder
        .flicker(random.nextBoolean())
        .withColor(getColor(random.nextInt(17) + 1))
        .withFade(getColor(random.nextInt(17) + 1))
        .with(FireworkEffect.Type.values()[random.nextInt(FireworkEffect.Type.values().length)])
        .trail(random.nextBoolean());

    return builder.build();
  }

  private static Color getColor(final int i) {
    switch (i) {
      case 1:
        return Color.AQUA;
      case 2:
        return Color.BLACK;
      case 3:
        return Color.BLUE;
      case 4:
        return Color.FUCHSIA;
      case 5:
        return Color.GRAY;
      case 6:
        return Color.GREEN;
      case 7:
        return Color.LIME;
      case 8:
        return Color.MAROON;
      case 9:
        return Color.NAVY;
      case 10:
        return Color.OLIVE;
      case 11:
        return Color.ORANGE;
      case 12:
        return Color.PURPLE;
      case 13:
        return Color.RED;
      case 14:
        return Color.SILVER;
      case 15:
        return Color.TEAL;
      case 16:
        return Color.WHITE;
      case 17:
        return Color.YELLOW;
      default:
        return Color.BLUE;
    }
  }

  public static class ChildrenCommands {

    static final String CANCEL_COMMAND = "cancel";
    static final String CONFIRM_COMMAND = "confirm";

    @Command(aliases = CANCEL_COMMAND, desc = "Cancel a prestige advancement", max = 0)
    public static void cancel(final CommandContext ctx, final CommandSender source)
        throws MustBePlayerCommandException {
      final Player player = MustBePlayerCommandException.ensurePlayer(source);
      source.sendMessage(MagmaTranslations.PRESTIGE_LEVELUP_CANCELED.with(ChatColor.LIGHT_PURPLE));
    }

    @Command(aliases = CONFIRM_COMMAND, desc = "Confirm a prestige advancement", max = 0)
    public static void confirm(final CommandContext ctx, final CommandSender source)
        throws CommandException {
      final Player player = MustBePlayerCommandException.ensurePlayer(source);

      // This is copied from above to prevent players from just running the confirm command.

      int xp = module.getXP(player);

      PrestigeLevel current = PrestigeLevel.fromDB(Magma.get().database().getPrestigeLevels()
          .currentLevel(Users.user(player).getId(), Magma.get().getCurrentSeason()));
      PrestigeLevel next = current.next();

      AtomicBoolean max = new AtomicBoolean(false);

      if (current == PrestigeLevel.MAX) {
        throw new TranslatableCommandErrorException(MagmaTranslations.PRESTIGE_LEVELUP_MAX, end);
      }

      if (xp < next.getXp()) {
        int leftXp = next.getXp() - xp;
        int leftLevel = module.getXPLevelsNeeded(xp, next);
        throw new TranslatableCommandWarningException(MagmaTranslations.PRESTIGE_LEVELUP_FAIL,
            new LocalizedNumber(leftXp), new LocalizedNumber(leftLevel));
      }

      if (next == PrestigeLevel.MAX) {
        Bukkit.broadcast(MagmaTranslations.PRESTIGE_LEVELUP_MAXBROADCAST
            .with(ChatColor.GOLD, new UnlocalizedText(player.getDisplayName())));
        Task task = MagmaTask.of(() -> spawnFireworks(player)).repeat(0, 10);
        MagmaTask.of(task::cancel).later(80);
        max.set(true);
      } else {
        source.sendMessage(MagmaTranslations.PRESTIGE_LEVELUP_SUCCESS.with(ChatColor.GREEN));
        Bukkit.broadcast(MagmaTranslations.PRESTIGE_LEVELUP_BROADCAST
            .with(ChatColor.GREEN, new UnlocalizedText(player.getDisplayName()),
                new LocalizedNumber(next.getId())));
        spawnFireworks(player);
      }

      MagmaTask.of(() -> {
        Location location = player.getLocation();
        // Get players 50 blocks around player
        Vector boundLower = location.clone().add(-50, -50, -50).toVector();
        Vector boundUpper = location.clone().add(50, 50, 50).toVector();
        CuboidRegion region = new CuboidRegion(boundLower, boundUpper);
        Bukkit.getOnlinePlayers().forEach(p -> {
          if (region.contains(p) && p != player) {
            int reward = (max.get() ? 50 : 20);
            module.give(p, reward, "other");
            p.sendMessage(MagmaTranslations.PRESTIGE_LEVELUP_NEARBY.with(ChatColor.GREEN,
                new LocalizedNumber(reward, TextStyle.create().color(ChatColor.GOLD))));
            MagmaTask.of(() -> {
              NMSUtils.showFakeItems(Magma.get(), p, p.getLocation().clone().add(0, 1, 1),
                  new ItemStack(Material.RED_ROSE), 15, Seconds.seconds(4).toStandardDuration());
            }).now();
          }
        });

        module.levelUp(Users.user(player));
      }).nowAsync();
    }
  }
}
