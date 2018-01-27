package net.avicus.atlas.module.locales;

import com.google.common.base.Splitter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.ToString;
import net.avicus.atlas.module.Module;
import net.avicus.compendium.locale.LocaleBundle;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizedText;
import net.avicus.compendium.utils.Strings;

@ToString
public class LocalesModule implements Module {

  private final Optional<LocaleBundle> bundle;

  public LocalesModule(LocaleBundle bundle) {
    this.bundle = Optional.of(bundle);
  }

  public LocalesModule() {
    this.bundle = Optional.empty();
  }

  /**
   * Parses a string and resolves it as a LocalizedString using
   * the bundle in this module.
   * <p>
   * Examples:
   * "{colors.red} wins!" -> "Red Wins!"
   * "{directions.right}" -> "Right"
   * "{winner,color.blue}" -> "The winner is Blue!"
   */
  public LocalizedXmlString parse(String text) {
    if (!this.bundle.isPresent()) {
      return new LocalizedXmlString(text);
    }

    List<Localizable> arguments = new ArrayList<>();

    // Matches anything within { }.
    Pattern pattern = Pattern.compile("\\{([^}]*)\\}");
    Matcher matcher = pattern.matcher(text);

    int argNum = 0;

    while (matcher.find()) {
      // Grabs the content within each { }.
      String raw = matcher.group(1);

      // Separate localized ids by comma
      List<String> split = Splitter.on(",").splitToList(raw);

      // The first is the key
      String key = split.get(0);
      List<Localizable> keyArgs = new ArrayList<>();

      // The others are arguments that go into the key
      for (int i = 1; i < split.size(); i++) {
        keyArgs.add(new LocalizedText(this.bundle.get(), split.get(i)));
      }

      // Add and move onto the next one
      arguments.add(new LocalizedText(this.bundle.get(), key, keyArgs));
      text = text.replace(raw, argNum + "");
      argNum++;
    }

    return new LocalizedXmlString(Strings.addColors(text), arguments);
  }

  public LocaleBundle safeGetBundle() {
    return this.bundle.isPresent() ? this.bundle.get() : new LocaleBundle();
  }
}
