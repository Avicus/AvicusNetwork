package net.avicus.atlas.module.modifydamage;

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
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.compendium.number.PreparedNumberAction;
import org.bukkit.event.entity.EntityDamageEvent;

public class ModifyDamageFactory implements ModuleFactory<ModifyDamageModule> {

  @Override
  public ModuleDocumentation getDocumentation() {
    return ModuleDocumentation.builder()
        .category(ModuleDocumentation.ModuleCategory.MISC)
        .name("Damage Modification")
        .tagName("modify-damage")
        .description(
            "This module is used to modify the amount of damage caused by an action based on a check.")
        .feature(FeatureDocumentation.builder()
            .name("Modifier")
            .tagName("mod")
            .description(
                "This represents a check in relation to a damage type with a modifer and number action.")
            .description("A specific damage type or `all` can be used to match all damage events.")
            .attribute("mod", new GenericAttribute(Double.class, true,
                "The amount of damage that should be used with the number action."))
            .attribute("action", Attributes.action(true, "damage caused by the event"))
            .text(Attributes
                .javaDoc(true, EntityDamageEvent.DamageCause.class, "The damage type to modify."))
            .attribute("check", Attributes.check(true, "before the damage is modified"))
            .build())
        .build();
  }

  @Override
  public Optional<ModifyDamageModule> build(Match match, MatchFactory factory, XmlElement root)
      throws ModuleBuildException {
    if (!root.hasChild("modify-damage")) {
      return Optional.empty();
    }

    List<ModifyDamageModule.DamageModifier> damageModifiers = new ArrayList<>();

    root.getChildren("modify-damage").stream().flatMap(e -> e.getChildren("mod").stream())
        .forEach(e -> {
          PreparedNumberAction action = new PreparedNumberAction(
              e.getAttribute("mod").asRequiredDouble(),
              e.getAttribute("action").asRequiredNumberAction());
          Check check = FactoryUtils
              .resolveRequiredCheckChild(match, e.getAttribute("check"), e.getChild("check"));
          if (e.getText().asRequiredString().equals("all")) {
            for (EntityDamageEvent.DamageCause cause : EntityDamageEvent.DamageCause.values()) {
              damageModifiers.add(new ModifyDamageModule.DamageModifier(cause, action, check));
            }
          } else {
            EntityDamageEvent.DamageCause cause = e.getText()
                .asRequiredEnum(EntityDamageEvent.DamageCause.class, true);
            damageModifiers.add(new ModifyDamageModule.DamageModifier(cause, action, check));
          }
        });

    return Optional.of(new ModifyDamageModule(match, damageModifiers));
  }

}
