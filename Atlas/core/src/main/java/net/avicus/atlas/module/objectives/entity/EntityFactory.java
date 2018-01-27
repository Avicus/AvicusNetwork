package net.avicus.atlas.module.objectives.entity;

import java.util.Optional;
import net.avicus.atlas.SpecificationVersionHistory;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.SpecInformation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.locales.LocalesModule;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.module.objectives.ObjectiveFactory;
import net.avicus.atlas.module.objectives.ObjectivesFactory;
import net.avicus.atlas.module.objectives.locatable.DistanceMetrics;
import net.avicus.atlas.module.shop.PointEarnConfig;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.magma.util.distance.DistanceCalculationMetric;
import net.avicus.magma.util.region.BoundedRegion;
import org.bukkit.entity.EntityType;

public class EntityFactory implements ObjectiveFactory<EntityObjective> {

  static {
    PointEarnConfig.CONFIGURABLES.add("entity-destroy");
  }

  @Override
  public FeatureDocumentation getDocumentation() {
    return FeatureDocumentation.builder()
        .name("Entity")
        .tagName("entity")
        .specInformation(
            SpecInformation.builder().added(SpecificationVersionHistory.UNTOUCHABLE_CHAT_CHANNELS)
                .build())
        .description(
            "The entity objective is used to track the health of entities in a region and use the combined health to determine completion.")
        .attribute("id", Attributes.id(false))
        .attribute("name", new GenericAttribute(LocalizedXmlString.class, true,
            "The name of the objective (for the UI)."))
        .attribute("owner", Attributes.idOf(false, "team",
            "The owner of the objective. (This will cause the objective to not be able to be completed by this team and the objective to be grouped under this team in the sidebar.)"))
        .attributes(ObjectivesFactory
            .proximity("pre-complete", "before the objective is completed", false,
                DistanceCalculationMetric.Type.PLAYER, true))
        .attributes(ObjectivesFactory
            .proximity("post-complete", "after the objective is completed", false, null, false))
        .attribute("damage-check", Attributes.check(false, "before the objective can be damaged"))
        .attribute("points", new GenericAttribute(Number.class, false,
            "How many points to award to the team who damages this objective."))
        .attribute("show", new GenericAttribute(Boolean.class, false,
                "If the objective should be shown in the UI, count towards match results, and count for stats."),
            true)
        .attribute("region", Attributes.region(true,
            "The region that contains all entities that should be counted as this objective."))
        .attribute("type", Attributes
            .javaDoc(true, EntityType.class, "The type of entity that this objective is."))
        .build();
  }

  @Override
  public EntityObjective build(Match match, MatchFactory factory, XmlElement element) {
    element.inheritAttributes("entities");

    // name
    String rawName = element.getAttribute("name").asRequiredString();
    LocalizedXmlString name = match.getRequiredModule(LocalesModule.class).parse(rawName);

    // owner
    Optional<Competitor> owner = Optional.empty();
    if (element.hasAttribute("owner")) {
      owner = match.getRegistry()
          .get(Competitor.class, element.getAttribute("owner").asRequiredString(), false);
    }

    Optional<Check> damageCheck = FactoryUtils
        .resolveCheck(match, element.getAttribute("damage-check"),
            element.getChild("damage-check"));

    DistanceCalculationMetric def = new DistanceCalculationMetric(
        DistanceCalculationMetric.Type.PLAYER, true);
    DistanceMetrics metrics = new DistanceMetrics.Builder()
        .preComplete(element, "pre-complete", def)
        .postComplete(element, "post-complete", null)
        .build();

    // points
    Optional<Integer> points = element.getAttribute("points").asInteger();

    EntityType type = element.getAttribute("type").asRequiredEnum(EntityType.class, true);

    boolean show = element.getAttribute("show").asBoolean().orElse(true);

    // region
    BoundedRegion region = FactoryUtils
        .resolveRequiredRegionAs(match, BoundedRegion.class, element.getAttribute("region"),
            element.getChild("region"));
    return new EntityObjective(metrics, match, name, show, owner, damageCheck, region, points,
        type);
  }
}
