package net.avicus.atlas.util;

import java.util.Locale;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import org.bukkit.entity.Player;
import org.github.paperspigot.Title;

public class LocalizedXmlTitle {

  private final LocalizedXmlString title;
  private final LocalizedXmlString subtitle;
  private final int fadeIn;
  private final int stay;
  private final int fadeOut;

  public LocalizedXmlTitle(LocalizedXmlString title, LocalizedXmlString subtitle, int fadeIn,
      int stay, int fadeOut) {
    this.title = title;
    this.subtitle = subtitle;
    this.fadeIn = fadeIn;
    this.stay = stay;
    this.fadeOut = fadeOut;
  }

  public Title createTitle(Player player) {
    return new Title(
            this.title.render(player),
            this.subtitle.render(player),
            this.fadeIn,
            this.stay,
            this.fadeOut);
  }
}
