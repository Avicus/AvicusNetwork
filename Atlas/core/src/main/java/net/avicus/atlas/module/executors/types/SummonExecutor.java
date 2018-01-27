package net.avicus.atlas.module.executors.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import lombok.Data;
import lombok.ToString;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.EnumAttribute;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.GroupVariable;
import net.avicus.atlas.module.executors.Executor;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.loadouts.LoadoutsFactory;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.atlas.util.xml.XmlException;
import net.avicus.magma.util.region.BoundedRegion;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

/**
 * An executor that summons entities into the world with specified attributes.
 */
@ToString(exclude = "match")
public class SummonExecutor extends Executor {

  private static final Random random = new Random();

  private final Match match;
  private final EntityType toSummon;
  private final Optional<String> customName;
  private final HashMap<EquipmentSlot, ItemStack> equipment;
  private final boolean dropEquipment;
  private final List<PotionEffect> effects;
  private final BoundedRegion location;
  private
  @Nullable
  final ItemStack material;
  private final int count;

  private final Optional<FireworkMetaWrapper> fireworkMetaWrapper;

  public SummonExecutor(String id,
      Check check,
      Match match,
      EntityType toSummon,
      Optional<String> customName,
      HashMap<EquipmentSlot, ItemStack> equipment,
      boolean dropEquipment,
      List<PotionEffect> effects,
      BoundedRegion location,
      ItemStack material,
      int count,
      Optional<FireworkMetaWrapper> fireworkMetaWrapper) {
    super(id, check);
    this.match = match;
    this.toSummon = toSummon;
    this.customName = customName;
    this.equipment = equipment;
    this.dropEquipment = dropEquipment;
    this.effects = effects;
    this.location = location;
    this.material = material;
    this.count = count;
    this.fireworkMetaWrapper = fireworkMetaWrapper;
  }

  public static Executor parse(Match match, XmlElement element) throws XmlException {
    Check check = FactoryUtils
        .resolveRequiredCheckChild(match, element.getAttribute("check"), element.getChild("check"));
    String id = element.getAttribute("id").asString().orElse(UUID.randomUUID().toString());
    EntityType type = element.getAttribute("type").asRequiredEnum(EntityType.class, true);
    Optional<String> customName = element.getAttribute("name").asString();

    LoadoutsFactory factory = match.getFactory().getFactory(LoadoutsFactory.class);
    HashMap<EquipmentSlot, ItemStack> equipment = new HashMap<>();
    element.getChild("equipment").ifPresent(child -> {
      child.getChildren().forEach(equipmentElement -> {
        EquipmentSlot slot = equipmentElement.getAttribute("slot")
            .asRequiredEnum(EquipmentSlot.class, true);
        ItemStack stack = factory.parseItemStack(match, equipmentElement).getItemStack();
        equipment.put(slot, stack);
      });
    });
    boolean dropEquipment = element.getAttribute("drop-equipment").asBoolean().orElse(false);
    List<PotionEffect> effects = new ArrayList<>();
    element.getChild("effects").ifPresent(child -> {
      child.getChildren().forEach(effectElement -> {
        PotionEffect effect = factory.parsePotionEffect(effectElement);
        effects.add(effect);
      });
    });
    BoundedRegion location = FactoryUtils
        .resolveRequiredRegionAs(match, BoundedRegion.class, element.getAttribute("region"),
            element.getChild("region"));
    int count = element.getAttribute("count").asInteger().orElse(1);

    Optional<FireworkMetaWrapper> meta = Optional.empty();

    ItemStack material = null;

    if (type.equals(EntityType.FIREWORK) && element.hasChild("firework")) {
      List<FireworkEffect> fireworkEffects = new ArrayList<>();
      element.getRequiredChild("firework").getChildren("effect").forEach(effectChild -> {
        FireworkEffect.Builder builder = FireworkEffect.builder();
        FireworkEffect.Type effectType = effectChild.getAttribute("effect")
            .asRequiredEnum(FireworkEffect.Type.class, true);
        builder.with(effectType);
        effectChild.getAttribute("flicker").asBoolean().ifPresent(builder::flicker);
        effectChild.getAttribute("trail").asBoolean().ifPresent(builder::trail);
        effectChild.getAttribute("color").asColor().ifPresent(builder::withColor);
        effectChild.getAttribute("fade").asColor().ifPresent(builder::withFade);
        fireworkEffects.add(builder.build());
      });
      int power = element.getRequiredChild("firework").getAttribute("power").asInteger().orElse(2);
      boolean groupColor = element.getRequiredChild("firework").getAttribute("team-color")
          .asBoolean().orElse(false);
      boolean groupFadeColor = element.getRequiredChild("firework").getAttribute("team-color-fade")
          .asBoolean().orElse(false);
      meta = Optional
          .of(new FireworkMetaWrapper(fireworkEffects, power, groupColor, groupFadeColor));
    } else if (type.equals(EntityType.DROPPED_ITEM) && element.hasChild("item")) {
      material = factory.parseItemStack(match, element.getRequiredChild("item")).getItemStack();
    } else if (type.equals(EntityType.SPLASH_POTION) && element.hasChild("potion")) {
      ItemStack stack = new ItemStack(Material.POTION);
      stack.setItemMeta(factory
          .parsePotion(match, stack, stack.getItemMeta(), element.getRequiredChild("potion")));
      material = stack;
    }

    return new SummonExecutor(id, check, match, type, customName, equipment, dropEquipment, effects,
        location, material, count, meta);
  }

