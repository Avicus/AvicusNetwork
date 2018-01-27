package net.avicus.atlas.util.color;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;

/**
 * Represents the possible colors that a team can have.
 */
public enum TeamColor {
  AQUA(ChatColor.AQUA, DyeColor.LIGHT_BLUE),
  CYAN(ChatColor.DARK_AQUA, DyeColor.CYAN),
  GRAY(ChatColor.GRAY, DyeColor.SILVER),
  DARK_GRAY(ChatColor.DARK_GRAY, DyeColor.GRAY),
  RED(ChatColor.DARK_RED, DyeColor.RED),
  BLUE(ChatColor.BLUE, DyeColor.BLUE),
  GREEN(ChatColor.DARK_GREEN, DyeColor.GREEN),
  LIME(ChatColor.GREEN, DyeColor.LIME),
  YELLOW(ChatColor.YELLOW, DyeColor.YELLOW),
  ORANGE(ChatColor.GOLD, DyeColor.ORANGE),
  PURPLE(ChatColor.DARK_PURPLE, DyeColor.PURPLE),
  PINK(ChatColor.LIGHT_PURPLE, DyeColor.PINK),
  BLACK(ChatColor.BLACK, DyeColor.BLACK),
  WHITE(ChatColor.WHITE, DyeColor.WHITE);

  @Getter
  ChatColor chatColor;
  @Getter
  DyeColor dyeColor;

  TeamColor(ChatColor chatColor, DyeColor dyeColor) {
    this.chatColor = chatColor;
    this.dyeColor = dyeColor;
  }

  public Color getColor() {
    return this.dyeColor.getColor();
  }

  public Color getFireworkColor() {
    return this.dyeColor.getFireworkColor();
  }

  public String getName() {
    return name();
  }
}
