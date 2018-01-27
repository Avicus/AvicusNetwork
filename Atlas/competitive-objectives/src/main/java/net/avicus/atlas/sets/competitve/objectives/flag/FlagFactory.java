package net.avicus.atlas.sets.competitve.objectives.flag;

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
import net.avicus.atlas.module.loadouts.LoadoutsFactory;
import net.avicus.atlas.module.objectives.ObjectiveFactory;
import net.avicus.atlas.module.objectives.ObjectivesFactory;
import net.avicus.atlas.module.zones.ZonesFactory;
import net.avicus.atlas.sets.competitve.objectives.zones.flag.PostZone;
import net.avicus.atlas.util.ScopableItemStack;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.magma.util.distance.DistanceCalculationMetric;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.joda.time.Duration;
import org.joda.time.Seconds;

public class FlagFactory implements ObjectiveFactory<FlagObjective> {

  @Override
  public FeatureDocumentation getDocumentation() {
    return FeatureDocumentation.builder()
        .name("Flags")
        .tagName("flags").tagName("flag")
        .description("Players must retrieve flags and return them to a net region.")
        .requirement(ZonesFactory.class)
        .attribute("post",
            Attributes.idOf(true, "post", "The location that this flag will spawn at."))
        .attribute("id", Attributes.id(false))
        .attribute("owner", Attributes.idOf(false, "team",
            "The owner of the objective. (This will cause the objective to not be able to be completed by this team and the objective to be grouped under this team in the sidebar.)"))
        .attribute("color", Attributes.javaDoc(false, DyeColor.class, "Color of the flag."))
        .attribute("banner", Attributes.loadout(false, "banner",
            "The banner design of the flag. If none is specified, the flag in the match world will be used."))
        .attribute("carry-check", Attributes.check(false, "before the flag can be picked up"))
        .attribute("recover-time", Attributes.duration(false, true,
            "The time after the flag is dropped before it is returned to the previous post."), 30)
        .attribute("highlight-holder", new GenericAttribute(Boolean.class, false,
                "If the flag holder should have a particle stream shooting up from their location."),
            true)
        .attribute("highlight-delay", Attributes.duration(false, false,
            "The delay before a particle stream is shown above the player who is holding the flag."))
        .attribute("permanent", new GenericAttribute(Boolean.class, false,
                "If the flag can only be captured once. If this is true and the flag is captured, it will be marked as completed."),
            false)
        .attributes(ObjectivesFactory.proximity("flag", "before the objective is carried", false,
            DistanceCalculationMetric.Type.PLAYER, true))
        .attributes(ObjectivesFactory.proximity("net", "after the objective is carried", false,
            DistanceCalculationMetric.Type.PLAYER, true))
        .attribute("carrying-points", new GenericAttribute(Integer.class, false,
            "Number of points the carrier should receive while holding the flag."), 0)
        .attribute("carrying-points-delay",
            Attributes.duration(false, false, "Delay before points should be earned."))
        .attribute("carrying-points-growth", Attributes
            .duration(false, false, "Delay before amount of points earned should be doubled."))
        .attribute("pickup-method", new EnumAttribute(FlagPickupMethod.class, false,
            "Method that can be used to pickup the flag."), FlagPickupMethod.ANY)
        .build();
  }

  @Override
  public FlagObjective build(Match match, MatchFactory factory, XmlElement element) {
    element.inheritAttributes("flags");

    PostZone post = match.getRegistry()
        .get(PostZone.class, element.getAttribute("post").asRequiredString(), true).get();

    Optional<Team> owner = Optional.empty();
    if (element.hasAttribute("owner")) {
      owner = match.getRegistry()
          .get(Team.class, element.getAttribute("owner").asRequiredString(), true);
    }

    Optional<DyeColor> color = Optional.empty();
    if (element.hasAttribute("color")) {
      color = Optional.of(element.getAttribute("color").asRequiredEnum(DyeColor.class, true));
    }

    Optional<ScopableItemStack> banner = Optional.empty();
    if (element.hasChild("banner")) {
      XmlElement el = element.getChild("banner").get();
      banner = Optional
          .of(factory.getFactory(LoadoutsFactory.class).parseItemStack(match, Material.BANNER, el));
    }

    Optional<Check> carryCheck = FactoryUtils
        .resolveCheckChild(match, element.getAttribute("carry-check"),
            element.getChild("carry-check"));

    Duration recoverTime = element.getAttribute("recover-time").asDuration()
        .orElse(Seconds.seconds(30).toStandardDuration());

    boolean highlightHolder = element.getAttribute("highlight-holder").asBoolean().orElse(true);
    Optional<Duration> highlightDelay = element.getAttribute("highlight-delay").asDuration();

    boolean permanent = element.getAttribute("permanent").asBoolean().orElse(false);

    DistanceCalculationMetric def = new DistanceCalculationMetric(
        DistanceCalculationMetric.Type.PLAYER, false);
    FlagDistanceMetrics metrics = new FlagDistanceMetrics.Builder()
        .carry(element, "net", def)
        .preComplete(element, "flag", def)
        .build();

    int carryingPoints = element.getAttribute("carrying-points").asInteger().orElse(0);
    Optional<Duration> carryingPointsDelay = element.getAttribute("carrying-points-delay")
        .asDuration();
    Optional<Duration> pointsGrowth = element.getAttribute("carrying-points-growth").asDuration();
    FlagPickupMethod pickupMethod = element.getAttribute("pickup-method")
        .asEnum(FlagPickupMethod.class, true).orElse(FlagPickupMethod.ANY);

    return new FlagObjective(match,
        metrics,
        post,
        owner,
        color,
        banner,
        carryCheck,
        recoverTime,
        highlightHolder,
        highlightDelay,
        permanent,
        carryingPoints,
        carryingPointsDelay,
        pointsGrowth,
        pickupMethod);
  }
}
