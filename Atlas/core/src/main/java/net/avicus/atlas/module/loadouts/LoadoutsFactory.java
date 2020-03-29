package net.avicus.atlas.module.loadouts;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import net.avicus.atlas.SpecificationVersionHistory;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.ModuleDocumentation;
import net.avicus.atlas.documentation.SpecInformation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.EnumAttribute;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.map.author.Minecrafter;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.match.registry.RegisteredObject;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.Module;
import net.avicus.atlas.module.ModuleBuildException;
import net.avicus.atlas.module.ModuleFactory;
import net.avicus.atlas.module.ModuleFactorySort;
import net.avicus.atlas.module.executors.ExecutorsFactory;
import net.avicus.atlas.module.loadouts.type.CompassLoadout;
import net.avicus.atlas.module.loadouts.type.DisguiseLoadout;
import net.avicus.atlas.module.loadouts.type.EffectLoadout;
import net.avicus.atlas.module.loadouts.type.FoodLoadout;
import net.avicus.atlas.module.loadouts.type.GlowLoadout;
import net.avicus.atlas.module.loadouts.type.HealthLoadout;
import net.avicus.atlas.module.loadouts.type.ItemLoadout;
import net.avicus.atlas.module.loadouts.type.LoadoutNode;
import net.avicus.atlas.module.loadouts.type.MovementLoadout;
import net.avicus.atlas.module.loadouts.type.PopulatorLoadout;
import net.avicus.atlas.module.loadouts.type.VehicleLoadout;
import net.avicus.atlas.module.loadouts.type.VisualLoadout;
import net.avicus.atlas.module.loadouts.type.XPLoadout;
import net.avicus.atlas.module.locales.LocalesModule;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.module.projectiles.CustomProjectile;
import net.avicus.atlas.module.projectiles.ProjectilesModule;
import net.avicus.atlas.util.ScopableItemStack;
import net.avicus.atlas.util.color.TeamColorProvider;
import net.avicus.atlas.util.inventory.RandomizableItemStack;
import net.avicus.atlas.util.inventory.populator.InventoryPopulator;
import net.avicus.atlas.util.inventory.populator.RandomInventoryPopulator;
import net.avicus.atlas.util.xml.GenericXmlString;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.atlas.util.xml.XmlException;
import net.avicus.atlas.util.xml.named.NamedParser;
import net.avicus.atlas.util.xml.named.NamedParsers;
import net.avicus.compendium.WeightedRandomizer;
import net.avicus.compendium.inventory.SingleMaterialMatcher;
import net.avicus.compendium.number.NumberAction;
import net.avicus.compendium.number.PreparedNumberAction;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.WeatherType;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;
import org.joda.time.Duration;
import org.joda.time.Seconds;

@ModuleFactorySort(ModuleFactorySort.Order.EARLY) // Doesn't rely on anything
public class LoadoutsFactory implements ModuleFactory<Module> {

  public final static Table<Object, Method, Collection<String>> NAMED_PARSERS = HashBasedTable
      .create();
  public final static List<FeatureDocumentation> FEATURES = Lists.newArrayList();
  private final static Collection<Material> COLORABLE = Arrays.asList(
      Material.WOOL,
      Material.STAINED_CLAY,
      Material.STAINED_GLASS,
      Material.STAINED_GLASS_PANE,
      Material.CARPET
  );

  public LoadoutsFactory() {
    NAMED_PARSERS.row(this).putAll(NamedParsers.methods(LoadoutsFactory.class));

    ExecutorsFactory.registerDocumentation(() ->
        FeatureDocumentation.builder()
            .requirement(this.getClass())
            .name("Apply Loadout")
            .tagName("apply-loadout")
            .description("An executor that gives a loadout to a player.")
            .attribute("loadout", Attributes.idOf(true, "loadout"))
            .build());
  }

