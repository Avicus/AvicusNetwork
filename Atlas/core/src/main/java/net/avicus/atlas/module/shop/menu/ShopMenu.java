package net.avicus.atlas.module.shop.menu;

import com.google.common.collect.Lists;
import java.util.List;
import net.avicus.atlas.module.shop.Shop;
import net.avicus.atlas.module.shop.ShopItem;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.compendium.locale.text.UnlocalizedFormat;
import net.avicus.compendium.menu.inventory.InventoryMenu;
import net.avicus.compendium.menu.inventory.InventoryMenuItem;
import net.avicus.magma.item.ItemTag;
import net.avicus.magma.item.ItemTag.Boolean;
import net.avicus.magma.util.Inventories;
import net.avicus.magma.util.NMSUtils;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Menu used to purchase items from a shop.
 */
public class ShopMenu extends InventoryMenu {

  /**
   * Identifies if an item is capable of opening a menu.
   */
  private static final ItemTag.Boolean IS_OPENER = new Boolean("atlas.shop-opener", false);
  /**
   * Identifies which shop an item should open.
   */
  private static final ItemTag.String SHOP_ID = new ItemTag.String("atlas.shop-id", "");

  /**
   * Constructor
   *
   * @param shop Shop that the menu should use for data population.
   * @param player Player that owns the menu.
   */
  private ShopMenu(Shop shop, Player player) {
    super(player,
        new UnlocalizedFormat("{0} - {1}").with(shop.getName().toText(), Messages.UI_SHOP_POINTS
            .with(new LocalizedNumber(shop.getPointListener().getPoints(player)))).translate(player)
            .toLegacyText(),
        Inventories.rowCount(shop.getItems().size()),
        createContents(shop, player));
  }

  /**
   * See {@link #ShopMenu(Shop, Player)}
   */
  public static ShopMenu create(Shop shop, Player player) {
    return new ShopMenu(shop, player);
  }

  /**
   * Populate the menu with contents.
   *
   * @param shop Shop that the menu should use for data population.
   * @param player Player that owns the menu.
   * @return Items for the menu.
   */
  private static List<InventoryMenuItem> createContents(Shop shop, Player player) {
    List<InventoryMenuItem> contents = Lists.newArrayList();

    for (ShopItem item : shop.getItems()) {
      contents.add(ShopMenuItem.of(shop, item, player));
    }

    return contents;
  }

  /**
   * Create an {@link ItemStack} that can be used to open a shop.
   *
   * @param player Player who the stack is for.
   * @param shopID ID of the shop that the stack should open.
   * @return An {@link ItemStack} that can be used to open a shop.
   */
  public static ItemStack create(Player player, String shopID) {
    final ItemStack stack = new ItemStack(Material.NETHER_STAR);
    final ItemMeta meta = stack.getItemMeta();
    meta.setDisplayName(
        Messages.UI_SHOP_MENU.with(ChatColor.GOLD).translate(player.getLocale()).toLegacyText());
    IS_OPENER.set(meta, true);
    SHOP_ID.set(meta, shopID);
    stack.setItemMeta(meta);
    return stack;
  }

  /**
   * Check if an {@link Entity} can be used to open a shop.
   *
   * @param entity Entity to check.
   * @param shop Shop to check against.
   * @return If an {@link Entity} can be used to open a shop.
   */
  public static boolean matches(Entity entity, Shop shop) {
    NBTTagCompound tagCompound = NMSUtils.getNBT(entity);
    return entity != null && IS_OPENER.get(tagCompound) && SHOP_ID.has(tagCompound) && SHOP_ID
        .get(tagCompound).equals(shop.getId());
  }

  /**
   * Check if an {@link ItemStack} can be used to open a shop.
   *
   * @param stack Stack to check.
   * @param shop Shop to check against.
   * @return If an {@link ItemStack} can be used to open a shop.
   */
  public static boolean matches(ItemStack stack, Shop shop) {
    return stack != null && IS_OPENER.get(stack) && SHOP_ID.has(stack) && SHOP_ID.get(stack)
        .equals(shop.getId());
  }
}
