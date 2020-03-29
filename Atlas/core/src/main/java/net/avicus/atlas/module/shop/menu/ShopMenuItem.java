package net.avicus.atlas.module.shop.menu;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Locale;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.module.shop.Shop;
import net.avicus.atlas.module.shop.ShopItem;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.compendium.menu.inventory.ClickableInventoryMenuItem;
import net.avicus.compendium.menu.inventory.InventoryMenuItem;
import net.avicus.compendium.number.NumberAction;
import net.avicus.magma.Magma;
import net.avicus.magma.module.prestige.PrestigeLevel;
import net.avicus.magma.module.prestige.PrestigeModule;
import net.avicus.magma.network.user.Users;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Item which represents a {@link ShopItem}.
 */
public class ShopMenuItem implements ClickableInventoryMenuItem, InventoryMenuItem {

  /**
   * Shop that this item is inside of.
   */
  private final Shop shop;
  /**
   * Item that this menu item should gather data from.
   */
  private final ShopItem item;
  /**
   * Player that is viewing this item.
   */
  private final Player player;

  /**
   * Constructor
   *
   * @param shop Shop that this item is inside of.
   * @param item Item that this menu item should gather data from.
   * @param player Player that is viewing this item.
   */
  public ShopMenuItem(Shop shop, ShopItem item, Player player) {
    this.shop = shop;
    this.item = item;
    this.player = player;
  }

  /**
   * See {@link #ShopMenuItem(Shop, ShopItem, Player)}.
   */
  public static ShopMenuItem of(Shop shop, ShopItem item, Player player) {
    return new ShopMenuItem(shop, item, player);
  }

  /**
   * Send a message and sound do denote a failure of purchase.
   */
  private void sendFail() {
    this.player.playSound(this.player.getLocation(), Sound.NOTE_PIANO, 1f, 0.5f);
    this.player.sendMessage(Messages.UI_SHOP_PURCHASE_FAIL.with(ChatColor.RED));
  }

  @Override
  public void onClick(InventoryClickEvent event) {
    Pair<Boolean, Boolean> purchase = canPurchase();
    if (purchase.getLeft() && purchase.getRight()) {
      CheckContext context = new CheckContext(this.shop.getMatch());
      context.add(new PlayerVariable(this.player));

      if (this.item.getPurchaseCheck().test(context).fails()) {
        sendFail();
        return;
      }

      this.item.give(this.player);
      this.player.playSound(this.player.getLocation(), Sound.NOTE_PIANO, 1f, 1.5f);
      this.player.sendMessage(Messages.UI_SHOP_PURCHASE_SUCCESS
          .with(ChatColor.GREEN, this.item.getName().translate(this.player)));
      this.shop.getPointListener()
          .modifyPoints(this.player.getUniqueId(), this.item.getPrice(), NumberAction.SUBTRACT);
      this.player.closeInventory();
      ShopMenu.create(this.shop, this.player).open();
      return;
    }
    sendFail();
  }

  /**
   * Check if this item can be purchased at the current time.
   *
   * @return Left: If has enough points. Right: If has the required level, or true if the module is
   * disabled.
   */
  private Pair<Boolean, Boolean> canPurchase() {
    boolean points = this.shop.getPointListener().getPoints(this.player) >= this.item.getPrice();
    boolean prestige = true;
    if (Magma.get().getMm().hasModule(PrestigeModule.class)) {
      PrestigeLevel current = PrestigeLevel.fromDB(Magma.get().database().getPrestigeLevels()
          .currentLevel(Users.user(player).getId(), Magma.get().getCurrentSeason()));
      prestige = current.getId() >= this.item.getRequiredLevel().getId();
    }
    return Pair.of(points, prestige);
  }

  @Override
  public ItemStack getItemStack() {
    final Locale locale = this.player.getLocale();
    ItemStack stack = this.item.getIcon().getBaseItemStack().clone();
    ItemMeta meta = stack.getItemMeta();
    meta.setDisplayName(this.item.getName().translate(this.player));
    List<String> loreList = Lists.newArrayList();
    if (!this.item.getDescription().isEmpty()) {
      for (LocalizedXmlString lore : this.item.getDescription()) {
        loreList.add(lore.translate(this.player));
      }
      loreList.add("");
    }
    Pair<Boolean, Boolean> purchase = canPurchase();
    Localizable cost = Messages.UI_SHOP_POINTS.with(new LocalizedNumber(this.item.getPrice()));
    if (purchase.getLeft()) {
      cost.style().color(ChatColor.GREEN);
    } else {
      cost.style().color(ChatColor.RED);
    }
    loreList.add(cost.translate(locale).toLegacyText());
    if (Magma.get().getMm().hasModule(PrestigeModule.class)) {
      Localizable prestige = Messages.UI_SHOP_PRESTIGE
          .with(new LocalizedNumber(this.item.getRequiredLevel().getId()));
      if (purchase.getRight()) {
        prestige.style().color(ChatColor.GREEN);
      } else {
        prestige.style().color(ChatColor.RED);
      }
      loreList.add(prestige.translate(this.player).toLegacyText());
    }
    meta.setLore(loreList);
    stack.setItemMeta(meta);

    return stack;
  }

  @Override
  public boolean shouldUpdate() {
    return false;
  }

  @Override
  public void onUpdate() {
  }
}
