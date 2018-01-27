package net.avicus.atlas.sets.competitve.objectives.phases;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.ModuleDocumentation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.Module;
import net.avicus.atlas.module.ModuleBuildException;
import net.avicus.atlas.module.ModuleFactory;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.locales.LocalesModule;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.compendium.inventory.MultiMaterialMatcher;
import net.avicus.compendium.inventory.SingleMaterialMatcher;
import org.joda.time.Duration;

/**
 * Factory for the parsing and storing of {@link DestroyablePhase Destroyable Phases} that can be
 * used in this match.
 */
public class PhasesFactory implements ModuleFactory<Module> {

  @Override
  public ModuleDocumentation getDocumentation() {
    return ModuleDocumentation.builder()
        .name("Destroyable Phases")
        .tagName("phases")
        .category(ModuleDocumentation.ModuleCategory.COMPONENTS)
        .description(
            "This module is used to automatically change the blocks of (and needed to repair) Destroyable objectives based on a delay and check.")
        .feature(FeatureDocumentation.builder()
            .name("Phase")
            .tagName("phase")
            .description(
                "A phase is a step in the progession of different materials for a destroyable objective.")
            .attribute("id", Attributes.id(true))
            .attribute("name", new GenericAttribute(LocalizedXmlString.class, true,
                "The name of the phase (for the UI)."))
            .attribute("countdown-message", new GenericAttribute(LocalizedXmlString.class, true,
                "The message that is used for the countdown that is displayed in chat and on the boss bar. ",
                "FORMAT: {0} = the name of the phase, {1} = the amount of time until the phase will be applied."))
            .attribute("success-message", new GenericAttribute(LocalizedXmlString.class, true,
                "The message that is displayed when the phase is successfully applied."))
            .attribute("fail-message", new GenericAttribute(LocalizedXmlString.class, true,
                "The message that is displayed when the phase fails to be applied."))
            .attribute("delay",
                Attributes.duration(true, true, "Delay before the phase is applied."))
            .attribute("check", Attributes.check(false, "before the phase is applied."))
            .attribute("retry-attempts", new GenericAttribute(Integer.class, false,
                    "Number of times the phase should be attempted to be re-applied if the check fails."),
                0)
            .attribute("retry-delay",
                Attributes.duration(false, false, "Delay between retry attempts."))
            .attribute("pass-phase",
                Attributes.idOf(true, "phase", "Phase that will be applied if the check passes."))
            .attribute("fail-phase", Attributes.idOf(true, "phase",
                "Phase that will be applied if the check fails and there are no retry attempts remaining."))
            .subFeature(FeatureDocumentation.builder()
                .name("Find/Replace")
                .tagName("materials")
                .attribute("find",
                    Attributes.materialMatcher(true, true, "Materials to search for."))
                .attribute("replace", Attributes
                    .materialMatcher(true, false, "Material to replace the found materials with."))
                .build())
            .build())
        .build();
  }

  @Override
  public Optional<Module> build(Match match, MatchFactory factory, XmlElement root)
      throws ModuleBuildException {
    if (root.hasChild("phases")) {
      // Copy children for safety reasons.
      List<XmlElement> childrenCopy = new ArrayList<XmlElement>();
      childrenCopy.addAll(root.getChildren("phases").stream().flatMap(e -> e.getChildren().stream())
          .collect(Collectors.toList()));
            /*
                We reverse the children so that the XML can be written in logical order but loaded in reverse so
                that earlier phases can reference later phases.
             */
      Collections.reverse(childrenCopy);

      childrenCopy.forEach(element -> match.getRegistry().add(parsePhase(match, element)));
    }

    return Optional.empty();
  }

  /**
   * Parse a {@link DestroyablePhase} from an XML element.
   *
   * @param match match the phase will be used in
   * @param element element of the phase
   * @return the parsed phase
   */
  public DestroyablePhase parsePhase(Match match, XmlElement element) {
    element.inheritAttributes("phases");
    String id = element.getAttribute("id").asRequiredString();

    String nameRaw = element.getText().asRequiredString();
    LocalizedXmlString name = match.getRequiredModule(LocalesModule.class).parse(nameRaw);

    String countdownRaw = element.getAttribute("countdown-message").asRequiredString();
    LocalizedXmlString countdown = match.getRequiredModule(LocalesModule.class).parse(countdownRaw);

    String successRaw = element.getAttribute("success-message").asRequiredString();
    LocalizedXmlString success = match.getRequiredModule(LocalesModule.class).parse(successRaw);

    String failRaw = element.getAttribute("fail-message").asRequiredString();
    LocalizedXmlString fail = match.getRequiredModule(LocalesModule.class).parse(failRaw);

    LinkedHashMap<MultiMaterialMatcher, SingleMaterialMatcher> materials = new LinkedHashMap<>();

    element.getRequiredChild("materials").getChildren().forEach(child -> {
      MultiMaterialMatcher find = child.getAttribute("find").asRequiredMultiMaterialMatcher();
      SingleMaterialMatcher replace = child.getAttribute("replace").asRequiredMaterialMatcher();
      materials.put(find, replace);
    });

    Duration delay = element.getAttribute("delay").asRequiredDuration();

    Optional<Check> check = FactoryUtils
        .resolveCheckChild(match, element.getAttribute("check"), element.getChild("check"));

    int retry = element.getAttribute("retry-attempts").asInteger().orElse(0);

    Optional<Duration> retryDelay = element.getAttribute("retry-delay").asDuration();

    Optional<DestroyablePhase> passPhase = Optional.empty();
    if (element.hasAttribute("pass-phase")) {
      passPhase = match.getRegistry()
          .get(DestroyablePhase.class, element.getAttribute("pass-phase").asRequiredString(), true);
    }

    Optional<DestroyablePhase> failPhase = Optional.empty();
    if (element.hasAttribute("fail-phase")) {
      failPhase = match.getRegistry()
          .get(DestroyablePhase.class, element.getAttribute("fail-phase").asRequiredString(), true);
    }

    return new DestroyablePhase(match, id, name, countdown, success, fail, materials, delay, check,
        retry, retryDelay, failPhase, passPhase);
  }
}
