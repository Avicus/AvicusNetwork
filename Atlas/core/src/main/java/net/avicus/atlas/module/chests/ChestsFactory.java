package net.avicus.atlas.module.chests;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.ModuleDocumentation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.ModuleBuildException;
import net.avicus.atlas.module.ModuleFactory;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.chests.generator.ChestGenerator;
import net.avicus.atlas.module.loadouts.LoadoutsFactory;
import net.avicus.atlas.util.ScopableItemStack;
import net.avicus.atlas.util.inventory.RandomizableItemStack;
import net.avicus.atlas.util.inventory.populator.InventoryPopulator;
import net.avicus.atlas.util.inventory.populator.RandomInventoryPopulator;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.compendium.WeightedRandomizer;
import net.avicus.magma.util.region.Region;
import org.joda.time.Duration;

/**
 * Factory for the {@link ChestsModule}
 */
public class ChestsFactory implements ModuleFactory<ChestsModule> {

  @Override
  public ModuleDocumentation getDocumentation() {
    return ModuleDocumentation.builder()
        .name("Chest Refill")
        .tagName("chests")
        .description(
            "This module is used to fill and refill chests on the map based on different conditions.")
        .feature(FeatureDocumentation.builder()
            .name("Chest Generator")
            .tagName("generator")
            .attribute("region",
                Attributes.region(true, "The region to generator should search for chest in,"))
            .attribute("clear", new GenericAttribute(Boolean.class, false,
                "If the chest should be cleared before items are added."), false)
            .attribute("min", new GenericAttribute(Integer.class, true, "Minimum item stack size."))
            .attribute("max", new GenericAttribute(Integer.class, true, "Maximum item stack size."))
            .attribute("regenerate-count", new GenericAttribute(Integer.class, false,
                "Number of times regeneration should occur per chest."), 0)
            .attribute("regenerate-countdown",
                Attributes.duration(false, true, "Time between chest refills."))
            .attribute("allow-duplicates", new GenericAttribute(Boolean.class, false,
                "If duplicates should be allowed during generation."), false)
            .attribute("check", Attributes.check(false, "before items are filled"))
            .subFeature(FeatureDocumentation.builder()
                .name("Item Set")
                .tagName("chest-items")
                .description(
                    "Item sets are used to define sets of items that can be filled with a weight.")
                .attribute("min",
                    new GenericAttribute(Integer.class, false, "Minimum item stack size."))
                .attribute("max",
                    new GenericAttribute(Integer.class, false, "Maximum item stack size."))
                .attribute("weight",
                    new GenericAttribute(Double.class, true, "Weight of each item in set."))
                .build())
            .build())
        .category(ModuleDocumentation.ModuleCategory.COMPONENTS)
        .build();
  }

  @Override
  public Optional<ChestsModule> build(Match match, MatchFactory factory, XmlElement root)
      throws ModuleBuildException {
    List<XmlElement> roots = root.getChildren("chests");

    if (roots.isEmpty()) {
      return Optional.empty();
    }

    List<ChestGenerator> generators = new ArrayList<>();

    roots.forEach(element -> {
      List<XmlElement> children = element.getChildren("generator");
      for (XmlElement child : children) {
        Region region = FactoryUtils
            .resolveRequiredRegion(match, Region.class, child.getAttribute("region"),
                child.getChild("region"));
        boolean clear = child.getAttribute("clear").asBoolean().orElse(false);

        InventoryPopulator populator = RandomInventoryPopulator.INSTANCE;

        int countMin = child.getAttribute("min").asRequiredInteger();
        int countMax = child.getAttribute("max").asRequiredInteger();

        WeightedRandomizer<RandomizableItemStack> items = new WeightedRandomizer<>();

        for (XmlElement elements : child.getDescendants("chest-items")) {
          elements.inheritAttributes("chest-items");

          Optional<Integer> amountMin = elements.getAttribute("min").asInteger();
          Optional<Integer> amountMax = elements.getAttribute("max").asInteger();
          double weight = elements.getAttribute("weight").asDouble().orElse(1.0);

          for (XmlElement item : elements.getChildren("item")) {
            ScopableItemStack stack = factory.getFactory(LoadoutsFactory.class)
                .parseItemStack(match, item);
            items.set(new RandomizableItemStack(stack, amountMin, amountMax), weight);
          }
        }

        int regenerateCount = child.getAttribute("regenerate-count").asInteger().orElse(0);
        Optional<Duration> regenerateCountdown = child.getAttribute("regenerate-countdown")
            .asDuration();
        boolean allowDuplicates = child.getAttribute("allow-duplicates").asBoolean().orElse(false);

        Optional<Check> check = FactoryUtils
            .resolveCheckChild(match, child.getAttribute("check"), child.getChild("check"));
        generators.add(
            new ChestGenerator(region, clear, populator, countMin, countMax, items, check,
                regenerateCountdown, regenerateCount, allowDuplicates));
      }
    });

    if (generators.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(new ChestsModule(match, generators));
  }
}
