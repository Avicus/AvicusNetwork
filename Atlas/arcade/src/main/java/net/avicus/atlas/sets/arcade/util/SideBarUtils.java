package net.avicus.atlas.sets.arcade.util;

import net.avicus.atlas.util.ObjectiveUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SideBarUtils {

  public static String checkPlayer(Player player) {
    return ChatColor.GREEN + ObjectiveUtils.CHECK_MARK + ChatColor.RESET + " " + player
        .getDisplayName();
  }

  public static String xPlayer(Player player) {
    return ChatColor.RED + ObjectiveUtils.X_MARK + ChatColor.RESET + " " + player.getDisplayName();
  }

  public static String booleanPlayer(Player player, boolean check) {
    return check ? checkPlayer(player) : xPlayer(player);
  }
}
