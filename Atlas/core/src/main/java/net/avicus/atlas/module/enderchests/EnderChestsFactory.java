package net.avicus.atlas.module.enderchests;

import java.util.List;
import java.util.Optional;
import net.avicus.atlas.SpecificationVersionHistory;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.ModuleDocumentation;
import net.avicus.atlas.documentation.SpecInformation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.ModuleBuildException;
import net.avicus.atlas.module.ModuleFactory;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.util.xml.XmlElement;

/**
 * Factory for the {@link EnderChestsModule}
 */
public class EnderChestsFactory implements ModuleFactory<EnderChestsModule> {

  @Override
  public ModuleDocumentation getDocumentation() {
    return ModuleDocumentation.builder()
        .category(ModuleDocumentation.ModuleCategory.MISC)
        .name("Ender Chests")
        .description(
            "This module is used to configure the behaviour of ender chests during the current map.")
        .description(
            "If this module is not defined, ender chests will work as they do in normal Minecraft.")
        .specInformation(
            SpecInformation.builder().added(SpecificationVersionHistory.LOADOUT_SUB_TAG).build())
        .tagName("enderchests")
        .feature(FeatureDocumentation.builder()
            .name("Configuration")
            .description("The main configuration for the enderchest module.")
            .attribute("exclusive", new GenericAttribute(Boolean.class, false,
                    "If ender chests should be shared for all matches on this map during the server's lifetime, or only kept as long as the match lasts."),
                true)
            .attribute("check", Attributes.check(false, "before an ender chest can be opened"))
            .build())
        .build();
  }

  @Override
  public Optional<EnderChestsModule> build(Match match, MatchFactory factory, XmlElement root)
      throws ModuleBuildException {
    List<XmlElement> elements = root.getChildren("enderchests");

    if (!elements.isEmpty()) {
      boolean exclusive = true;
      Optional<Check> openCheck = Optional.empty();
      for (XmlElement element : elements) {
        if (element.hasAttribute("exclusive")) {
          exclusive = element.getAttribute("exclusive").asRequiredBoolean();
        }
        if (element.hasAttribute("check") || element.hasChild("check")) {
          openCheck = FactoryUtils
              .resolveCheck(match, element.getAttribute("check"), element.getChild("check"));
        }
      }
      return Optional.of(new EnderChestsModule(match, exclusive, openCheck));
    }

    return Optional.empty();
  }
}
