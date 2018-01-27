package net.avicus.atlas.module.groups;

import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.util.color.TeamColor;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.magma.util.distance.PlayerStore;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

public interface Competitor extends PlayerStore {

  String getId();

  LocalizedXmlString getName();

  default Localizable getColoredName() {
    return getName().toText(TextStyle.ofColor(getChatColor()));
  }

  Group getGroup();

  boolean hasPlayer(Player player);

  TeamColor getTeamColor();

  default ChatColor getChatColor() {
    return getTeamColor().getChatColor();
  }

  default DyeColor getDyeColor() {
    return getTeamColor().getDyeColor();
  }

  default Color getFireworkColor() {
    return getTeamColor().getFireworkColor();
  }
}
