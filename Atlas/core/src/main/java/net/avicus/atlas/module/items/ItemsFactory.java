package net.avicus.atlas.module.items;

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
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.util.xml.XmlElement;

public class ItemsFactory implements ModuleFactory<ItemsModule> {

  @Override
  public ModuleDocumentation getDocumentation() {
    return ModuleDocumentation.builder()
        .name("Remove, Repair, and Keep")
        .tagName("items")
        .description(
            "This module is used to modify which items are dropped, kept, and which tools are repaired when picked up.")
        .category(ModuleDocumentation.ModuleCategory.COMPONENTS)
        .feature(FeatureDocumentation.builder()
            .name("Remove Drops")
            .tagName("remove-drops")
            .description(
                "This feature is used to modify which items can be dropped by players/blocks.")
            .attribute("check", Attributes.check(true, "before an item is allowed to be dropped"))
            .build())
        .feature(FeatureDocumentation.builder()
            .name("Keep Items")
            .tagName("keep-items")
            .description(
                "This feature is used to modify which items are kept by players across lives.")
            .attribute("check", Attributes.check(true, "before an item is kept"))
            .build())
        .feature(FeatureDocumentation.builder()
            .name("Keep Armor")
            .tagName("keep-armor")
            .description(
                "This feature is used to modify which armor are kept by players across lives.")
            .attribute("check", Attributes.check(true, "before armor is kept"))
            .build())
        .feature(FeatureDocumentation.builder()
            .name("Repair Tools")
            .tagName("repair-tools")
            .description(
                "This feature is used to modify which tools are repaired when a player picks up a matching tool.")
            .attribute("check", Attributes.check(true, "before a tool is repaired"))
            .build())
        .build();
  }

  @Override
  public Optional<ItemsModule> build(Match match, MatchFactory factory, XmlElement root)
      throws ModuleBuildException {
    List<XmlElement> elements = root.getChildren("items");

    if (elements.isEmpty()) {
      return Optional.empty();
    }

    Optional<Check> removeDrops = Optional.empty();
    Optional<Check> keepItems = Optional.empty();
    Optional<Check> keepArmor = Optional.empty();
    Optional<Check> repairTools = Optional.empty();

    for (XmlElement element : elements) {
      if (element.getChild("remove-drops").isPresent()) {
        XmlElement child = element.getChild("remove-drops").get();
        Check check = FactoryUtils
            .resolveRequiredCheckChild(match, child.getAttribute("check"), child);
        removeDrops = Optional.of(check);
      }

      if (element.getChild("keep-items").isPresent()) {
        XmlElement child = element.getChild("keep-items").get();
        Check check = FactoryUtils
            .resolveRequiredCheckChild(match, child.getAttribute("check"), child);
        keepItems = Optional.of(check);
      }

      if (element.getChild("keep-armor").isPresent()) {
        XmlElement child = element.getChild("keep-armor").get();
        Check check = FactoryUtils
            .resolveRequiredCheckChild(match, child.getAttribute("check"), child);
        keepArmor = Optional.of(check);
      }

      if (element.getChild("repair-tools").isPresent()) {
        XmlElement child = element.getChild("repair-tools").get();
        Check check = FactoryUtils
            .resolveRequiredCheckChild(match, child.getAttribute("check"), child);
        repairTools = Optional.of(check);
      }
    }

    return Optional.of(new ItemsModule(match, removeDrops, keepItems, keepArmor, repairTools));
  }

}
