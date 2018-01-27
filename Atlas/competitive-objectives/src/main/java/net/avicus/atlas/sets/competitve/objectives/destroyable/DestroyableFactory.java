package net.avicus.atlas.sets.competitve.objectives.destroyable;

import java.util.Optional;
import net.avicus.atlas.SpecificationVersionHistory;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.SpecInformation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.documentation.attributes.RangeAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.groups.teams.Team;
import net.avicus.atlas.module.locales.LocalesModule;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.module.objectives.ObjectiveFactory;
import net.avicus.atlas.module.objectives.ObjectivesFactory;
import net.avicus.atlas.module.objectives.TouchableDistanceMetrics;
import net.avicus.atlas.sets.competitve.objectives.destroyable.leakable.LeakableObjective;
import net.avicus.atlas.sets.competitve.objectives.destroyable.monument.MonumentObjective;
import net.avicus.atlas.sets.competitve.objectives.phases.DestroyablePhase;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.atlas.util.xml.XmlException;
import net.avicus.compendium.inventory.MultiMaterialMatcher;
import net.avicus.compendium.inventory.SingleMaterialMatcher;
import net.avicus.magma.util.distance.DistanceCalculationMetric;
import net.avicus.magma.util.region.BoundedRegion;

public class DestroyableFactory implements ObjectiveFactory<DestroyableObjective> {

  @Override
  public FeatureDocumentation getDocumentation() {
    return FeatureDocumentation.builder()
        .name("Destroyables and Leakables")
        .tagName("monuments").tagName("monument")
        .tagName("leakables").tagName("leakable")
        .description(
            "Destroyable objectives count as any objective that must be completed by breaking blocks in a specified region.")
        .description(
            "Leakable objectives count as any container of blocks that contain a liquid that must be leaked out of the bottom in order for the objective to be marked as completed.")
        .attribute("id", Attributes.id(false))
        .attribute("name", new GenericAttribute(LocalizedXmlString.class, true,
            "The name of the objective (for the UI)."))
        .attribute("owner", Attributes.idOf(false, "team",
            "The owner of the objective. (This will cause the objective to not be able to be completed by this team and the objective to be grouped under this team in the sidebar.)"))
        .attribute("region", Attributes.region(true, "The region that contains this objective."))
        .attribute("materials", Attributes
            .materialMatcher(true, true, "The materials that can be broken of this objective."))
        .attribute("points", new GenericAttribute(Integer.class, false,
            "How many points to award to the team who completes this objective."))
        .attribute("points-per-block", new GenericAttribute(Integer.class, false,
            "How many points to award to the team who damages this objective."))
        .attribute("show", new GenericAttribute(Boolean.class, false,
                "If the objective should be shown in the UI, count towards match results, and count for stats."),
            true)
        .attribute("completion", new RangeAttribute(0, 1.0, true,
                "The percentage of this objective that must be broken in order for it to be considered completed."),
            1.0)
        .attribute("destroyable", new GenericAttribute(Boolean.class, false,
                "If the blocks of the objective can be destroyed using means other than by hand (TNT, fireballs, etc)."),
            false)
        .attribute("repairable", new GenericAttribute(Boolean.class, false,
                "If the objective can be repaired by the owning team using any of the specified materials."),
            false)
        .attribute("any-repair", new GenericAttribute(Boolean.class, false,
            "If any material can be used to repair this objective."), false)
        .attribute("enforce-anti-repair", new GenericAttribute(Boolean.class, false,
                "If blocks can be placed in the objective region that do not match the materials specified. It should be noted that these materials do not repair the objective and the repair check is not called."),
            false)
        .specInformation(SpecInformation.builder()
            .change(SpecificationVersionHistory.REPAIR_CHECK_DEF_OFF,
                "Objective repair rules are now ignored by default.").build())
        .attribute("fireworks", new GenericAttribute(Boolean.class, false,
            "If fireworks should shoot off of the objective when it is completed."), true)
        .attribute("break-check", Attributes.check(false, "before the objective can be damaged"))
        .attribute("repair-check", Attributes.check(false, "before the objective can be repaired"))
        .attributes(ObjectivesFactory
            .proximity("pre-touch", "before the objective is touched", false,
                DistanceCalculationMetric.Type.PLAYER, true))
        .attributes(ObjectivesFactory
            .proximity("post-touch", "after the objective is touched", false,
                DistanceCalculationMetric.Type.PLAYER, true))
        .attributes(ObjectivesFactory
            .proximity("post-complete", "after the objective is completed", false, null, false))
        .attribute("first-phase", Attributes.idOf(false, "destroyable phase",
            "The first phase that should be applied to this objective when the match starts."))
        .subFeature(FeatureDocumentation.builder()
            .name("Monuments")
            .tagName("monuments").tagName("monument")
            .description(
                "Monuments count as any objective that must be completed by breaking blocks in a specified region.")
            .attribute("completed-state", Attributes.materialMatcher(false, false,
                "The material that the blocks inside of the objective region should change to when the objective is completed."))
            .build())
        .subFeature(FeatureDocumentation.builder()
            .name("Leakables")
            .tagName("leakables").tagName("leakable")
            .description(
                "Leakables count as any container of blocks that contain a liquid that must be leaked out of the bottom in order for the objective to be marked as completed.")
            .attribute("leak-distance", new GenericAttribute(Integer.class, false,
                    "The number of blocks below the objective region that the liquid must travel in order to complete this objective."),
                7)
            .build())
        .build();
  }

