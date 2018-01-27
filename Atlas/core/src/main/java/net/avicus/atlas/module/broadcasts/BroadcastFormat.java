package net.avicus.atlas.module.broadcasts;

import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.util.Translations;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizedText;
import net.avicus.compendium.locale.text.UnlocalizedFormat;
import org.bukkit.ChatColor;

/**
 * Format of messages that are scheduled for broadcast.
 */
public enum BroadcastFormat {
  DEFAULT(ChatColor.AQUA),
  TIP(ChatColor.AQUA),
  ALERT(ChatColor.YELLOW);

  private static final UnlocalizedFormat FORMAT = new UnlocalizedFormat("[{0}] {1}");
  private final String translation;
  private final ChatColor color;

  BroadcastFormat(ChatColor color) {
    this.translation = "ui.broadcast.type." + this.name().toLowerCase();
    this.color = color;
  }

  public Localizable apply(LocalizedXmlString message) {
    return new LocalizedXmlString(FORMAT,
        new LocalizedText(Translations.getBundle(), this.translation,
            TextStyle.ofColor(this.color)), message.toText(ChatColor.WHITE)).toText(ChatColor.GRAY);
  }
}
