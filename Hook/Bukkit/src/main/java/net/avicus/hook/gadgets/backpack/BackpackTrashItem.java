package net.avicus.hook.gadgets.backpack;

import com.google.common.base.Splitter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.menu.IndexedMenuItem;
import net.avicus.compendium.menu.inventory.ClickableInventoryMenuItem;
import net.avicus.compendium.menu.inventory.InventoryMenuItem;
import net.avicus.hook.utils.Messages;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BackpackTrashItem implements InventoryMenuItem, ClickableInventoryMenuItem,
    IndexedMenuItem {

  private final BackpackMenu menu;
  private final Player player;
  private final int index;

  public BackpackTrashItem(BackpackMenu menu, Player player, int index) {
    this.menu = menu;
    this.player = player;
    this.index = index;
  }

  @Override
  public ItemStack getItemStack() {
    Locale locale = this.player.getLocale();

    Material material = Material.BUCKET;
    String title = Messages.UI_TRASH_BIN.with(TextStyle.ofColor(ChatColor.DARK_RED).bold())
        .translate(locale).toLegacyText();
    String fullInfo = Messages.UI_TRASH_INFO.with(ChatColor.GRAY).translate(locale).toLegacyText();

    List<String> lore = new ArrayList<>(
        Splitter.on(';').splitToList(WordUtils.wrap(fullInfo, 35, ";" + ChatColor.GRAY, false)));
    lore.add(0,
        Messages.enabledOrDisabled(this.menu.isTrashEnabled()).translate(locale).toLegacyText());

    if (this.menu.isTrashEnabled()) {
      material = Material.LAVA_BUCKET;
    }

    ItemStack stack = new ItemStack(material);
    ItemMeta meta = stack.getItemMeta();
    meta.setDisplayName(title);
    meta.setLore(lore);
    stack.setItemMeta(meta);

    return stack;
  }

  @Override
  public void onClick(ClickType type) {
    this.menu.setTrashEnabled(!this.menu.isTrashEnabled());
    this.menu.update(true);
  }

  @Override
  public boolean shouldUpdate() {
    return false;
  }

  @Override
  public void onUpdate() {

  }

  @Override
  public int getIndex() {
    return this.index;
  }
}
