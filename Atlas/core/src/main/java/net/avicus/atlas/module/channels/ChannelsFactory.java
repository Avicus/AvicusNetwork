package net.avicus.atlas.module.channels;

import java.util.Optional;
import net.avicus.atlas.SpecificationVersionHistory;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.ModuleDocumentation;
import net.avicus.atlas.documentation.SpecInformation;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.module.ModuleBuildException;
import net.avicus.atlas.module.ModuleFactory;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.atlas.util.xml.XmlException;

/**
 * Factory for the {@link ChannelsModule}
 */
public class ChannelsFactory implements ModuleFactory<ChannelsModule> {

  @Override
  public ModuleDocumentation getDocumentation() {
    return ModuleDocumentation.builder()
        .name("Chat Channels")
        .tagName("channels")
        .category(ModuleDocumentation.ModuleCategory.COMPONENTS)
        .specInformation(SpecInformation.builder()
            .deprecated(SpecificationVersionHistory.REPAIR_CHECK_DEF_OFF)
            .removed(SpecificationVersionHistory.UNTOUCHABLE_CHAT_CHANNELS)
            .build()
        )
        .feature(FeatureDocumentation.builder()
            .name("Main Configuration")
            .attribute("team-chat",
                new GenericAttribute(Boolean.class, false, "If team chat is enabled."), true)
            .attribute("global-chat",
                new GenericAttribute(Boolean.class, false, "If global chat is enabled."), true)
            .build()
        )
        .build();
  }

  @Override
  public Optional<ChannelsModule> build(Match match, MatchFactory factory, XmlElement root)
      throws ModuleBuildException {
    XmlElement element = root.getChild("channels").orElse(null);

    boolean allowTeamChat = true;
    boolean allowGlobalChat = true;

    if (element != null) {
      if (!match.getMap().getSpecification()
          .greaterEqual(SpecificationVersionHistory.UNTOUCHABLE_CHAT_CHANNELS)) {
        allowTeamChat = element.getAttribute("team-chat").asRequiredBoolean();
        allowGlobalChat = element.getAttribute("global-chat").asRequiredBoolean();
      } else {
        match.warnDeprecation("Channels are no longer configurable",
            SpecificationVersionHistory.UNTOUCHABLE_CHAT_CHANNELS);
      }
    }

    if (!allowTeamChat && !allowGlobalChat) {
      throw new XmlException(element, "Team chat and global chat cannot be both disabled");
    }

    return Optional.of(new ChannelsModule(match, allowTeamChat, allowGlobalChat));
  }
}
