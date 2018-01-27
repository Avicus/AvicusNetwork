package net.avicus.atlas.module.broadcasts;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.ModuleDocumentation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.EnumAttribute;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.match.registry.RegisteredObject;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.ModuleBuildException;
import net.avicus.atlas.module.ModuleFactory;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.locales.LocalesModule;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.util.xml.XmlElement;
import org.joda.time.Duration;

/**
 * Factory for the {@link BroadcastsModule}
 */
public class BroadcastsFactory implements ModuleFactory<BroadcastsModule> {

  @Override
  public ModuleDocumentation getDocumentation() {
    return ModuleDocumentation.builder()
        .category(ModuleDocumentation.ModuleCategory.COMPONENTS)
        .name("Broadcasts")
        .tagFilter((e) -> e.getName().equals("broadcasts"))
        .description("Broadcasts are used to display messages in game.")
        .feature(
            FeatureDocumentation.builder()
                .name("Broadcast")
                .tagFilter((e) -> e.getName().equals("broadcasts"))
                .attribute("id", Attributes.id(false))
                .attribute("message",
                    new GenericAttribute(String.class, true, "The content of the broadcast."))
                .attribute("format",
                    new EnumAttribute(BroadcastFormat.class, false, "The format of the broadcast"),
                    BroadcastFormat.DEFAULT)
                .attribute("check", Attributes.check(false, "before the broadcast is displayed"))
                .attribute("interval",
                    Attributes.duration(true, true, "The time between broadcasts."))
                .attribute("repetition-count", new GenericAttribute(Integer.class, true,
                    "Amount of times the broadcast should repeat.",
                    "If this is not included, the broadcast will repeat infinitely."
                ))
                .build()
        )
        .build();
  }

  @Override
  public Optional<BroadcastsModule> build(Match match, MatchFactory factory, XmlElement root)
      throws ModuleBuildException {
    List<XmlElement> elements = root.getChildren("broadcasts");
    if (elements.isEmpty()) {
      return Optional.empty();
    }

    List<Broadcast> broadcasts = new ArrayList<>();

    elements.forEach(element -> {
      for (XmlElement child : element.getChildren()) {
        Broadcast broadcast = parseBroadcast(match, child);

        if (child.hasAttribute("id")) {
          RegisteredObject registered = new RegisteredObject<>(
              child.getAttribute("id").asRequiredString(), broadcast);
          match.getRegistry().add(registered);
        }
        broadcasts.add(broadcast);
      }
    });

    return Optional.of(new BroadcastsModule(match, broadcasts));
  }

  /**
   * Parses a broadcast from XML
   *
   * @param match match that the broadcast will exist in
   * @param element root element of the broadcast
   * @return a parsed broadcast
   */
  private Broadcast parseBroadcast(Match match, XmlElement element) {
    String textRaw = element.getText().asRequiredString();
    LocalizedXmlString text = match.getRequiredModule(LocalesModule.class).parse(textRaw);

    BroadcastFormat format = element.getAttribute("format").asEnum(BroadcastFormat.class, true)
        .orElse(BroadcastFormat.DEFAULT);
    Duration interval = element.getAttribute("interval").asRequiredDuration();
    Optional<Integer> repetitionCount = element.getAttribute("repetitions").asInteger();
    Optional<Check> check = FactoryUtils
        .resolveCheckChild(match, element.getAttribute("check"), element.getChild("check"));

    return new Broadcast(text, format, interval, repetitionCount, check);
  }
}
