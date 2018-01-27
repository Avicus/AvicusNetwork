package net.avicus.magma.text;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

public class Components {

  public static BaseComponent simple(String string, ChatColor color) {
    return color(new TextComponent(string), color);
  }

  public static BaseComponent simple(BaseComponent[] components, ChatColor color) {
    return color(new TextComponent(components), color);
  }

  public static BaseComponent color(BaseComponent component, ChatColor color) {
    component.setColor(color.asBungee());
    return component;
  }

  public static BaseComponent event(BaseComponent component, ClickEvent clickEvent,
      HoverEvent hoverEvent) {
    component.setHoverEvent(hoverEvent);
    component.setClickEvent(clickEvent);
    return component;
  }
}
