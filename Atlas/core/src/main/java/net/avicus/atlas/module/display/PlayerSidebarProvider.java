package net.avicus.atlas.module.display;

import com.keenant.tabbed.item.PlayerTabItem.PlayerProvider;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.match.Match;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class PlayerSidebarProvider implements PlayerProvider<String> {

  private final Match match;
  private final Player viewer;

  public PlayerSidebarProvider(Match match, Player viewer) {
    this.match = match;
    this.viewer = viewer;
  }

  @Override
  public String get(Player player) {
    if (!player.isOnline()) {
      return ChatColor.STRIKETHROUGH + player.getName();
    }
    return Atlas.get().getBridge().displayName(this.match, this.viewer, player);
  }
}