  @Override
  public ModuleDocumentation getDocumentation() {
    ModuleDocumentation.ModuleDocumentationBuilder builder = ModuleDocumentation.builder();

    builder.name("Items & Loadouts")
        .tagName("loadouts")
        .category(ModuleDocumentation.ModuleCategory.CORE)
        .description(
            "You can use this module to create sets of items and other player attributes that can be given to players at different times.");

    builder.feature(FeatureDocumentation.builder()
        .name("Root Loadout Element")
        .tagName("loadout")
        .description(
            "The root loadout element is used to define basic information about the loadout.")
        .attribute("id", Attributes.id(false))
        .attribute("parent", Attributes.idOf(false, "parent loadout"))
        .attribute("force", new GenericAttribute(Boolean.class, false,
            "If the items should be forced into already occupied item slots."), false)
        .subFeature(FeatureDocumentation.builder()
            .name("Generic Items")
            .tagName("item")
            .tagName("helmet")
            .tagName("chestplate")
            .tagName("leggings")
            .tagName("boots")
            .tagName("kit-menu")
            .tagName("shop-opener")
            .description("Items can have many attributes, and are the main element of loadouts.")
            .attribute("slot", new GenericAttribute(Integer.class, false,
                "Slot in the inventory where the item should be placed. (If no slot is specified, the item will be put in the first available slot)"))
            .attribute("material",
                Attributes.javaDoc(true, Material.class, "The material of the item."))
            .attribute("shop-id", Attributes.idOf(true, "shop",
                "If a shop opener, which shop this item should open on right click."))
            .attribute("damage", new GenericAttribute(Integer.class, false,
                "The damage value of the item. (This can be used to color wool or to change the durability of a pickaxe.)"))
            .attribute("amount", new GenericAttribute(Integer.class, false,
                    "The amount of the item to be given to the player. (Use -1 for infinite stacks)"),
                1)
            .attribute("unbreakable", new GenericAttribute(Boolean.class, false,
                "If the item should never run out of durability."), false)
            .attribute("flags", new GenericAttribute(String.class, false,
                "The flags that should be applied to the item. Separate each flag with a ,"))
            .attribute("color", Attributes.color(false,
                "The color to be applied to the item. (This item must be a colorable item, or a parser error will be thrown)"))
            .attribute("team-color", new GenericAttribute(Boolean.class, false,
                    "If the player's team color should be used to color the item. This will override the other color attribute."),
                false)
            .attribute("potion", Attributes.javaDoc(false, PotionType.class,
                "The type of the potion that will be displayed. (This will not override the effects added by the effects tag)"))
            .attribute("projectile", Attributes.idOf(false, "projectile"))
            .subFeature(
                FeatureDocumentation.builder()
                    .name("Enchantments")
                    .tagName("enchantments")
                    .specInformation(SpecInformation.builder()
                        .change(SpecificationVersionHistory.LOADOUT_SUB_TAG,
                            "Item enchantments now must be wrapped in corresponding sub-tags.")
                        .build())
                    .description("This is used to apply enchantments to items.")
                    .attribute("type", Attributes.javaDoc(true, Enchantment.class,
                        "The type of enchantment to apply to the item."))
                    .attribute("level", new GenericAttribute(Integer.class, false,
                        "The level of the enchantment to be applied to the item."), 1)
                    .build()
            )
            .subFeature(FeatureDocumentation.builder()
                .name("Effects")
                .tagName("effects")
                .description("This is used to apply potion effects to potions.")
                .attribute("type", Attributes
                    .javaDoc(true, PotionEffectType.class, "Type of the effect to be applied."))
                .attribute("amplifier", new GenericAttribute(Integer.class, false,
                    "The amplifier of the potion effect."), 1)
                .attribute("duration",
                    Attributes.duration(false, true, "The duration of the effect."), 1)
                .specInformation(SpecInformation.builder()
                    .change(SpecificationVersionHistory.LOADOUT_SUB_TAG,
                        "Item effects now must be wrapped in corresponding sub-tags.")
                    .build())
                .build())
            .subFeature(FeatureDocumentation.builder()
                .name("Stored Enchantments")
                .tagName("stored-enchantments")
                .specInformation(
                    SpecInformation.builder().added(SpecificationVersionHistory.LOADOUT_SUB_TAG)
                        .build())
                .description("This is used to store enchantments inside of enchanted books.")
                .text(Attributes.javaDoc(true, Enchantment.class, "The enchantment to store."))
                .attribute("level",
                    new GenericAttribute(Integer.class, false, "The level of the enchantment."), 1)
                .build())
            .subFeature(
                FeatureDocumentation.builder()
                    .name("Banner Designs")
                    .tagName("pattern")
                    .description(
                        "Banner designs can also be created via XML, and can be combined to create complex designs. Designs are applied in the order that they are written.")
                    .attribute("pattern", Attributes.javaDoc(true, PatternType.class,
                        "The name of the type of pattern to be applied."))
                    .attribute("color", Attributes.color(false, "The color of the pattern."))
                    .build()
            )
            .build()
        )
        .subFeature(FeatureDocumentation.builder()
            .name("Player Heads")
            .tagName("player-head")
            .description("You can use this tag to give player heads in loadouts.")
            .attribute("slot", new GenericAttribute(Integer.class, false,
                "Slot in the inventory where the item should be placed. (If no slot is specified, the item will be put in the first available slot)"))
            .attribute("uuid",
                new GenericAttribute(UUID.class, true, "UUID of the owner of the head."))
            .build())
        .subFeature(FeatureDocumentation.builder()
            .name("Disguises")
            .tagName("disguise")
            .description(
                "Disguises are used to turn players into different types of entities or blocks.")
            .attribute("baby", new GenericAttribute(Boolean.class, false,
                    "If the disguise should be a baby. (Only for creatures which support this.)"),
                false)
            .text(new EnumAttribute(DisguiseType.class, true,
                "Type of disguise to give to the player."))
            .attribute("material", Attributes.materialMatcher(false, false,
                "If the diguise is a block, what material the block should be."))
            .build())
        .subFeature(FeatureDocumentation.builder()
            .name("Vehicle")
            .tagName("vehicle")
            .specInformation(
                SpecInformation.builder().added(SpecificationVersionHistory.LOADOUT_SUB_TAG)
                    .build())
            .description(
                "Vehicles are used to mount players on entities when they receive the loadout.")
            .text(Attributes.javaDoc(true, EntityType.class, "The type of vehicle."))
            .attribute("sticky", new GenericAttribute(Boolean.class, false,
                "If the player should be unable to dismount from the entity."), false)
            .attribute("remove", new GenericAttribute(Boolean.class, false,
                "If the entity should be removed when the player dismounts."), false)
            .attribute("velocity",
                Attributes.vector(false, "The initial velocity of the entity."), "0, 0, 0")
            .build())
        .subFeature(FeatureDocumentation.builder()
            .name("Effects")
            .tagName("effects")
            .description("Use this to apply potion effects directly to the player.")
            .text(Attributes.javaDoc(true, PotionEffectType.class,
                "The type of the effect that the potion will cause."))
            .attribute("amplifier",
                new GenericAttribute(Integer.class, false, "The amplifier of the potion effect."),
                1)
            .attribute("duration", Attributes.duration(false, true, "The duration of the effect."),
                1)
            .build())
        .subFeature(FeatureDocumentation.builder()
            .name("Compass")
            .tagName("compass")
            .description(
                "Set the player's compass target to a fixed position. (The compass module will override this)")
            .text(Attributes.vector(true, "The target of the compass."))
            .build())
        .subFeature(FeatureDocumentation.builder()
            .name("Weather")
            .tagName("weather")
            .description("Use this to set the player's personal weather state.")
            .text(new EnumAttribute(WeatherType.class, true,
                "The type of weather the player should see."))
            .build())
        .subFeature(FeatureDocumentation.builder()
            .name("Time")
            .tagName("time")
            .description("You can use this to set the player's personal time.")
            .attribute("action", Attributes.action(false, "the player's local time"), "set")
            .text(new GenericAttribute(Integer.class, true,
                "The time modifier that should be used by the action."))
            .build())
        .subFeature(FeatureDocumentation.builder()
            .name("Health")
            .tagName("health")
            .description("You can use this to set the player's health.")
            .attribute("action", Attributes.action(false, "the player's health"), "set")
            .text(new GenericAttribute(Integer.class, true,
                "The health modifier that should be used by the action."))
            .build())
        .subFeature(FeatureDocumentation.builder()
            .name("Max Health")
            .tagName("max-health")
            .description("You can use this to set the player's max health.")
            .attribute("action", Attributes.action(false, "the player's max health"), "set")
            .text(new GenericAttribute(Integer.class, true,
                "The health modifier that should be used by the action."))
            .build())
        .subFeature(FeatureDocumentation.builder()
            .name("Health Scale")
            .tagName("health-scale")
            .description("You can use this to set the player's health scale.")
            .attribute("action", Attributes.action(false, "the player's health scale"), "set")
            .text(new GenericAttribute(Integer.class, true,
                "The health modifier that should be used by the action."))
            .build())
        .subFeature(FeatureDocumentation.builder()
            .name("Food")
            .tagName("food-level")
            .description("You can use this to set the player's food.")
            .attribute("action", Attributes.action(false, "the player's food level"), "set")
            .text(new GenericAttribute(Integer.class, true,
                "The food modifier that should be used by the action."))
            .build())
        .subFeature(FeatureDocumentation.builder()
            .name("Saturation")
            .tagName("saturation")
            .description("You can use this to set the player's saturation.")
            .attribute("action", Attributes.action(false, "the player's saturation level"), "set")
            .text(new GenericAttribute(Integer.class, true,
                "The saturation modifier that should be used by the action."))
            .build())
        .subFeature(FeatureDocumentation.builder()
            .name("Exhaustion")
            .tagName("exhaustion")
            .description(
                "Set the player's level of exhaustion (This ranges from 0.0 to 4.0. When the exhaustion level reaches above 4.0 it will get subtracted by 4.0 and subtracts 1 point of hunger or saturation.)")
            .attribute("action", Attributes.action(false, "the player's exhaustion level"), "set")
            .text(new GenericAttribute(Integer.class, true,
                "The exhaustion modifier that should be used by the action."))
            .build())
        .subFeature(FeatureDocumentation.builder()
            .name("Experience Level")
            .tagName("exp-level")
            .description("Set the player's experience level. (This is the number above the bar)")
            .attribute("action", Attributes.action(false, "the player's XP level"), "set")
            .text(new GenericAttribute(Integer.class, true,
                "The XP modifier that should be used by the action."))
            .build())
        .subFeature(FeatureDocumentation.builder()
            .name("Experience Points")
            .tagName("exp-points")
            .description("Set the player's experience points. (This is the value on the bar)")
            .attribute("action", Attributes.action(false, "the player's XP points"), "set")
            .text(new GenericAttribute(Integer.class, true,
                "The XP modifier that should be used by the action."))
            .build())
        .subFeature(FeatureDocumentation.builder()
            .name("Total Experience")
            .tagName("exp-total")
            .description(
                "Set the player's total experience level. (This will override both of the above values)")
            .attribute("action", Attributes.action(false, "the player's total XP"), "set")
            .text(new GenericAttribute(Integer.class, true,
                "The XP modifier that should be used by the action."))
            .build())
        .subFeature(FeatureDocumentation.builder()
            .name("Fly Speed")
            .tagName("fly-speed")
            .description("Set the player's fly speed.")
            .attribute("action", Attributes.action(false, "the player's fly speed"), "set")
            .text(new GenericAttribute(Integer.class, true,
                "The speed modifier that should be used by the action."))
            .build())
        .subFeature(FeatureDocumentation.builder()
            .name("Walk Speed")
            .tagName("walk-speed")
            .description("Set the player's walk speed.")
            .attribute("action", Attributes.action(false, "the player's walk speed"), "set")
            .text(new GenericAttribute(Integer.class, true,
                "The speed modifier that should be used by the action."))
            .build())
        .subFeature(FeatureDocumentation.builder()
            .name("Randomizer")
            .tagName("randomizer")
            .specInformation(
                SpecInformation.builder().added(SpecificationVersionHistory.LOADOUT_SUB_TAG)
                    .build())
            .description("Randomizers are used to give random items in loadouts with a chance.")
            .attribute("min",
                new GenericAttribute(Integer.class, true, "The minimum stack size for items."))
            .attribute("max",
                new GenericAttribute(Integer.class, true, "The maximum stack size for items."))
            .subFeature(FeatureDocumentation.builder()
                .name("Item Set")
                .tagName("items")
                .description(
                    "Item sets are used to define sets of items that can be given with a weight.")
                .attribute("min",
                    new GenericAttribute(Integer.class, false, "Minimum item stack size."))
                .attribute("max",
                    new GenericAttribute(Integer.class, false, "Maximum item stack size."))
                .attribute("weight",
                    new GenericAttribute(Double.class, true, "Weight of each item in set."))
                .build())
            .build())
        .build());

    FEATURES.forEach(builder::feature);

    return builder.build();
  }

