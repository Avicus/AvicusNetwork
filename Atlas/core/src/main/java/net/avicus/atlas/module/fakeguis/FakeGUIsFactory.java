package net.avicus.atlas.module.fakeguis;

import java.util.List;
import java.util.Optional;
import net.avicus.atlas.SpecificationVersionHistory;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.ModuleDocumentation;
import net.avicus.atlas.documentation.SpecInformation;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.module.ModuleBuildException;
import net.avicus.atlas.module.ModuleFactory;
import net.avicus.atlas.util.xml.XmlElement;

/**
 * Factory for the {@link FakeGUIsModule}
 */
public class FakeGUIsFactory implements ModuleFactory<FakeGUIsModule> {

  @Override
  public ModuleDocumentation getDocumentation() {
    return ModuleDocumentation.builder()
        .name("Fake GUIs")
        .tagName("fake-guis")
        .specInformation(
            SpecInformation.builder().added(SpecificationVersionHistory.LOADOUT_SUB_TAG).build())
        .category(ModuleDocumentation.ModuleCategory.MISC)
        .description(
            "This module is used to open fake GUIs for players in place of real ones. These GUIs are fully functional and stay open when the source block is broken. This is a useful tool to prevent griefing.")
        .feature(FeatureDocumentation.builder()
            .name("Configuration")
            .description("Configuration options for the fake GUIs module.")
            .attribute("fake-benches", new GenericAttribute(Boolean.class, false,
                    "If a fake crafting inventory should be displayed to players when they open crafting tables."),
                true)
            .attribute("fake-anvils", new GenericAttribute(Boolean.class, false,
                    "If a fake crafting inventory should be displayed to players when they open anvils."),
                false)
            .attribute("fake-enchant-tables", new GenericAttribute(Boolean.class, false,
                    "If a fake crafting inventory should be displayed to players when they open enchantment tables."),
                true)
            .build())
        .build();
  }

  @Override
  public Optional<FakeGUIsModule> build(Match match, MatchFactory factory, XmlElement root)
      throws ModuleBuildException {
    List<XmlElement> elements = root.getChildren("fake-guis");

    boolean fakeBenches = true;
    boolean fakeAnvils = false;
    boolean fakeEnchantTables = true;

    if (!elements.isEmpty()) {
      for (XmlElement element : elements) {
        fakeBenches = element.getAttribute("fake-benches").asBoolean().orElse(fakeBenches);
        fakeAnvils = element.getAttribute("fake-anvils").asBoolean().orElse(fakeAnvils);
        fakeEnchantTables = element.getAttribute("fake-enchant-tables").asBoolean()
            .orElse(fakeEnchantTables);
      }
    }

    return Optional.of(new FakeGUIsModule(match, fakeBenches, fakeEnchantTables));
  }
}
