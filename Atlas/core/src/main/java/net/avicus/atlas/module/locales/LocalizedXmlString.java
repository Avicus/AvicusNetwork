package net.avicus.atlas.module.locales;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizableFormat;
import net.avicus.compendium.locale.text.UnlocalizedFormat;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class LocalizedXmlString {

  private final Localizable text;

  public LocalizedXmlString(String format, Localizable... arguments) {
    this(format, new ArrayList<>(Arrays.asList(arguments)));
  }

  public LocalizedXmlString(LocalizableFormat format, Localizable... arguments) {
    this(format, new ArrayList<>(Arrays.asList(arguments)));
  }

  public LocalizedXmlString(String format, List<Localizable> arguments) {
    this(new UnlocalizedFormat(format), arguments);
  }

  public LocalizedXmlString(LocalizableFormat format, List<Localizable> arguments) {
    this.text = format.with(arguments.toArray(new Localizable[arguments.size()]));
  }

  public String translate(Locale locale) {
    BaseComponent component = this.text.translate(locale);
    // toPlainText() is misleading in this situation
    // colors will be present since we put in colors straight into
    // the string with "^colorcode".
    return component.toPlainText();
  }

  public String translate(CommandSender player) {
    return translate(player.getLocale());
  }

  public String translateDefault() {
    return translate(Bukkit.getConsoleSender());
  }

  public Localizable toText() {
    return this.text.duplicate();
  }

  public Localizable toText(ChatColor color) {
    return toText(TextStyle.ofColor(color));
  }

  public Localizable toText(TextStyle style) {
    Localizable text = toText();
    text.style().inherit(style);
    return text;
  }

  @Override
  public String toString() {
    return "LocalizedXmlString(text=" + translate(Locale.ENGLISH) + ")";
  }
}