  @Override
  public Optional<Module> build(Match match, MatchFactory factory, XmlElement root)
      throws ModuleBuildException {
    List<XmlElement> elements = root.getChildren("loadouts");

    if (elements.isEmpty()) {
      return Optional.empty();
    }

    elements.forEach(element -> {
      for (XmlElement child : element.getChildren()) {
        String id = child.getAttribute("id").asRequiredString();
        Loadout loadout = parseLoadout(match, child);

// register
        match.getRegistry().add(new RegisteredObject<>(id, loadout));
      }
    });

    match.getFactory().getFactory(ExecutorsFactory.class)
        .registerExecutor("apply-loadout", ApplyLoadoutExecutor::parse);
    return Optional.empty();
  }

  public ScopableItemStack parseItemStack(Match match, XmlElement element) {
    // material
    Material material = element.getAttribute("material").asRequiredEnum(Material.class, true);
    return parseItemStack(match, material, element);
  }

  public PotionMeta parsePotion(Match match, ItemStack base, ItemMeta meta, XmlElement element) {
    if (match.getMap().getSpecification()
        .greaterEqual(SpecificationVersionHistory.LOADOUT_SUB_TAG)) {
      if (element.hasChild("effects")) {
        for (XmlElement el : element.getRequiredChild("effects").getChildren("effect")) {
          PotionEffect effect = parsePotionEffect(el);

          if (!(meta instanceof PotionMeta)) {
            throw new XmlException(el, "Item effects can only be applied to potion items.");
          }

          PotionMeta potion = (PotionMeta) meta;
          potion.addCustomEffect(effect, true);
        }
      } else if (element.hasChild("effect")) {
        throw new XmlException(element, "Item effects must be wrapped in corresponding sub-tags.");
      }
    } else {
      if (element.hasChild("effect")) {
        match.warnDeprecation(
            "Item enchantments and effects now must be wrapped in corresponding sub-tags.",
            SpecificationVersionHistory.LOADOUT_SUB_TAG);
      }
      for (XmlElement el : element.getChildren("effect")) {
        PotionEffect effect = parsePotionEffect(el);

        if (!(meta instanceof PotionMeta)) {
          throw new XmlException(el, "Item effects can only be applied to potion items.");
        }

        PotionMeta potion = (PotionMeta) meta;
        potion.addCustomEffect(effect, true);
      }
    }

    if (element.getAttribute("potion").isValuePresent()) {
      if (!(meta instanceof PotionMeta)) {
        throw new XmlException(element,
            "Potion type attribute can only be applied to potion items.");
      }

      PotionType type = element.getAttribute("potion").asRequiredEnum(PotionType.class, true);
      base.setDurability((short) type.getDamageValue());
    }

    if (element.getAttribute("splash").isValuePresent()) {
      if (!(meta instanceof PotionMeta)) {
        throw new XmlException(element,
            "Potion splash attribute can only be applied to potion items.");
      }

      Potion potion = Potion.fromItemStack(base);
      boolean splash = element.getAttribute("splash").asRequiredBoolean();
      potion.setSplash(splash);
      base.setDurability((short) potion.toDamageValue());
    }
    return (PotionMeta) meta;
  }

