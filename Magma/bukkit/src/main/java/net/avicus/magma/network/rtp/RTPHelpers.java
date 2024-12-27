package net.avicus.magma.network.rtp;

import java.util.Locale;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.network.user.Users;
import net.avicus.magma.util.MagmaTranslations;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

public class RTPHelpers {

  public static BaseComponent clickableServer(Server server, Locale locale) {
    final BaseComponent component = new TextComponent(server.getName());
    component.setColor(ChatColor.GOLD);
    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
        MagmaTranslations.RTP_SERVER_CONNECT
            .with(new UnlocalizedText(server.getName(), org.bukkit.ChatColor.GOLD)).render(null)
    }));
    component.setClickEvent(
        new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + server.getName()));
    return component;
  }

  public static BaseComponent permissibleClickablePlayer(CommandSender viewer, Server server,
      User player, Locale locale) {
    return permissibleClickablePlayer(viewer, server, player, locale, false);
  }

  public static BaseComponent permissibleClickablePlayer(CommandSender viewer, Server server,
      User player, Locale locale, boolean includeRanks) {
    final UnlocalizedText displayName = Users.getTranslatableDisplayName(player, includeRanks);
    final BaseComponent component = displayName.render(null);
    if (viewer.hasPermission(RemoteTeleports.PERMISSION)) {
      component
          .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rtp " + player.getName()));
      component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
          (server.isLocal() ? MagmaTranslations.RTP_PLAYER_TELEPORT_LOCAL.with(displayName)
              : MagmaTranslations.RTP_PLAYER_TELEPORT_REMOTE.with(displayName,
                  new UnlocalizedText(server.getName(), org.bukkit.ChatColor.GOLD))).render(null)
      }));
    }
    return component;
  }

  public static BaseComponent clickablePlayer(Server server, User player, Locale locale) {
    final UnlocalizedText displayName = Users.getTranslatableDisplayName(player, false);
    final BaseComponent component = displayName.render(null);
    component
        .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rtp " + player.getName()));
    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
        (server.isLocal() ? MagmaTranslations.RTP_PLAYER_TELEPORT_LOCAL.with(displayName)
            : MagmaTranslations.RTP_PLAYER_TELEPORT_REMOTE.with(displayName,
                new UnlocalizedText(server.getName(), org.bukkit.ChatColor.GOLD))).render(null)
    }));
    return component;
  }

  public static BaseComponent clickablePlayerFullName(Server server, User player, Locale locale) {
    final UnlocalizedText displayName = Users.getTranslatableDisplayName(player, true);
    final BaseComponent component = displayName.render(null);
    component
        .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rtp " + player.getName()));
    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
        (server.isLocal() ? MagmaTranslations.RTP_PLAYER_TELEPORT_LOCAL.with(displayName)
            : MagmaTranslations.RTP_PLAYER_TELEPORT_REMOTE.with(displayName,
                new UnlocalizedText(server.getName(), org.bukkit.ChatColor.GOLD))).render(null)
    }));
    return component;
  }

}
