package net.avicus.magma.channel.premium;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import net.avicus.compendium.settings.PlayerSettings;
import net.avicus.compendium.settings.Setting;
import net.avicus.compendium.settings.types.SettingTypes;
import net.avicus.magma.Magma;
import net.avicus.magma.module.CommandModule;
import net.avicus.magma.module.ListenerModule;
import net.avicus.magma.util.MagmaTranslations;
import org.bukkit.command.CommandSender;

public class Premium implements CommandModule, ListenerModule {

  public static final Setting<Boolean> SETTING = new Setting<>(
      "premium-chat",
      SettingTypes.BOOLEAN,
      true,
      MagmaTranslations.SETTING_PREMIUM_NAME.with(),
      MagmaTranslations.SETTING_PREMIUM_SUMMARY.with()
  );

  private final PremiumChannel premiumChannel = new PremiumChannel();
  private final BedrockChannel bedrockChannel = new BedrockChannel();


  private static PremiumChannel getPremiumChannel() {
    return Magma.get().getChannelManager().getChannel("premium", PremiumChannel.class);
  }

  private static BedrockChannel getBedrockChannel() {
    return Magma.get().getChannelManager().getChannel("bedrock", BedrockChannel.class);
  }

  @Command(aliases = {"pr",
      "premium"}, desc = "Premium chat", usage = "<msg...>", min = 1, anyFlags = true)
  @CommandPermissions("channel.premium")
  public static void premium(CommandContext ctx, CommandSender source) {
    getPremiumChannel().send(source, ctx.getJoinedStrings(0),
        source.hasPermission("channels.colors"), source.hasPermission("channels.formatting"));
  }

  @Command(aliases = {"bero",
      "bedrock"}, desc = "Bedrock chat", usage = "<msg...>", min = 1, anyFlags = true)
  @CommandPermissions("channel.bedrock")
  public static void bedrock(CommandContext ctx, CommandSender source) {
    getBedrockChannel().send(source, ctx.getJoinedStrings(0),
        source.hasPermission("channels.colors"), source.hasPermission("channels.formatting"));
  }

  @Override
  public void enable() {
    PlayerSettings.register(SETTING);
    Magma.get().getChannelManager().register(this.premiumChannel);
    Magma.get().getChannelManager().register(this.bedrockChannel);
  }

  @Override
  public void registerCommands(CommandsManagerRegistration registrar) {
    registrar.register(Premium.class);
  }
}