  public ScopableItemStack parseItemStack(Match match, Material material, XmlElement element) {
    // damage
    short damage = element.getAttribute("damage").asNumber().orElse(0).shortValue();

    // amount
    int amount = element.getAttribute("amount").asInteger().orElse(1);

    ItemStack item = new ItemStack(material, amount, damage);
    ItemMeta meta = item.getItemMeta();

    // name
    Optional<LocalizedXmlString> name = Optional.empty();
    Optional<String> nameRaw = element.getAttribute("name").asString();
    if (nameRaw.isPresent()) {
      name = Optional.of(match.getRequiredModule(LocalesModule.class).parse(nameRaw.get()));
    }

    // lore
    Optional<List<LocalizedXmlString>> lore = Optional.empty();
    Optional<XmlElement> loreChild = element.getChild("lore");
    if (loreChild.isPresent()) {
      lore = Optional.of(new ArrayList<>());
      for (XmlElement child : loreChild.get().getChildren()) {
        lore.get().add(
            match.getRequiredModule(LocalesModule.class).parse(child.getText().asRequiredString()));
      }
    }

    // unbreakable
    boolean unbreakable = element.getAttribute("unbreakable").asBoolean().orElse(false);
    meta.spigot().setUnbreakable(unbreakable);

    // flags
    Optional<List<GenericXmlString>> flags = element.getAttribute("flags").asList(";", true);
    for (GenericXmlString value : flags.orElse(Collections.emptyList())) {
      ItemFlag flag = value.asRequiredEnum(ItemFlag.class, true);
      meta.addItemFlags(flag);
    }

    // color
    if (element.getAttribute("color").isValuePresent()) {
      if (meta instanceof LeatherArmorMeta) {
        Color color = element.getAttribute("color").asRequiredColor();
        ((LeatherArmorMeta) meta).setColor(color);
      } else if (meta instanceof BannerMeta) {
        DyeColor color = element.getAttribute("color").asRequiredEnum(DyeColor.class, true);
        ((BannerMeta) meta).setBaseColor(color);
      } else if (COLORABLE.contains(material)) {
        DyeColor dyeColor = element.getAttribute("color").asRequiredEnum(DyeColor.class, true);
        item.setDurability(dyeColor.getWoolData());
      } else {
        throw new XmlException(element, "Color can only be applied to leather armor.");
      }
    }

    // team-color
    boolean useTeamColor = element.getAttribute("team-color").asBoolean().orElse(false);
    Optional<TeamColorProvider> teamColor = Optional.empty();
    if (useTeamColor) {
      teamColor = Optional.of(new TeamColorProvider(match));
    }

    if (match.getMap().getSpecification()
        .greaterEqual(SpecificationVersionHistory.LOADOUT_SUB_TAG)) {
      if (element.hasChild("enchantments")) {
        for (XmlElement el : element.getRequiredChild("enchantments").getChildren("enchantment")) {
          Enchantment enchantment = Enchantment
              .getByName(el.getText().asRequiredString().toUpperCase().replace(" ", "_"));

          if (enchantment == null) {
            throw new XmlException(el, "Unknown enchantment.");
          }

          int level = el.getAttribute("level").asInteger().orElse(1);

          meta.addEnchant(enchantment, level, true);
        }
      } else if (element.hasChild("enchantment")) {
        throw new XmlException(element,
            "Item enchantments and effects now must be wrapped in corresponding sub-tags.");
      }
    } else {
      if (element.hasChild("enchantment")) {
        match.warnDeprecation("Item enchantments must be wrapped in corresponding sub-tags.",
            SpecificationVersionHistory.LOADOUT_SUB_TAG);
      }
      for (XmlElement el : element.getChildren("enchantment")) {
        Enchantment enchantment = Enchantment
            .getByName(el.getText().asRequiredString().toUpperCase().replace(" ", "_"));

        if (enchantment == null) {
          throw new XmlException(el, "Unknown enchantment.");
        }

        int level = el.getAttribute("level").asInteger().orElse(1);

        meta.addEnchant(enchantment, level, true);
      }
    }

    if (meta instanceof EnchantmentStorageMeta) {
      for (XmlElement el : element.getChildren("stored-enchantment")) {
        Enchantment enchantment = Enchantment
            .getByName(el.getText().asRequiredString().toUpperCase().replace(" ", "_"));

        if (enchantment == null) {
          throw new XmlException(el, "Unknown enchantment.");
        }

        int level = el.getAttribute("level").asInteger().orElse(1);

        ((EnchantmentStorageMeta) meta).addStoredEnchant(enchantment, level, true);
      }
    }

    if (meta instanceof PotionMeta) {
      meta = parsePotion(match, item, meta, element);
    }

    // banner pattern
    List<XmlElement> patterns = element.getChildren("pattern");
    Collections.reverse(patterns);
    for (XmlElement el : patterns) {
      DyeColor color = el.getAttribute("color").asRequiredEnum(DyeColor.class, true);
      PatternType type = el.getText().asRequiredEnum(PatternType.class, true);
      if (meta instanceof BannerMeta) {
        ((BannerMeta) meta).addPattern(new Pattern(color, type));
      } else {
        throw new XmlException(el, "Patterns can only be applied to banners.");
      }
    }

    item.setItemMeta(meta);

    Optional<CustomProjectile> projectile = FactoryUtils
        .resolveProjectile(match, element.getAttribute("projectile"),
            element.getChild("projectile"));

    if (projectile.isPresent()) {
      match.getRequiredModule(ProjectilesModule.class).registerProjectile(projectile.get());
      ProjectilesModule.applyProjectileFormat(item, projectile.get());
    }

    return new ScopableItemStack(match, item, name, lore, teamColor, projectile);
  }

