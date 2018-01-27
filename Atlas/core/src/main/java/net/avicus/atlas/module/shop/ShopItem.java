package net.avicus.atlas.module.shop;

import java.util.List;
import lombok.Getter;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.util.ScopableItemStack;
import net.avicus.magma.module.prestige.PrestigeLevel;
import org.bukkit.entity.Player;

/**
 * An item that can be purchased in a shop.
 */
@Getter
public abstract class ShopItem {

  /**
   * The price of the item.
   */
  private final int price;

  /**
   * The required level needed to purchase the item.
   *
   * NOTE: If the {@link net.avicus.magma.module.prestige.PrestigeModule} is not enabled, this will
   * be ignored.
   */
  private final PrestigeLevel requiredLevel;

  /**
   * The name of the item.
   */
  private final LocalizedXmlString name;

  /**
   * List of description lines for the item. This will never be null, but can be empty.
   */
  private final List<LocalizedXmlString> description;

  /**
   * The icon which should be used for the corresponding {@link net.avicus.atlas.module.shop.menu.ShopMenuItem}.
   */
  private final ScopableItemStack icon;

  /**
   * The check that should be ran before this item can be purchased.
   */
  private final Check purchaseCheck;

  /**
   * Constructor
   *
   * @param price The price of the item.
   * @param requiredLevel The required level needed to purchase the item.
   * @param name The name of the item.
   * @param description List of description lines for the item. This will never be null, but can be
   * empty.
   * @param icon The icon which should be used for the corresponding {@link
   * net.avicus.atlas.module.shop.menu.ShopMenuItem}.
   * @param purchaseCheck The check that should be ran before this item can be purchased.
   */
  public ShopItem(int price, PrestigeLevel requiredLevel,
      LocalizedXmlString name,
      List<LocalizedXmlString> description, ScopableItemStack icon,
      Check purchaseCheck) {
    this.price = price;
    this.requiredLevel = requiredLevel;
    this.name = name;
    this.description = description;
    this.icon = icon;
    this.purchaseCheck = purchaseCheck;
  }

  /**
   * Give an item to a player.
   *
   * IMPLEMENTATION NOTE: This is performed after the {@link #purchaseCheck} is performed and
   * passes, and price/level requirements are met.
   *
   * @param player Player to give the item to.
   */
  public abstract void give(Player player);
}