  public static FeatureDocumentation documentation() {
    return FeatureDocumentation.builder()
        .name("Summon Entity")
        .tagName("summon-entity")
        .description("An executor that summons entities into the world with specified attributes.")
        .attribute("type",
            Attributes.javaDoc(true, EntityType.class, "Type of entity to be spawned."))
        .attribute("name", new GenericAttribute(String.class, false, "Custom name for the entity."))
        .attribute("effects",
            Attributes.loadout(false, "effects", "Potion effects to apply to the entity."))
        .attribute("count", new GenericAttribute(Integer.class, false,
            "Number of entities that should be spawned."), 1)
        .attribute("region", Attributes.region(false, "Region this executor acts inside of."))
        .subFeature(FeatureDocumentation.builder()
            .tagName("equipment")
            .name("Equipment Attributes")
            .description("Attributes used to configure entity equipment.")
            .attribute("slot",
                new EnumAttribute(EquipmentSlot.class, true, "Slot to place the item in."))
            .attribute("item", Attributes.loadout(true, "an item"))
            .build())
        .subFeature(FeatureDocumentation.builder()
            .tagName("effects")
            .name("Firework Attributes")
            .description("Attributes used to configure fireworks which are spawned.")
            .attribute("power",
                new GenericAttribute(Number.class, false, "The power of the firework."), 1)
            .attribute("team-color", new GenericAttribute(Boolean.class, false,
                    "If the team color (if it can be inferred) should be a part of the firework."),
                false)
            .attribute("team-color-fade", new GenericAttribute(Boolean.class, false,
                    "If the team color (if it can be inferred) should be a part of the firework's fade."),
                false)
            .subFeature(FeatureDocumentation.builder()
                .name("Firework Effect Attributes")
                .tagName("effect")
                .description(
                    "Attributes used to configure firework effects for fireworks which are spawned.")
                .attribute("effect", new EnumAttribute(FireworkEffect.Type.class, true,
                    "Type of effect to be applied."))
                .attribute("flicker",
                    new GenericAttribute(Boolean.class, false, "If the effect should flicker."),
                    false)
                .attribute("trail", new GenericAttribute(Boolean.class, false,
                    "If the effect should have a trail."), false)
                .attribute("color", Attributes.color(false, "The color of the effect."))
                .attribute("fade",
                    new GenericAttribute(Boolean.class, false, "If the effect should fade."), false)
                .build())
            .build())
        .build();
  }

  @Override
  public void execute(CheckContext context) {
    List<Entity> spawned = new ArrayList<>();

    for (int i = 1; i <= this.count; i++) {
      Vector spawnLocation = this.location.getRandomPosition(random);
      if (this.toSummon == EntityType.DROPPED_ITEM) {
        spawned.add(this.match.getWorld()
            .dropItem(spawnLocation.toLocation(this.match.getWorld()), this.material));
      } else {
        spawned.add(this.match.getWorld()
            .spawnEntity(spawnLocation.toLocation(this.match.getWorld()), this.toSummon));
      }
    }

    spawned.forEach(entity -> {
      if (this.customName.isPresent()) {
        entity.setCustomNameVisible(true);
        entity.setCustomName(this.customName.get());
      }
      if (entity instanceof LivingEntity) {
        EntityEquipment equipment = ((LivingEntity) entity).getEquipment();
        this.equipment.forEach((slot, stack) -> {
          switch (slot) {
            case HAND:
              equipment.setItemInHand(stack);
              break;
            case HEAD:
              equipment.setHelmet(stack);
              break;
            case CHEST:
              equipment.setChestplate(stack);
              break;
            case LEGS:
              equipment.setLeggings(stack);
              break;
            case FEET:
              equipment.setLeggings(stack);
              break;
          }
        });
        if (!this.dropEquipment) {
          equipment.setBootsDropChance(0);
          equipment.setLeggingsDropChance(0);
          equipment.setChestplateDropChance(0);
          equipment.setHelmetDropChance(0);
          equipment.setItemInHandDropChance(0);
        }
        this.effects.forEach(((LivingEntity) entity)::addPotionEffect);
      } else if (entity instanceof Firework && this.fireworkMetaWrapper.isPresent()) {
        Group group = context.getLast(GroupVariable.class).map(GroupVariable::getGroup)
            .orElse(null);
        FireworkMeta fireworkMeta = ((Firework) entity).getFireworkMeta();
        FireworkMetaWrapper wrapper = this.fireworkMetaWrapper.get();
        if ((wrapper.isGroupColor() || wrapper.isGroupFadeColor()) && group != null) {
          Color color = group.getFireworkColor();
          if (wrapper.isGroupFadeColor()) {
            fireworkMeta.addEffect(FireworkEffect.builder().withFade(color).build());
          }
          if (wrapper.isGroupColor()) {
            fireworkMeta.addEffect(FireworkEffect.builder().withColor(color).build());
          }
        }
        fireworkMeta.addEffects(wrapper.getEffects());
        fireworkMeta.setPower(wrapper.getPower());
        ((Firework) entity).setFireworkMeta(fireworkMeta);
      } else if (entity instanceof ThrownPotion && this.material != null) {
        ((ThrownPotion) entity).setItem(this.material);
      }
    });
  }

  @Data
  private static class FireworkMetaWrapper {

    private final List<FireworkEffect> effects;
    private final int power;
    private final boolean groupColor;
    private final boolean groupFadeColor;
  }
}