  public ScopableItemStack parsePlayerHead(Match match, XmlElement element) {
    Optional<TeamColorProvider> teamColor = Optional.empty();
    Material material = Material.SKULL_ITEM;

    int amount = element.getAttribute("amount").asInteger().orElse(1);

    ItemStack item = new ItemStack(material, amount, (short) SkullType.PLAYER.ordinal());
    SkullMeta meta = (SkullMeta) item.getItemMeta();

    Minecrafter owner = new Minecrafter(element.getAttribute("uuid").asRequiredString(), null,
        null);

    String ownerName = ChatColor.stripColor(owner.getName());
    meta.setOwner(ownerName);

    item.setItemMeta(meta);

    return new ScopableItemStack(match, item, Optional.empty(), Optional.empty(), teamColor,
        Optional.empty());
  }

  public PotionEffect parsePotionEffect(XmlElement element) {
    PotionEffectType type = PotionEffectType
        .getByName(element.getText().asRequiredString().toUpperCase().replace(' ', '_'));
    if (type == null) {
      throw new XmlException(element, "Effect not found.");
    }
    int amplifier = element.getAttribute("amplifier").asInteger().orElse(1);
    Duration duration = element.getAttribute("duration").asDuration()
        .orElse(Seconds.MAX_VALUE.toStandardDuration());

    int ticks = (int) duration.getStandardSeconds();
    if (ticks != Integer.MAX_VALUE) {
      ticks *= 20;
    }

    return new PotionEffect(type, ticks, amplifier - 1);
  }

