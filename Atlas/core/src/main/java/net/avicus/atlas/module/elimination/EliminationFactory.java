package net.avicus.atlas.module.elimination;

import java.util.List;
import java.util.Optional;
import net.avicus.atlas.SpecificationVersionHistory;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.ModuleDocumentation;
import net.avicus.atlas.documentation.SpecInformation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.ModuleBuildException;
import net.avicus.atlas.module.ModuleFactory;
import net.avicus.atlas.module.ModuleFactorySort;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.elimination.executor.ModifyLivesExecutor;
import net.avicus.atlas.module.elimination.executor.ToggleEliminationExecutor;
import net.avicus.atlas.module.executors.ExecutorsFactory;
import net.avicus.atlas.module.spawns.SpawnsModule;
import net.avicus.atlas.util.xml.XmlElement;

@ModuleFactorySort(ModuleFactorySort.Order.LATE)
public class EliminationFactory implements ModuleFactory<EliminationModule> {

  static {
    ExecutorsFactory.registerDocumentation(() -> FeatureDocumentation.builder()
        .requirement(EliminationFactory.class)
        .name("Modify Lives")
        .tagName("modify-lives")
        .description(
            "An executor that is used to modify the amount of lives a player has remaining.")
        .attribute("action", Attributes.action(true, "number of lives the player has"))
        .attribute("amount",
            new GenericAttribute(Integer.class, false, "Amount of lives to modify."), 1)
        .build());
    ExecutorsFactory.registerDocumentation(() -> FeatureDocumentation.builder()
        .name("Toggle Elimination")
        .tagName("toggle-elimination")
        .description("An executor used to toggle elimination on/off for a match.")
        .attribute("toggle", new GenericAttribute(Boolean.class, false,
            "What elimination should be set to. If this is not specified, it will just be toggled."))
        .requirement(EliminationFactory.class)
        .build());
  }

  @Override
  public ModuleDocumentation getDocumentation() {
    return ModuleDocumentation.builder()
        .category(ModuleDocumentation.ModuleCategory.COMPONENTS)
        .name("Elimination")
        .tagName("elimination")
        .specInformation(SpecInformation.builder()
            .change(SpecificationVersionHistory.UNTOUCHABLE_CHAT_CHANNELS,
                "Auto-respawn is automatically enabled on elimination maps to prevent players from prolonging the match")
            .build())
        .description(
            "The elimination module is used to remove players from matches after a certain amount of deaths.")
        .description(
            "It should be noted that auto-respawn is automatically enabled on elimination maps to prevent players from prolonging the match.")
        .feature(FeatureDocumentation.builder()
            .name("Configuration")
            .description("The main configuration for the elimination module.")
            .attribute("lives", new GenericAttribute(Integer.class, false,
                "The amount of deaths a player is allowed before they are eliminated."), 1)
            .attribute("enable-on-start", new GenericAttribute(Boolean.class, false,
                    "If elimination should be enabled when the match starts. If this is false, the module will need to be enabled with an executor."),
                true)
            .attribute("strike-on-eliminate", new GenericAttribute(Boolean.class, false,
                    "If lightning should strike at the location a player dies when they are eliminated."),
                false)
            .attribute("check",
                Attributes.check(false, "before a life is subtracted due to a death"))
            .build())
        .build();
  }

  @Override
  public Optional<EliminationModule> build(Match match, MatchFactory factory, XmlElement root)
      throws ModuleBuildException {
    List<XmlElement> elements = root.getChildren("elimination");

    if (elements.isEmpty()) {
      return Optional.empty();
    }

    int lives = 1;
    boolean enabled = true;
    boolean strikeOnEliminate = false;
    Optional<Check> check = Optional.empty();

    for (XmlElement element : elements) {
      if (element.hasAttribute("lives")) {
        lives = element.getAttribute("lives").asRequiredInteger();
      }
      if (element.hasAttribute("enable-on-start")) {
        enabled = element.getAttribute("enable-on-start").asRequiredBoolean();
      }
      if (element.hasAttribute("strike-on-eliminate")) {
        strikeOnEliminate = element.getAttribute("strike-on-eliminate").asRequiredBoolean();
      }
      if (element.hasAttribute("check") || element.hasChild("check")) {
        check = FactoryUtils
            .resolveCheck(match, element.getAttribute("check"), element.getChild("check"));
      }
    }

    if (match.getMap().getSpecification()
        .greaterEqual(SpecificationVersionHistory.UNTOUCHABLE_CHAT_CHANNELS)) {
      SpawnsModule spawns = match.getRequiredModule(SpawnsModule.class);
      if (!spawns.isAutoRespawn()) {
        spawns.setAutoRespawn(true);
      }
    }

    match.getFactory().getFactory(ExecutorsFactory.class)
        .registerExecutor("modify-lives", ModifyLivesExecutor::parse);
    match.getFactory().getFactory(ExecutorsFactory.class)
        .registerExecutor("toggle-elimination", ToggleEliminationExecutor::parse);

    return Optional.of(new EliminationModule(match, lives, enabled, strikeOnEliminate, check));
  }
}
