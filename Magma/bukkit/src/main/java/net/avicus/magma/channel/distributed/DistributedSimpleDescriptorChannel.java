package net.avicus.magma.channel.distributed;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import net.avicus.magma.Magma;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.database.model.impl.ServerCategory;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.network.rtp.RTPHelpers;
import net.avicus.magma.text.Components;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

public abstract class DistributedSimpleDescriptorChannel extends DistributedSimpleChannel {

  protected final BaseComponent descriptor;

  protected DistributedSimpleDescriptorChannel(String id, String permission,
      BaseComponent descriptor) {
    super(id, permission);
    this.descriptor = descriptor;
  }

  public static BaseComponent channelDescriptor(String name, ChatColor color) {
    TextComponent component = new TextComponent(name);
    component.setBold(true);
    component.setColor(color.asBungee());
    return component;
  }

  @Override
  protected BaseComponent format(Server server, User source, BaseComponent[] components,
      Map<String, String> context) {
    final BaseComponent template = new TextComponent("");

    this.formatDescriptor(template, server, source);
    this.format(template, server, source, components, context);

    return template;
  }

  protected void formatDescriptor(final BaseComponent template, final Server server,
      final User source) {
    if (!server.isLocal()) {
      Optional<ServerCategory> serverCategory = Magma.get().database().getServerCategories()
          .fromServer(server);

      serverCategory.ifPresent(s -> {
        if (!s.getOptions().getRemotePrefix().isEmpty()) {
          template.addExtra(Components.simple("[", ChatColor.DARK_GRAY));
          template.addExtra(Components.simple(s.getOptions().getRemotePrefix(), ChatColor.BLUE));
          template.addExtra(Components.simple("]", ChatColor.DARK_GRAY));
          template.addExtra(" ");
        }
      });

      template.addExtra(Components.simple("[", ChatColor.GRAY));
      template.addExtra(RTPHelpers.clickableServer(server, Locale.US));
      template.addExtra(Components.simple("]", ChatColor.GRAY));
      template.addExtra(" ");
    }

    template.addExtra(Components.simple("[", ChatColor.GRAY));
    template.addExtra(this.descriptor);
    template.addExtra(Components.simple("]", ChatColor.GRAY));
    template.addExtra(" ");
  }

  protected abstract void format(BaseComponent template, Server server, User source,
      BaseComponent[] components, Map<String, String> context);
}