  @SuppressWarnings("unchecked")
  public Loadout parseLoadout(Match match, XmlElement element) {
    Loadout parent = null;
    if (element.getAttribute("parent").isValuePresent()) {
      parent = match.getRegistry()
          .get(Loadout.class, element.getAttribute("parent").asRequiredString(), true)
          .orElse(null);
    }

    boolean force = element.getAttribute("force").asBoolean().orElse(false);

    List<Loadout> parsed = new ArrayList<>();

    for (Table.Cell<Object, Method, Collection<String>> parser : NAMED_PARSERS.cellSet()) {
      boolean anyMatch = false;
      for (String matcher : parser.getValue()) {
        if (element.hasChild(matcher)) {
          anyMatch = true;
          break;
        }
      }
      if (anyMatch) {
        try {
          parsed.add((Loadout) parser.getColumnKey()
              .invoke(parser.getRowKey(), match, element, force, parent));
        } catch (Exception e) {
          if (e.getCause() != null) {
            if (e.getCause() instanceof XmlException) {
              throw (XmlException) e.getCause();
            }
            throw new XmlException(element, e.getCause());
          }
          throw new XmlException(element, e);
        }
      }
    }

    if (parsed.isEmpty()) {
      throw new XmlException(element, "Element has no valid loadout children.");
    }

    if (parsed.size() == 1) {
      return parsed.get(0);
    } else {
      return new LoadoutNode(force, parent, parsed);
    }
  }

