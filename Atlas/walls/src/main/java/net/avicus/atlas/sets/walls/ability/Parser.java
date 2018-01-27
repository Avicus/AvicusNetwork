package net.avicus.atlas.sets.walls.ability;

import java.util.Optional;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.kits.KitsFactory;
import net.avicus.atlas.sets.walls.WallFactory;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.atlas.util.xml.named.NamedParser;
import net.avicus.atlas.util.xml.named.NamedParsers;

public class Parser {

  public void loadParsers() {
    KitsFactory.NAMED_PARSERS.row(this).putAll(NamedParsers.methods(Parser.class));

    KitsFactory.FEATURES.add(FeatureDocumentation.builder()
        .name("Lumber Jack")
        .tagName("lumber-jack")
        .description(
            "The lumber jack ability allows players to chop down columns of matching logs up to 60 blocks tall with just breaking the bottom log.")
        .requirement(WallFactory.class)
        .attribute("check", Attributes.check(false, "before the ability can be used"))
        .build());
  }

  @NamedParser("lumber-jack")
  private LumberJackAbility parseLumberJack(Match match, XmlElement element) {
    Optional<Check> check = FactoryUtils
        .resolveCheckChild(match, element.getAttribute("check"), element.getChild("check"));
    return new LumberJackAbility(match, check);
  }
}
