package net.avicus.atlas.sets.arcade.carts;

import java.util.Optional;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.ModuleDocumentation;
import net.avicus.atlas.documentation.ModuleDocumentation.ModuleCategory;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.module.ModuleFactory;
import net.avicus.atlas.module.ModuleFactorySort;
import net.avicus.atlas.module.elimination.EliminationModule;
import net.avicus.atlas.module.objectives.ObjectivesFactory;
import net.avicus.atlas.module.objectives.lcs.LastCompetitorStanding;
import net.avicus.atlas.util.xml.XmlElement;
import org.bukkit.util.Vector;

@ModuleFactorySort(ModuleFactorySort.Order.LAST)
public final class CartsFactory implements ModuleFactory<CartsModule> {

  @Override
  public ModuleDocumentation getDocumentation() {
    return ModuleDocumentation.builder()
        .name("Musical Carts")
        .tagName("carts")
        .category(ModuleCategory.ARCADE)
        .description(
            "The musical carts game mode spawns minecarts around a center that players must enter to survive.")
        .description("This module can only be used on arcade servers.")
        .description(
            "This module automatically enables one-life elimination and a win scenario for players.")
        .feature(FeatureDocumentation.builder()
            .name("Configuration")
            .attribute("center",
                Attributes.vector(true, "The center of the spawning circle."))
            .attribute("radius",
                new GenericAttribute(Integer.class, true, "The initial radius to spawn carts in."))
            .attribute("radius-subtract", new GenericAttribute(Integer.class, true,
                "The amount that the radius should be shrank by each round."))
            .attribute("height", new GenericAttribute(Integer.class, true,
                "The Y value that all carts should spawn at."))
            .build())
        .build();
  }

  @Override
  public Optional<CartsModule> build(final Match match, final MatchFactory factory,
      final XmlElement root) {
    if (!root.hasChild("carts")) {
      return Optional.empty();
    }

    XmlElement element = root.getChild("carts").get();
    Vector center = element.getAttribute("center").asRequiredVector();
    int radius = element.getAttribute("radius").asRequiredInteger();
    int radiusSubtract = element.getAttribute("radius-subtract").asRequiredInteger();
    int height = element.getAttribute("height").asRequiredInteger();

    match.getFactory().getFactory(ObjectivesFactory.class)
        .addObjective(new LastCompetitorStanding(match), match);
    match.addModule(new EliminationModule(match, 1, true, false, Optional.empty()));
    return Optional
        .of(new CartsModule(match, center, radius,
            radiusSubtract, height));
  }
}