  @NamedParser({"item", "helmet", "chestplate", "leggings", "boots", "player-head", "kit-menu",
      "shop-opener"})
  public ItemLoadout parseItemLoadout(Match match, XmlElement element, boolean force,
      Loadout parent) {
    Map<Integer, ScopableItemStack> slotted = Maps.newHashMap();
    List<ScopableItemStack> unSlotted = new ArrayList<>();
    boolean kitMenu = false;
    int menuSlot = -1;
    boolean shopOpener = false;
    int shopSlot = -1;
    String shopID = null;

    for (XmlElement child : element.getChildren()) {
      String name = child.getName();
      boolean item = false;
      int slot = -1;

      switch (name) {
        case "item":
          item = true;
          if (child.getAttribute("slot").isValuePresent()) {
            slot = child.getAttribute("slot").asRequiredInteger();
          }
          break;
        case "helmet":
          item = true;
          slot = 103;
          break;
        case "chestplate":
          item = true;
          slot = 102;
          break;
        case "leggings":
          item = true;
          slot = 101;
          break;
        case "boots":
          item = true;
          slot = 100;
          break;
      }

      // Item
      if (item) {
        ScopableItemStack stack = parseItemStack(match, child);
        if (slot == -1) {
          unSlotted.add(stack);
        } else {
          slotted.put(slot, stack);
        }
      } else if (name.equals("player-head")) {
        if (child.getAttribute("slot").isValuePresent()) {
          slot = child.getAttribute("slot").asRequiredInteger();
        }

        ScopableItemStack stack = parsePlayerHead(match, child);

        if (slot == -1) {
          unSlotted.add(stack);
        } else {
          slotted.put(slot, stack);
        }
      } else if (name.equals("kit-menu")) {
        if (child.getAttribute("slot").isValuePresent()) {
          slot = child.getAttribute("slot").asRequiredInteger();
        }

        kitMenu = true;
        menuSlot = slot;
      } else if (name.equals("shop-opener")) {
        if (child.getAttribute("slot").isValuePresent()) {
          slot = child.getAttribute("slot").asRequiredInteger();
        }

        shopID = child.getAttribute("shop-id").asRequiredString();

        shopOpener = true;
        shopSlot = slot;
      }
    }
    return new ItemLoadout(force, parent, slotted, unSlotted, kitMenu, menuSlot, shopOpener,
        shopSlot, shopID);
  }

  @NamedParser("effect")
  public EffectLoadout parseEffectLoadout(Match match, XmlElement element, boolean force,
      Loadout parent) {
    List<PotionEffect> effects = new ArrayList<>();

    element.getChildren("effect").forEach(e -> {
      effects.add(parsePotionEffect(e));
    });

    return new EffectLoadout(force, parent, effects);
  }

  @NamedParser("compass")
  public CompassLoadout parseCompassLoadout(Match match, XmlElement element, boolean force,
      Loadout parent) {
    return new CompassLoadout(force, parent,
        element.getRequiredChild("compass").getText().asRequiredVector());
  }

  @NamedParser("glow")
  public GlowLoadout parseGlowLoadout(Match match, XmlElement element, boolean force,
      Loadout parent) {
    return new GlowLoadout(force, parent,
        element.getRequiredChild("glow").getText().asRequiredBoolean());
  }

  @NamedParser({"weather", "time"})
  public VisualLoadout parseVisualLoadout(Match match, XmlElement element, boolean force,
      Loadout parent) {
    WeatherType weather = null;
    PreparedNumberAction time = null;
    if (element.hasChild("weather")) {
      weather = element.getRequiredChild("weather").getText()
          .asRequiredEnum(WeatherType.class, true);
    }

    if (element.hasChild("time")) {
      time = parsePreparedNumberAction(element.getRequiredChild("time"));
    }

    return new VisualLoadout(force, parent, weather, time);
  }

  @NamedParser({"health", "max-health", "health-scale"})
  public HealthLoadout parseHealthLoadout(Match match, XmlElement element, boolean force,
      Loadout parent) {
    PreparedNumberAction health = null;
    PreparedNumberAction maxHealth = null;
    PreparedNumberAction healthScale = null;

    if (element.hasChild("health")) {
      health = parsePreparedNumberAction(element.getRequiredChild("health"));
    }

    if (element.hasChild("max-health")) {
      maxHealth = parsePreparedNumberAction(element.getRequiredChild("max-health"));
    }

    if (element.hasChild("health-scale")) {
      healthScale = parsePreparedNumberAction(element.getRequiredChild("health-scale"));
    }

    return new HealthLoadout(force, parent, health, maxHealth, healthScale);
  }

