package net.avicus.atlas.sets.walls;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.avicus.atlas.SpecificationVersionHistory;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.ModuleDocumentation;
import net.avicus.atlas.documentation.SpecInformation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.ModuleFactory;
import net.avicus.atlas.module.ModuleFactorySort;
import net.avicus.atlas.module.checks.types.TimeCheck;
import net.avicus.atlas.module.elimination.EliminationModule;
import net.avicus.atlas.module.groups.CompetitorRule;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.groups.teams.Team;
import net.avicus.atlas.module.objectives.ObjectivesFactory;
import net.avicus.atlas.module.objectives.lcs.LastCompetitorStanding;
import net.avicus.atlas.module.objectives.lts.LastTeamStanding;
import net.avicus.atlas.module.results.ResultsModule;
import net.avicus.atlas.module.results.scenario.ObjectivesScenario;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.compendium.inventory.SingleMaterialMatcher;
import net.avicus.compendium.number.NumberComparator;
import net.avicus.magma.util.region.BoundedRegion;
import org.joda.time.Duration;

/**
 * A factory to build {@link Wall}.
 */
@ModuleFactorySort(ModuleFactorySort.Order.LAST)
public final class WallFactory implements ModuleFactory<WallsModule> {

  @Override
  public ModuleDocumentation getDocumentation() {
    return ModuleDocumentation.builder()
        .name("Walls")
        .tagName("walls")
        .category(ModuleDocumentation.ModuleCategory.SPECIAL)
        .specInformation(SpecInformation.builder()
            .change(SpecificationVersionHistory.SEPARATE_WALLS,
                "Elimination is now enabled by walls.")
            .change(SpecificationVersionHistory.SEPARATE_WALLS,
                "Objectives are now created by the walls module.")
            .breakingChange(SpecificationVersionHistory.SEPARATE_WALLS,
                "Walls are no longer objectives and are now their own module.")
            .build()
        )
        .description(
            "The walls module is a collection of special features used for the Walls game type.")
        .description("This module can only be used on walls servers.")
        .description(
            "The walls module automatically enables one-life elimination, win scenarios for either teams or players, and a time limit.")
        .feature(FeatureDocumentation.builder()
            .name("Base Configuration")
            .description("The base configuration for the walls module.")
            .attribute("fall-time",
                Attributes.duration(true, true, "Time it takes for the walls to fall."))
            .attribute("end-time", Attributes.duration(true, false, "Total time of the match."))
            .build())
        .feature(FeatureDocumentation.builder()
            .name("Wall")
            .tagName("wall")
            .description(
                "A wall is a physical barrier in game that will fall after a specified time.")
            .attribute("region",
                Attributes.region(true, "Region that contains the blocks that should be removed."))
            .attribute("physical", new GenericAttribute(Boolean.class, false,
                "If there is a physical wall in the world already."), true)
            .attribute("source-material", Attributes.materialMatcher(false, false,
                "Materials to remove. If this is not included, all materials in the region will be removed."))
            .attribute("target-material", Attributes.materialMatcher(false, false,
                "The material that the blocks removed should turn into."), "air")
            .build())
        .build();
  }

  @Override
  public Optional<WallsModule> build(final Match match, final MatchFactory factory,
      final XmlElement root) {
    if (!root.hasChild("walls")) {
      return Optional.empty();
    }

    final Duration fallTime = root.getRequiredChild("walls").getAttribute("fall-time")
        .asRequiredDuration();
    final Duration endTime = root.getRequiredChild("walls").getAttribute("end-time")
        .asRequiredDuration();

    List<Wall> walls = Lists.newArrayList();

    root.getRequiredChild("walls").getChildren().forEach(element -> {
      element.inheritAttributes("walls");

      final BoundedRegion region = FactoryUtils
          .resolveRequiredRegionAs(match, BoundedRegion.class, element.getAttribute("region"),
              element.getChild("region"));
      final boolean physical = element.getAttribute("physical").asBoolean().orElse(true);
      @Nullable final SingleMaterialMatcher sourceMaterial = element.getAttribute("source-material")
          .asMaterialMatcher().orElse(null);
      @Nullable final SingleMaterialMatcher targetMaterial = element.getAttribute("target-material")
          .asMaterialMatcher().orElse(null);

      walls.add(new Wall(match, region, physical, sourceMaterial, targetMaterial));
    });

    if (walls.isEmpty()) {
      return Optional.empty();
    } else {
      if (match.getRequiredModule(GroupsModule.class).getCompetitorRule() == CompetitorRule.TEAM) {
        match.getRequiredModule(GroupsModule.class).getCompetitors().forEach(g -> {
          if (g instanceof Team) {
            match.getFactory().getFactory(ObjectivesFactory.class)
                .addObjective(new LastTeamStanding(match, (Team) g), match);
          }
        });
      } else {
        match.getFactory().getFactory(ObjectivesFactory.class)
            .addObjective(new LastCompetitorStanding(match), match);
      }
      match.getRequiredModule(ResultsModule.class).getScenarios()
          .add(new ObjectivesScenario(match, new TimeCheck(endTime, NumberComparator.EQUALS), 1));
      match.addModule(new EliminationModule(match, 1, true, true, Optional.empty()));
      return Optional.of(new WallsModule(walls, fallTime));
    }
  }
}