  @Override
  public DestroyableObjective build(Match match, MatchFactory factory, XmlElement element) {
    element.inheritAttributes("leakables");
    element.inheritAttributes("monuments");

    // name
    String rawName = element.getAttribute("name").asRequiredString();
    LocalizedXmlString name = match.getRequiredModule(LocalesModule.class).parse(rawName);

    // owner
    Optional<Team> owner = Optional.empty();
    if (element.hasAttribute("owner")) {
      owner = match.getRegistry()
          .get(Team.class, element.getAttribute("owner").asRequiredString(), false);
    }

    // region
    BoundedRegion region = FactoryUtils
        .resolveRequiredRegionAs(match, BoundedRegion.class, element.getAttribute("region"),
            element.getChild("region"));

    // Materials
    MultiMaterialMatcher materials = element.getAttribute("materials")
        .asRequiredMultiMaterialMatcher();

    // points
    Optional<Integer> points = element.getAttribute("points").asInteger();

    // points-per-block
    Optional<Integer> pointsPerBlock = element.getAttribute("points-per-block").asInteger();

    double completion = element.getAttribute("completion").asDouble().orElse(1.0);

    boolean destroyable = element.getAttribute("destroyable").asBoolean().orElse(false);

    // repairable
    boolean repairable = element.getAttribute("repairable").asBoolean().orElse(false);

    boolean enforceAntiRepair = true;
    // enforce-anti-repair
    if (match.getMap().getSpecification()
        .greaterEqual(SpecificationVersionHistory.REPAIR_CHECK_DEF_OFF)) {
      enforceAntiRepair = element.getAttribute("enforce-anti-repair").asBoolean().orElse(false);
    } else {
      match.warnDeprecation("Destroyables now default to ignoring all repair rules unless defined",
          SpecificationVersionHistory.REPAIR_CHECK_DEF_OFF);
    }

    // fireworks
    boolean fireworks = element.getAttribute("fireworks").asBoolean().orElse(true);

    // break-check
    Optional<Check> breakCheck = FactoryUtils
        .resolveCheckChild(match, element.getAttribute("break-check"),
            element.getChild("break-check"));

    // repair-check
    Optional<Check> repairCheck = FactoryUtils
        .resolveCheckChild(match, element.getAttribute("repair-check"),
            element.getChild("repair-check"));

    // any-repair
    boolean anyRepair = element.getAttribute("any-repair").asBoolean().orElse(false);

    DistanceCalculationMetric def = new DistanceCalculationMetric(
        DistanceCalculationMetric.Type.PLAYER, true);
    TouchableDistanceMetrics metrics = new TouchableDistanceMetrics.Builder()
        .preComplete(element, "pre-touch", def)
        .postTouch(element, "post-touch", def)
        .postComplete(element, "post-complete", null)
        .build();

    // phase
    Optional<DestroyablePhase> phase = Optional.empty();
    if (element.hasAttribute("first-phase")) {
      phase = match.getRegistry()
          .get(DestroyablePhase.class, element.getAttribute("first-phase").asRequiredString(),
              true);
    }

    switch (element.getName()) {
      case "leakable":
        // leak-distance
        int leak = element.getAttribute("leak-distance").asInteger().orElse(7);
        return new LeakableObjective(match, metrics, name, owner, region, points, pointsPerBlock,
            materials, destroyable, repairable, enforceAntiRepair, fireworks, breakCheck,
            repairCheck, completion, anyRepair, phase, leak);
      case "monument":
        // completed-state
        Optional<SingleMaterialMatcher> completedState = element.getAttribute("completed-state")
            .asMaterialMatcher();
        return new MonumentObjective(match, metrics, name, owner, region, points, pointsPerBlock,
            materials, destroyable, repairable, enforceAntiRepair, fireworks, breakCheck,
            repairCheck, completedState, completion, anyRepair, phase);
      default:
        throw new XmlException(element, "Invalid destroyable type specified.");
    }
  }
}
