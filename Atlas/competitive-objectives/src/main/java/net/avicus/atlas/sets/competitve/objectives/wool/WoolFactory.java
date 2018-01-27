package net.avicus.atlas.sets.competitve.objectives.wool;

import java.util.Optional;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.groups.teams.Team;
import net.avicus.atlas.module.objectives.ObjectiveFactory;
import net.avicus.atlas.module.objectives.ObjectivesFactory;
import net.avicus.atlas.module.objectives.TouchableDistanceMetrics;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.magma.util.distance.DistanceCalculationMetric;
import net.avicus.magma.util.region.BoundedRegion;
import org.bukkit.DyeColor;
import org.joda.time.Duration;

public class WoolFactory implements ObjectiveFactory<WoolObjective> {

  @Override
  public FeatureDocumentation getDocumentation() {
    return FeatureDocumentation.builder()
        .name("Wool")
        .tagName("wools").tagName("wool")
        .description("Players must retrieve wool blocks and place them within a region.")
        .attribute("id", Attributes.id(false))
        .attribute("team", Attributes.idOf(false, "team",
            "The owner of the objective. (This will cause the objective to not be able to be completed by this team and the objective to be grouped under this team in the sidebar.)"))
        .attribute("color", Attributes.javaDoc(true, DyeColor.class, "Color of the wool."))
        .attribute("source", Attributes.region(false,
            "The region that contains the location where the wool can be picked up. This is used for chest refill and proximity."))
        .attribute("destination", Attributes
            .region(true, "The region that contains the location where the wool must be placed."))
        .attribute("pickup", new GenericAttribute(Boolean.class, false,
            "If the wool can be picked up off of the ground."), true)
        .attribute("refill", new GenericAttribute(Boolean.class, false,
            "If the chests in the source region should automatically refill with wool."), true)
        .attribute("max-refill", new GenericAttribute(Integer.class, false,
            "The maximum amount of wool that should be refilled."), "oo")
        .attribute("refill-delay",
            Attributes.duration(false, true, "The amount of time before wools are refilled."), 0)
        .attribute("craftable", new GenericAttribute(Boolean.class, false,
            "If the wool can be crafted in a work bench."), false)
        .attribute("fireworks", new GenericAttribute(Boolean.class, false,
            "If fireworks should be spawned at the destination when the wool is placed."), true)
        .attributes(ObjectivesFactory.proximity("source", "before the objective is touched", false,
            DistanceCalculationMetric.Type.PLAYER, true))
        .attributes(ObjectivesFactory.proximity("dest", "after the objective is touched", false,
            DistanceCalculationMetric.Type.PLAYER, true))
        .build();
  }

  @Override
  public WoolObjective build(Match match, MatchFactory factory, XmlElement element) {
    element.inheritAttributes("wools");

    // team
    Optional<Team> team = match.getRegistry()
        .get(Team.class, element.getAttribute("team").asRequiredString(), false);

    // color
    DyeColor color = element.getAttribute("color").asRequiredEnum(DyeColor.class, true);

    // source
    Optional<BoundedRegion> source = FactoryUtils
        .resolveRegionAs(match, BoundedRegion.class, element.getAttribute("source"),
            element.getChild("source"));

    // destination
    BoundedRegion destination = FactoryUtils
        .resolveRequiredRegionAs(match, BoundedRegion.class, element.getAttribute("destination"),
            element.getChild("destination"));

    // pickup
    boolean pickup = element.getAttribute("pickup").asBoolean().orElse(true);

    // refill
    boolean refill = element.getAttribute("refill").asBoolean().orElse(true);

    int maxRefill = element.getAttribute("max-refill").asInteger().orElse(Integer.MAX_VALUE);

    Optional<Duration> refillDelay = element.getAttribute("refill-delay").asDuration();

    // craftable
    boolean craftable = element.getAttribute("craftable").asBoolean().orElse(false);

    // fireworks
    boolean fireworks = element.getAttribute("fireworks").asBoolean().orElse(true);

    TouchableDistanceMetrics metrics = new TouchableDistanceMetrics.Builder()
        .postTouch(element, "dest",
            new DistanceCalculationMetric(DistanceCalculationMetric.Type.PLAYER, true))
        .preComplete(element, "source",
            new DistanceCalculationMetric(DistanceCalculationMetric.Type.PLAYER, true))
        .build();

    return new WoolObjective(
        match,
        metrics,
        team,
        color,
        source,
        destination,
        pickup,
        refill,
        maxRefill,
        refillDelay,
        craftable,
        fireworks);
  }
}
