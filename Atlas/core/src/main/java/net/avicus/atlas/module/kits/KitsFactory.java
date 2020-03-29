package net.avicus.atlas.module.kits;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.avicus.atlas.SpecificationVersionHistory;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.ModuleDocumentation;
import net.avicus.atlas.documentation.SpecInformation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.documentation.attributes.RangeAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.ModuleFactory;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.kits.abilities.DoubleJumpAbility;
import net.avicus.atlas.module.kits.abilities.HealthEffectAbility;
import net.avicus.atlas.module.loadouts.Loadout;
import net.avicus.atlas.module.loadouts.LoadoutsFactory;
import net.avicus.atlas.module.locales.LocalesModule;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.util.ScopableItemStack;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.atlas.util.xml.XmlException;
import net.avicus.atlas.util.xml.named.NamedParser;
import net.avicus.atlas.util.xml.named.NamedParsers;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class KitsFactory implements ModuleFactory<KitsModule> {

  public final static Table<Object, Method, Collection<String>> NAMED_PARSERS = HashBasedTable
      .create();

  public final static List<FeatureDocumentation> FEATURES = Lists.newArrayList();

  public KitsFactory() {
    NAMED_PARSERS.row(this).putAll(NamedParsers.methods(KitsFactory.class));

    FEATURES.add(FeatureDocumentation.builder()
        .name("Double Jump Ability")
        .tagName("double-jump")
        .description("This ability allows the player to jump higher than normal.")
        .attribute("push",
            new GenericAttribute(Double.class, true, "How far forward the player should go."))
        .attribute("icarus",
            new GenericAttribute(Double.class, true, "How high the player should go."))
        .build());

    FEATURES.add(FeatureDocumentation.builder()
        .name("Health Effect Ability")
        .tagName("health-effect")
        .description(
            "This ability applies a potion effect to a player when they reach a certain health level.")
        .attribute("health",
            new RangeAttribute(0, 20, true, "Health level that will trigger the effect."))
        .attribute("effect",
            Attributes.javaDoc(true, PotionEffectType.class, "Effect to apply to the player."))
        .build());
  }

  @Override
  public ModuleDocumentation getDocumentation() {
    ModuleDocumentation.ModuleDocumentationBuilder builder = ModuleDocumentation.builder();

    builder.name("Kits")
        .tagName("kits")
        .description(
            "Kits are used to apply special abilities to players and let then choose special loadouts.")
        .category(ModuleDocumentation.ModuleCategory.COMPONENTS);

    builder.feature(FeatureDocumentation.builder()
        .name("Kits")
        .tagName("kit")
        .description(
            "Kits give players special abilities and allow them to choose loadouts based on checks.")
        .specInformation(SpecInformation.builder()
            .breakingChange(SpecificationVersionHistory.KIT_IDS_REQUIRED,
                "Kit IDS are now required.").build())
        .attribute("id", Attributes.id(true))
        .attribute("name",
            new GenericAttribute(LocalizedXmlString.class, true, "The name of the kit."))
        .attribute("description", new GenericAttribute(LocalizedXmlString.class, false,
            "The description of the kit (for the menu)."))
        .attribute("icon",
            Attributes.loadout(true, "item stack", "The icon of the kit for use in the menu."))
        .attribute("loadout", Attributes.idOf(false, "loadout"))
        .attribute("check", Attributes.check(false,
            "on the player before they can view or select the kit. If a player respawns with the kit and check fails, they will be given the default kit if there is one, or have no kit"))
        .attribute("default", new GenericAttribute(Boolean.class, false,
                "If this kit should be the default kit which is applied to players who do not choose a kit."),
            true)
        .build())
        .feature(FeatureDocumentation.builder()
            .name("Kit Permissions")
            .tagName("permissions")
            .description(
                "This is used to give minecraft permissions to players who currently have this kit.")
            .text(new GenericAttribute(String.class, true, "The permission to add to the player"))
            .attribute("value",
                new GenericAttribute(Boolean.class, false, "The value of the permission.",
                    "Setting this to false is the equivalent of adding a `-` before the permission when using permissions plugins."),
                true)
            .build());

    FEATURES.forEach(builder::feature);

    return builder.build();
  }

  @Override
  public Optional<KitsModule> build(Match match, MatchFactory factory, XmlElement root) {
    List<XmlElement> elements = root.getChildren("kits");

    if (elements.isEmpty()) {
      return Optional.empty();
    }

    Optional<Kit> defaultKit = Optional.empty();
    List<Kit> kits = new ArrayList<>();

    for (XmlElement element : elements) {
      for (XmlElement child : element.getChildren("kit")) {
        // ID
        String id = UUID.randomUUID().toString();
        if (match.getMap().getSpecification()
            .greaterEqual(SpecificationVersionHistory.KIT_IDS_REQUIRED)) {
          id = child.getAttribute("id").asRequiredString();
        } else {
          match.warnDeprecation("Kits now require IDs",
              SpecificationVersionHistory.KIT_IDS_REQUIRED);
        }

        // Name
        String nameRaw = child.getAttribute("name").asRequiredString();
        LocalizedXmlString name = match.getRequiredModule(LocalesModule.class).parse(nameRaw);

        // Description
        Optional<LocalizedXmlString> description = Optional.empty();
        String descRaw = child.getAttribute("description").asString().orElse(null);
        if (descRaw != null) {
          description = Optional
              .of(match.getRequiredModule(LocalesModule.class).parse(descRaw));
        }

        // Display
        ScopableItemStack icon = match.getFactory().getFactory(LoadoutsFactory.class)
            .parseItemStack(match, child.getRequiredChild("icon"));

        // Loadout
        Optional<Loadout> loadout = FactoryUtils
            .resolveLoadout(match, child.getAttribute("loadout"), child.getChild("loadout"));

        // Abilities
        List<KitAbility> abilities = new ArrayList<>();
        if (child.hasChild("abilities")) {
          for (XmlElement el : child.getRequiredChild("abilities").getChildren()) {
            abilities.add(parseKitAbility(match, el));
          }
        }

        // Permissions
        List<KitPermission> permissions = new ArrayList<>();
        if (child.hasChild("permissions")) {
          for (XmlElement nodeElement : child.getRequiredChild("permissions").getChildren("node")) {
            String node = nodeElement.getText().asRequiredString();
            boolean value = nodeElement.getAttribute("value").asBoolean().orElse(true);
            permissions.add(new KitPermission(node, value));
          }
        }

        // Check
        Optional<Check> check = FactoryUtils
            .resolveCheckChild(match, child.getAttribute("check"), child.getChild("check"));

        Kit kit = new Kit(id, check, name, description, icon, loadout, abilities, permissions);

        if (match.getMap().getSpecification()
            .greaterEqual(SpecificationVersionHistory.KIT_IDS_REQUIRED)) {
          match.getRegistry().add(kit);
        }

        if (child.hasAttribute("default")) {
          // Prevent multiple default definitions
          if (defaultKit.isPresent()) {
            throw new XmlException(child, "Multiple default kits defined.");
          }

          defaultKit = Optional.of(kit);
        }

        kits.add(kit);
      }
    }

    return Optional.of(new KitsModule(match, defaultKit, kits));
  }

  private KitAbility parseKitAbility(Match match, XmlElement element) {
    return NamedParsers.invokeMethod(NAMED_PARSERS, element, "Unknown kit ability specified.",
        new Object[]{match, element});
  }

  @NamedParser("double-jump")
  private DoubleJumpAbility parseDoubleJumpAbility(Match match, XmlElement element) {
    double push = element.getAttribute("push").asRequiredDouble();
    double icarus = element.getAttribute("icarus").asRequiredDouble();

    return new DoubleJumpAbility(match, push, icarus);
  }

  @NamedParser("health-effect")
  private HealthEffectAbility parseHealthEffectAbility(Match match, XmlElement element) {
    PotionEffect effect = match.getFactory().getFactory(LoadoutsFactory.class)
        .parsePotionEffect(element);
    double health = element.getAttribute("health").asRequiredDouble();

    return new HealthEffectAbility(match, effect, health);
  }
}
