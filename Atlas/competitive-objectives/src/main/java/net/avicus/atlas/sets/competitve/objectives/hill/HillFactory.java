package net.avicus.atlas.sets.competitve.objectives.hill;

import java.util.Optional;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.EnumAttribute;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.groups.teams.Team;
import net.avicus.atlas.module.locales.LocalesModule;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.module.objectives.ObjectiveFactory;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.compendium.inventory.MultiMaterialMatcher;
import net.avicus.magma.util.region.BoundedRegion;
import net.avicus.magma.util.region.Region;
import org.joda.time.Duration;

public class HillFactory implements ObjectiveFactory<HillObjective> {

  @Override
  public FeatureDocumentation getDocumentation() {
    return FeatureDocumentation.builder()
        .name("Hill")
        .tagName("hills").tagName("hill")
        .description("Hills are areas that teams fight over for control.")
        .attribute("id", Attributes.id(false))
        .attribute("name", new GenericAttribute(LocalizedXmlString.class, true,
            "The name of the objective (for the UI)."))
        .attribute("initial-owner",
            Attributes.idOf(false, "team", "The owner of the objective when the match starts."))
        .attribute("capture",
            Attributes.region(true, "The region that contains the area that must be captured."))
        .attribute("progress",
            Attributes.region(true, "The region that contains where progress should be displayed."))
        .attribute("capture-rule", new EnumAttribute(HillCaptureRule.class, false,
                "Rule used to determine which team is currently capturing the hill."),
            HillCaptureRule.EXCLUSIVE)
        .attribute("capture-time", Attributes
            .duration(true, true, "The time required for a team to capture the hill completely."))
        .attribute("capture-check",
            Attributes.check(false, "before the hill can be captured by a player"))
        .attribute("points", new GenericAttribute(Integer.class, false,
            "Number of points the owning team should score every second."))
        .attribute("points-growth", Attributes
            .duration(false, false, "Time during point earn before points should be doubled."))
        .attribute("fireworks", new GenericAttribute(Boolean.class, false,
            "If fireworks should shoot off of the objective when it is captured."), true)
        .attribute("permanent", new GenericAttribute(Boolean.class, false,
            "Hill can only be captured once during a match."), false)
        .attribute("depreciate", new GenericAttribute(Boolean.class, false,
                "If a hill is controlled and no team is capturing it, the completion will depreciate over time."),
            false)
        .attribute("ignored-blocks", Attributes.materialMatcher(false, true,
            "Specific types of blocks that should be ignored during progress indication."))
        .attribute("earn-points", new EnumAttribute(PointEarnRule.class, false,
                "Rule used to determine when teams should earn points from owning hills."),
            PointEarnRule.STANDING_ON)
        .build();
  }

  @Override
  public HillObjective build(Match match, MatchFactory factory, XmlElement element) {
    // Grabs attributes from parent <hills/>
    element.inheritAttributes("hills");

    // name
    String rawName = element.getAttribute("name").asRequiredString();
    LocalizedXmlString name = match.getRequiredModule(LocalesModule.class).parse(rawName);

    // capture
    Region capture = FactoryUtils
        .resolveRequiredRegionAs(match, Region.class, element.getAttribute("capture"),
            element.getChild("capture"));

    // progress
    BoundedRegion progress = FactoryUtils
        .resolveRequiredRegionAs(match, BoundedRegion.class, element.getAttribute("progress"),
            element.getChild("progress"));

    // capture-rule
    HillCaptureRule captureRule = element.getAttribute("capture-rule")
        .asEnum(HillCaptureRule.class, true).orElse(HillCaptureRule.EXCLUSIVE);

    // capture-time
    Duration captureTime = element.getAttribute("capture-time").asRequiredDuration();

    // capture-check
    Optional<Check> captureCheck = FactoryUtils
        .resolveCheckChild(match, element.getAttribute("capture-check"),
            element.getChild("capture-check"));

    // initial-owner
    Optional<Team> initialOwner = Optional.empty();
    if (element.hasAttribute("initial-owner")) {
      initialOwner = match.getRegistry()
          .get(Team.class, element.getAttribute("initial-owner").asRequiredString(), true);
    }

    // points
    Optional<Integer> points = element.getAttribute("points").asInteger();

    // points-growth
    Optional<Duration> pointsGrowth = element.getAttribute("points-growth").asDuration();

    // fireworks
    boolean fireworks = element.getAttribute("fireworks").asBoolean().orElse(true);

    // permanent
    boolean permanent = element.getAttribute("permanent").asBoolean().orElse(false);

    // depreciate
    boolean depreciate = element.getAttribute("depreciate").asBoolean().orElse(false);

    // ignored blocks
    Optional<MultiMaterialMatcher> ignoredBlocks = element.getAttribute("ignored-blocks")
        .asMultiMaterialMatcher();

    PointEarnRule earnRule = element.getAttribute("earn-points").asEnum(PointEarnRule.class, true)
        .orElse(PointEarnRule.STANDING_ON);

    return new HillObjective(
        match,
        name,
        capture,
        progress,
        captureRule,
        captureTime,
        captureCheck,
        initialOwner,
        points,
        pointsGrowth,
        fireworks,
        permanent,
        depreciate,
        ignoredBlocks,
        earnRule);
  }
}
