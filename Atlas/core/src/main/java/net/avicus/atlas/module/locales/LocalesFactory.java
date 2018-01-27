package net.avicus.atlas.module.locales;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.ModuleDocumentation;
import net.avicus.atlas.documentation.attributes.Attribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.module.ModuleBuildException;
import net.avicus.atlas.module.ModuleFactory;
import net.avicus.atlas.module.ModuleFactorySort;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.compendium.locale.LocaleBundle;
import net.avicus.compendium.locale.LocaleStrings;

@ModuleFactorySort(ModuleFactorySort.Order.FIRST)
public class LocalesFactory implements ModuleFactory<LocalesModule> {

  @Override
  public ModuleDocumentation getDocumentation() {
    return ModuleDocumentation.builder()
        .name("Localization")
        .category(ModuleDocumentation.ModuleCategory.CORE)
        .description(
            "Localization is a requirement for all maps to accepted, we ask that you DO NOT simply google translate the localizations and if you need assistance translating your messages, to ask the translation team.")
        .description(
            "Localizations are defined inside of this tag and can be as deeply nested as need be.")
        .description(
            "Each new nesting is denoted by a . when referencing the locale in other parts of the XML.")
        .tagName("locales")
        .feature(FeatureDocumentation.builder()
            .name("Locale Entry")
            .tagName("locale")
            .description("This represents a single localized string.")
            .attribute("lang", new Attribute() {
              @Override
              public String getName() {
                return "Language";
              }

              @Override
              public boolean isRequired() {
                return true;
              }

              @Override
              public String[] getDescription() {
                return new String[]{"The identifier for the language."};
              }

              @Override
              public String getLink() {
                return "http://www.oracle.com/technetwork/java/javase/java8locales-2095355.html";
              }
            })
            .attribute("country", new Attribute() {
              @Override
              public String getName() {
                return "Country";
              }

              @Override
              public boolean isRequired() {
                return false;
              }

              @Override
              public String[] getDescription() {
                return new String[]{
                    "The country of the translated text (For example, to use Canadian English, you would define the lang as en and the country as ca)."};
              }

              @Override
              public String getLink() {
                return "http://www.oracle.com/technetwork/java/javase/java8locales-2095355.html";
              }
            })
            .build())
        .build();
  }

  @Override
  public Optional<LocalesModule> build(Match match, MatchFactory factory, XmlElement root)
      throws ModuleBuildException {
    List<XmlElement> elements = root.getChildren("locales");

    if (elements.isEmpty()) {
      return Optional.of(new LocalesModule());
    }

    List<LocaleStrings> list = new ArrayList<>();

    elements.forEach(element -> {
      for (XmlElement child : element.getChildren()) {
        LocaleStrings strings = LocaleStrings.fromXml(child.getJdomElement());
        list.add(strings);
      }
    });

    LocaleBundle bundle = new LocaleBundle(list);
    return Optional.of(new LocalesModule(bundle));
  }
}
