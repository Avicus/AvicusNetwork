package net.avicus.magma.channel.staff;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import net.avicus.compendium.settings.PlayerSettings;
import net.avicus.compendium.settings.Setting;
import net.avicus.compendium.settings.types.SettingTypes;
import net.avicus.magma.Magma;
import net.avicus.magma.channel.Channel;
import net.avicus.magma.channel.ChannelManager;
import net.avicus.magma.channel.distributed.DistributedSimpleDescriptorChannel;
import net.avicus.magma.module.CommandModule;
import net.avicus.magma.module.ListenerModule;
import net.avicus.magma.util.MagmaTranslations;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

public class StaffChannels implements CommandModule, ListenerModule {

  public static final Setting<Boolean> SETTING = new Setting<>(
      "staff-channels",
      SettingTypes.BOOLEAN,
      true,
      MagmaTranslations.SETTING_STAFFCHANNELS_NAME.with(),
      MagmaTranslations.SETTING_STAFFCHANNELS_SUMMARY.with()
  );
  public static final StaffChannel ADMIN_CHANNEL = new StaffChannel("admin", "channel.admins",
      DistributedSimpleDescriptorChannel.channelDescriptor("A", ChatColor.GOLD));
  public static final StaffChannel SENIOR_CHANNEL = new StaffChannel("senior", "channel.seniors",
      DistributedSimpleDescriptorChannel.channelDescriptor("SS", ChatColor.DARK_RED));
  public static final StaffChannel STAFF_CHANNEL = new StaffChannel("staff", "channel.staff",
      DistributedSimpleDescriptorChannel.channelDescriptor("S", ChatColor.RED));
  public static final StaffChannel DEV_CHANNEL = new StaffChannel("dev", "channel.devs",
      DistributedSimpleDescriptorChannel.channelDescriptor("D", ChatColor.BLUE));
  public static final StaffChannel MAPDEV_CHANNEL = new StaffChannel("mapdev", "channel.mapdevs",
      DistributedSimpleDescriptorChannel.channelDescriptor("MD", ChatColor.BLUE));
  public static final StaffChannel OFFICIAL_CHANNEL = new StaffChannel("official",
      "channel.officials",
      DistributedSimpleDescriptorChannel.channelDescriptor("OC", ChatColor.DARK_AQUA));
  public static final StaffChannel REF_CHANNEL = new StaffChannel("ref", "channel.refs",
      DistributedSimpleDescriptorChannel.channelDescriptor("R", ChatColor.DARK_AQUA));

  @Command(aliases = {
      "a"}, desc = "Admin channel", usage = "<msg...>", min = 1, anyFlags = true, flags = "c")
  @CommandPermissions("channel.admins")
  public static void admin(CommandContext ctx, CommandSender source) {
    messageChannel(ADMIN_CHANNEL, ctx, source);
  }

  @Command(aliases = {
      "ss"}, desc = "Senior staff channel", usage = "<msg...>", min = 1, anyFlags = true, flags = "c")
  @CommandPermissions("channel.seniors")
  public static void senior(CommandContext ctx, CommandSender source) {
    messageChannel(SENIOR_CHANNEL, ctx, source);
  }

  @Command(aliases = {
      "s"}, desc = "Staff channel", usage = "<msg...>", min = 1, anyFlags = true, flags = "c")
  @CommandPermissions("channel.staff")
  public static void staff(CommandContext ctx, CommandSender source) {
    messageChannel(STAFF_CHANNEL, ctx, source);
  }

  @Command(aliases = {
      "d"}, desc = "Dev channel", usage = "<msg...>", min = 1, anyFlags = true, flags = "c")
  @CommandPermissions("channel.devs")
  public static void dev(CommandContext ctx, CommandSender source) {
    messageChannel(DEV_CHANNEL, ctx, source);
  }

  @Command(aliases = {
      "md"}, desc = "Map developer channel", usage = "<msg...>", min = 1, anyFlags = true, flags = "c")
  @CommandPermissions("channel.mapdevs")
  public static void mapdev(CommandContext ctx, CommandSender source) {
    messageChannel(MAPDEV_CHANNEL, ctx, source);
  }

  @Command(aliases = {
      "oc"}, desc = "Official channel", usage = "<msg...>", min = 1, anyFlags = true, flags = "c")
  @CommandPermissions("channel.officials")
  public static void official(CommandContext ctx, CommandSender source) {
    messageChannel(OFFICIAL_CHANNEL, ctx, source);
  }

  @Command(aliases = {"rc",
      "ref"}, desc = "Referee channel", usage = "<msg...>", min = 1, anyFlags = true, flags = "c")
  @CommandPermissions("channel.refs")
  public static void ref(CommandContext ctx, CommandSender source) {
    messageChannel(REF_CHANNEL, ctx, source);
  }

  private static void messageChannel(final Channel channel, final CommandContext ctx,
      final CommandSender source) {
    channel.send(source, ctx.getJoinedStrings(0),
        ctx.hasFlag('c') && source.hasPermission("channels.formatting"));
  }

  @Override
  public void enable() {
    PlayerSettings.register(SETTING);
    final ChannelManager cm = Magma.get().getChannelManager();
    cm.register(ADMIN_CHANNEL);
    cm.register(SENIOR_CHANNEL);
    cm.register(STAFF_CHANNEL);
    cm.register(DEV_CHANNEL);
    cm.register(MAPDEV_CHANNEL);
    cm.register(OFFICIAL_CHANNEL);
    cm.register(REF_CHANNEL);
  }

  @Override
  public void registerCommands(CommandsManagerRegistration registrar) {
    registrar.register(StaffChannels.class);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void playerJoin(final PlayerJoinEvent event) {
    final Player player = event.getPlayer();
    if (!PlayerSettings.get(player, SETTING)) {
      player.sendMessage(MagmaTranslations.SETTING_STAFFCHANNELS_DISABLED.with());
    }
  }
}
