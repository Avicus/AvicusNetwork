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
    return createTitle(player.getLocale());
  }

  public Title createTitle(Locale locale) {
    return new Title(
        this.title.translate(locale),
        this.subtitle.translate(locale),
        this.fadeIn,
        this.stay,
        this.fadeOut);
  }
}