  @NamedParser({"food-level", "saturation"})
  public FoodLoadout parseFoodLoadout(Match match, XmlElement element, boolean force,
      Loadout parent) {
    PreparedNumberAction foodLevel = null;
    PreparedNumberAction saturation = null;

    if (element.hasChild("food-level")) {
      foodLevel = parsePreparedNumberAction(element.getRequiredChild("food-level"));
    }

    if (element.hasChild("saturation")) {
      saturation = parsePreparedNumberAction(element.getRequiredChild("saturation"));
    }

    return new FoodLoadout(force, parent, foodLevel, saturation);
  }

  @NamedParser({"exp-level", "exp-points", "exp-total"})
  public XPLoadout parseXPLoadout(Match match, XmlElement element, boolean force, Loadout parent) {
    PreparedNumberAction points = null;
    PreparedNumberAction level = null;
    PreparedNumberAction total = null;

    if (element.hasChild("exp-level")) {
      level = parsePreparedNumberAction(element.getRequiredChild("exp-level"));
    }

    if (element.hasChild("exp-points")) {
      points = parsePreparedNumberAction(element.getRequiredChild("exp-points"));
    }

    if (element.hasChild("exp-total")) {
      total = parsePreparedNumberAction(element.getRequiredChild("exp-total"));
    }

    return new XPLoadout(force, parent, level, points, total);
  }

  @NamedParser("randomizer")
  public PopulatorLoadout parsePopulator(Match match, XmlElement element, boolean force,
      Loadout parent) {
    InventoryPopulator populator = RandomInventoryPopulator.INSTANCE;

    int countMin = element.getAttribute("min").asRequiredInteger();
    int countMax = element.getAttribute("max").asRequiredInteger();

    WeightedRandomizer<RandomizableItemStack> items = new WeightedRandomizer<>();

    for (XmlElement elements : element.getDescendants("items")) {
      elements.inheritAttributes("items");

      Optional<Integer> amountMin = elements.getAttribute("min").asInteger();
      Optional<Integer> amountMax = elements.getAttribute("max").asInteger();
      double weight = elements.getAttribute("weight").asDouble().orElse(1.0);

      for (XmlElement item : elements.getChildren("item")) {
        ScopableItemStack stack = parseItemStack(match, item);
        items.set(new RandomizableItemStack(stack, amountMin, amountMax), weight);
      }
    }

    boolean allowDuplicates = element.getAttribute("allow-duplicates").asBoolean().orElse(false);
    return new PopulatorLoadout(force, parent, items, populator, allowDuplicates, countMin,
        countMax);
  }

  @NamedParser({"exhaustion", "walk-speed", "fly-speed"})
  public MovementLoadout parseMovementLoadout(Match match, XmlElement element, boolean force,
      Loadout parent) {
    PreparedNumberAction exhaustion = null;
    PreparedNumberAction walkSpeed = null;
    PreparedNumberAction flySpeed = null;

    if (element.hasChild("exhaustion")) {
      exhaustion = parsePreparedNumberAction(element.getRequiredChild("exhaustion"));
    }

    if (element.hasChild("walk-speed")) {
      walkSpeed = parsePreparedNumberAction(element.getRequiredChild("walk-speed"));
    }

    if (element.hasChild("fly-speed")) {
      flySpeed = parsePreparedNumberAction(element.getRequiredChild("fly-speed"));
    }

    return new MovementLoadout(force, parent, exhaustion, flySpeed, walkSpeed);
  }

  @NamedParser("vehicle")
  public VehicleLoadout parseVehicleLoadout(Match match, XmlElement element, boolean foce,
      Loadout parent) {
    XmlElement child = element.getRequiredChild("vehicle");

    EntityType vehicle = child.getText().asRequiredEnum(EntityType.class, true);
    Vector velocity = child.getAttribute("velocity").asVector().orElse(new Vector(0, 0, 0));
    boolean sticky = child.getAttribute("sticky").asBoolean().orElse(false);
    boolean remove = child.getAttribute("remove").asBoolean().orElse(false);
    return new VehicleLoadout(foce, parent, vehicle, velocity, sticky, remove);
  }

  @NamedParser("disguise")
  public DisguiseLoadout parseDisguiseLoadout(Match match, XmlElement element, boolean force,
      Loadout parent) {
    XmlElement child = element.getRequiredChild("disguise");

    boolean baby = child.getAttribute("baby").asBoolean().orElse(false);
    Material material = child.getAttribute("material").asMaterialMatcher()
        .map(SingleMaterialMatcher::getMaterial).orElse(null);
    EntityType type = child.getText().asRequiredEnum(EntityType.class, true);

    return new DisguiseLoadout(force, parent, DisguiseType.getType(type), material, baby);
  }

  private PreparedNumberAction parsePreparedNumberAction(XmlElement element) {
    Number value = element.getText().asRequiredNumber();
    NumberAction action = element.getAttribute("action").asNumberAction().orElse(NumberAction.SET);
    return new PreparedNumberAction(value, action);
  }
}
