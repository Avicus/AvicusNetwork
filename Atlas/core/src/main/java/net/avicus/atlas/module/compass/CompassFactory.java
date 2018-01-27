package net.avicus.atlas.module.compass;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.ModuleDocumentation;
import net.avicus.atlas.documentation.attributes.Attribute;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.ModuleBuildException;
import net.avicus.atlas.module.ModuleFactory;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.util.xml.XmlAttribute;
import net.avicus.atlas.util.xml.XmlElement;

public class CompassFactory implements ModuleFactory<CompassModule> {

  @Override
  public ModuleDocumentation getDocumentation() {
    return ModuleDocumentation.builder()
        .name("Compasses")
        .tagName("compasses")
        .description(
            "This module is used to customize where a player's compass points based on various conditions.")
        .description(
            "If multiple compasses are defined, checks will be used to determine which target is assigned to each player.")
        .category(ModuleDocumentation.ModuleCategory.MISC)
        .feature(FeatureDocumentation.builder()
            .name("Compass")
            .tagName("compass")
            .attribute("target", new Attribute() {
              @Override
              public String getName() {
                return "Compass Target/Point";
              }

              @Override
              public boolean isRequired() {
                return true;
              }

              @Override
              public String[] getDescription() {
                return new String[]{
                    "The targeting mode/point of the compass.",
                    "If 'enemy' is supplied, the compass will point to the closest enemy",
                    "If not, the compass will point to the provided x,y,z coordinate."
                };
              }
            })
            .attribute("check", Attributes
                .check(false, "when deciding which compass target to choose for each player."))
            .build())
        .build();
  }

  @Override
  public Optional<CompassModule> build(Match match, MatchFactory factory, XmlElement root)
      throws ModuleBuildException {
    List<XmlElement> elements = root.getChildren("compasses");

    if (elements.isEmpty()) {
      return Optional.empty();
    }

    List<Compass> compasses = new ArrayList<>();

    elements.forEach(element -> {
      for (XmlElement child : element.getChildren()) {
        XmlAttribute target = child.getAttribute("target");
        CompassResolver resolver;
        if (target.asRequiredString().toLowerCase().equals("enemy")) {
          resolver = new EnemyCompassResolver();
        } else {
          resolver = new PointCompassResolver(target.asRequiredVector());
        }

        Optional<Check> check = FactoryUtils
            .resolveCheckChild(match, child.getAttribute("check"), child.getChild("check"));
        compasses.add(new Compass(resolver, check));
      }
    });

    return Optional.of(new CompassModule(match, compasses));
  }

}
