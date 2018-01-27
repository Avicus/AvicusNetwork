package net.avicus.atlas.module.executors.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.ToString;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.EnumAttribute;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.module.executors.Executor;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.atlas.util.xml.XmlException;
import net.avicus.compendium.inventory.MultiMaterialMatcher;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * An executor that enchants items in player inventories based on various criteria.
 */
@ToString
public class EnchantExecutor extends Executor {

  private final EnchantmentSelector selector;
  @Nullable
  private final MultiMaterialMatcher itemsToEnchant;
  private final Enchantment enchantment;
  private final int level;
  private final boolean onlyNatural;

  public EnchantExecutor(String id, Check check, EnchantmentSelector selector,
      @Nullable MultiMaterialMatcher itemsToEnchant, Enchantment enchantment, int level,
      boolean onlyNatural) {
    super(id, check);
    this.selector = selector;
    this.itemsToEnchant = itemsToEnchant;
    this.enchantment = enchantment;
    this.level = level;
    this.onlyNatural = onlyNatural;
  }

  public static FeatureDocumentation documentation() {
    return FeatureDocumentation.builder()
        .name("Enchant Item")
        .tagName("enchant-item")
        .description(
            "An executor that enchants items in player inventories based on various criteria.")
        .attribute("selector", new EnumAttribute(EnchantExecutor.EnchantmentSelector.class, true,
            "Part of inventory to look inside of."))
        .attribute("matcher", Attributes.materialMatcher(true, true, "Items to look for."))
        .text(Attributes.javaDoc(true, Enchantment.class, "Enchantment to be applied."))
        .attribute("level", new GenericAttribute(Integer.class, false, "Level of the enchantment."),
            1)
        .attribute("only-natural", new GenericAttribute(Boolean.class, false,
                "Only enchants items that can naturally be enchanted with this type of enchantment."),
            false)
        .build();
  }

  public static Executor parse(Match match, XmlElement element) throws XmlException {
    Check check = FactoryUtils
        .resolveRequiredCheckChild(match, element.getAttribute("check"), element.getChild("check"));
    String id = element.getAttribute("id").asString().orElse(UUID.randomUUID().toString());
    EnchantmentSelector selector = element.getAttribute("selector")
        .asRequiredEnum(EnchantmentSelector.class, true);
    MultiMaterialMatcher matcher = element.getAttribute("to-enchant").asMultiMaterialMatcher()
        .orElse(null);
    Enchantment enchantment = Enchantment
        .getByName(element.getText().asRequiredString().toUpperCase().replace(" ", "_"));

    if (enchantment == null) {
      throw new XmlException(element, "Unknown enchantment.");
    }

    int level = element.getAttribute("level").asInteger().orElse(1);

    boolean onlyNatural = element.getAttribute("only-natural").asBoolean().orElse(true);
    return new EnchantExecutor(id, check, selector, matcher, enchantment, level, onlyNatural);
  }

  @Override
  public void execute(CheckContext context) {
    Player player = context.getLast(PlayerVariable.class).map(PlayerVariable::getPlayer)
        .orElse(null);
    if (player != null) {
      List<ItemStack> toEnchant = new ArrayList<>();
      switch (this.selector) {
        case INVENTORY:
          toEnchant = Arrays.asList(player.getInventory().getContents());
          break;
        case HOTBAR:
          for (int i = 0; i < 8; i++) {
            toEnchant.add(player.getInventory().getItem(i));
          }
          break;
        case ARMOR:
          toEnchant = Arrays.asList(player.getInventory().getArmorContents());
          break;
        case HAND:
          toEnchant.add(player.getItemInHand());
      }

      if (this.itemsToEnchant != null) {
        toEnchant = toEnchant.stream().filter(stack -> this.itemsToEnchant.matches(stack.getData()))
            .collect(Collectors.toList());
      }
      if (this.onlyNatural) {
        toEnchant = toEnchant.stream().filter(this.enchantment::canEnchantItem)
            .collect(Collectors.toList());
      }

      toEnchant.forEach(item -> {
        if (item != null && item.getType() != Material.AIR) {
          item.addUnsafeEnchantment(this.enchantment, this.level);
        }
      });
    }
  }

  public enum EnchantmentSelector {
    INVENTORY, HOTBAR, ARMOR, HAND
  }
}
