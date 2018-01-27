package net.avicus.atlas.module.projectiles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.ModuleDocumentation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.match.registry.RegisteredObject;
import net.avicus.atlas.match.registry.WeakReference;
import net.avicus.atlas.module.ModuleBuildException;
import net.avicus.atlas.module.ModuleFactory;
import net.avicus.atlas.module.ModuleFactorySort;
import net.avicus.atlas.module.loadouts.Loadout;
import net.avicus.atlas.module.loadouts.LoadoutsFactory;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.compendium.inventory.SingleMaterialMatcher;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;
import org.joda.time.Duration;

@ModuleFactorySort(ModuleFactorySort.Order.FIRST)
public class ProjectilesFactory implements ModuleFactory<ProjectilesModule> {

  @Override
  public ModuleDocumentation getDocumentation() {
    return ModuleDocumentation.builder()
        .name("Custom Projectiles")
        .tagName("projectiles")
        .category(ModuleDocumentation.ModuleCategory.COMPONENTS)
        .description(
            "Use this module to create custom projectiles. These can be referenced by id in loadouts.")
        .feature(FeatureDocumentation.builder()
            .name("Projectile")
            .tagName("projectile")
            .description("This represents a custom projectile that can be shot from bows or items.")
            .attribute("id", Attributes.id(true))
            .attribute("name", new GenericAttribute(LocalizedXmlString.class, true,
                "Name of the custom projectile for use in death messages and cool-down alerts."))
            .attribute("type", Attributes
                .javaDoc(true, EntityType.class, "The type of entity to shoot out of the player."))
            .attribute("damage", new GenericAttribute(Double.class, false,
                "Amount of damage the projectile will do when it hits the player."), 1.0)
            .attribute("velocity",
                new GenericAttribute(Double.class, false, "The velocity of the projectile."), 1.0)
            .attribute("loadout", Attributes.idOf(false, "strike loadout"))
            .attribute("cooldown", Attributes.duration(false, true,
                "The time it takes for the projectile item to be used again after use."), 0)
            .attribute("mount", new GenericAttribute(Boolean.class, false,
                "If the player should mount the projectile after it is fired."), false)
            .build())
        .build();
  }

  @Override
  public Optional<ProjectilesModule> build(Match match, MatchFactory factory, XmlElement root)
      throws ModuleBuildException {
    List<XmlElement> elements = root.getChildren("projectiles");

    elements.forEach(element -> {
      for (XmlElement child : element.getChildren()) {
        String id = child.getAttribute("id").asRequiredString();
        CustomProjectile projectile = parseProjectile(match, child);
        match.getRegistry().add(new RegisteredObject<>(id, projectile));
      }
    });

    return Optional.of(new ProjectilesModule(match));
  }

  public CustomProjectile parseProjectile(Match match, XmlElement element) {
    String name = element.getAttribute("name").asRequiredString();
    boolean throwable = element.getAttribute("throwable").asBoolean().orElse(true);
    EntityType type = element.getAttribute("type").asRequiredEnum(EntityType.class, true);
    double damage = element.getAttribute("damage").asDouble().orElse(0.0);
    double velocity = element.getAttribute("velocity").asDouble().orElse(1.0);
    Optional<WeakReference<Loadout>> loadout = Optional.empty();
    if (element.getAttribute("loadout").isValuePresent()) {
      loadout = Optional.of(match.getRegistry()
          .getReference(Loadout.class, element.getAttribute("loadout").asRequiredString()));
    }
    Optional<Duration> duration = element.getAttribute("cooldown").asDuration();
    boolean mount = element.getAttribute("mount").asBoolean().orElse(false);
    boolean sticky = element.getAttribute("sticky").asBoolean().orElse(false);
    Optional<SingleMaterialMatcher> block = element.getAttribute("block").asMaterialMatcher();

    Collection<PotionEffect> effects = new ArrayList<>();

    element.getChildren("effect").forEach(effect -> effects
        .add(match.getFactory().getFactory(LoadoutsFactory.class).parsePotionEffect(effect)));

    return new CustomProjectile(UUID.randomUUID(), name, throwable, type, damage, velocity, loadout,
        duration, mount, sticky, block, effects);
  }
}
