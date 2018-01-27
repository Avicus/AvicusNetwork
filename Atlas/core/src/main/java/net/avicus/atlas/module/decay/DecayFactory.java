package net.avicus.atlas.module.decay;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.ModuleDocumentation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.ModuleBuildException;
import net.avicus.atlas.module.ModuleFactory;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.compendium.inventory.SingleMaterialMatcher;
import net.avicus.magma.util.region.BoundedRegion;
import org.bukkit.util.Vector;
import org.joda.time.Duration;


public class DecayFactory implements ModuleFactory<DecayModule> {

  @Override
  public ModuleDocumentation getDocumentation() {
    return ModuleDocumentation.builder()
        .name("Decay Areas and Phases")
        .tagName("decay")
        .description(
            "This module is used to make blocks change phases under players and eventually fall at the specified time.")
        .category(ModuleDocumentation.ModuleCategory.MISC)
        .feature(FeatureDocumentation.builder()
            .name("Decay Area")
            .tagName("area")
            .description(
                "These represent areas of the map that the decay module should work inside of.")
            .attribute("region",
                Attributes.region(true, "The region that the block decay should act inside of."))
            .attribute("fall-delay", Attributes.duration(false, true,
                "Time after the last phase completes before the blocks should turn into falling sand."),
                4)
            .subFeature(FeatureDocumentation.builder()
                .name("Decay Phase")
                .tagName("phase")
                .description(
                    "These denote materials that the blocks will change into in order before falling.")
                .attribute("delay", Attributes.duration(false, false,
                    "How long this phase should last before the next phase is applied."), 3)
                .text(Attributes.materialMatcher(true, false, "Material of the phase."))
                .build())
            .build())
        .build();
  }

  @Override
  public Optional<DecayModule> build(Match match, MatchFactory factory, XmlElement root)
      throws ModuleBuildException {
    List<XmlElement> elements = root.getChildren("decay");
    if (elements.isEmpty()) {
      return Optional.empty();
    }

    List<DecayArea> areas = new ArrayList<>();
    for (XmlElement element : elements) {
      for (XmlElement area : element.getChildren("area")) {
        BoundedRegion region = FactoryUtils
            .resolveRequiredRegionAs(match, BoundedRegion.class, area.getAttribute("region"),
                area.getChild("region"));

        final List<ModuleBuildException> exceptions = new ArrayList<>();

        for (DecayArea decayArea : areas) {
          Iterator<Vector> vector = decayArea.getRegion().iterator();
          vector.forEachRemaining((a) -> {
            if (region.contains(a)) {
              exceptions
                  .add(new ModuleBuildException(this, "Cannot define overlapping decay regions."));
            }
          });
        }

        if (!exceptions.isEmpty()) {
          throw exceptions.get(0);
        }

        List<DecayPhase> phases = new ArrayList<>();

        for (XmlElement phase : area.getChildren("phase")) {
          Duration delay = phase.getAttribute("delay").asDuration()
              .orElse(Duration.standardSeconds(3));
          SingleMaterialMatcher matcher = phase.getText().asRequiredMaterialMatcher();

          phases.add(
              new DecayPhase(delay, matcher.getMaterial(), matcher.getData().orElse((byte) 0)));
        }

        Duration fallDelay = area.getAttribute("fall-delay").asDuration()
            .orElse(Duration.standardSeconds(4));

        areas.add(new DecayArea(region, phases, fallDelay));
      }
    }

    return Optional.of(new DecayModule(match, areas));
  }
}
