package net.avicus.hook.gadgets.backpack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.avicus.compendium.menu.inventory.ClickableInventoryMenuItem;
import net.avicus.compendium.menu.inventory.StaticInventoryMenuItem;
import net.avicus.hook.Hook;
import net.avicus.hook.utils.ConfirmationDialog;
import net.avicus.hook.utils.HookTask;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.module.gadgets.GadgetContext;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BackpackGadgetItem extends StaticInventoryMenuItem implements
    ClickableInventoryMenuItem {

  private final BackpackMenu menu;
  private final Player player;
  private final GadgetContext gadget;

  public BackpackGadgetItem(BackpackMenu menu, Player player, GadgetContext gadget) {
    this.menu = menu;
    this.player = player;
    this.gadget = gadget;
  }

  @Override
  public ItemStack getItemStack() {
    Locale locale = this.player.getLocale();

    ItemStack icon = this.gadget.icon(locale);
    ItemMeta meta = icon.getItemMeta();

    List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();

    lore.add(
        ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------");

    if (this.menu.isTrashEnabled()) {
      lore.add(
          Messages.UI_BACKPACK_TRASH.with(ChatColor.DARK_RED).translate(locale).toLegacyText());
    } else if (Hook.atlas() && !gadget.getGadget().isAllowedInMatches()) {
      lore.add(Messages.ERROR_CANNOT_USE.with(ChatColor.RED).translate(locale).toLegacyText());
    } else {
      lore.add(Messages.UI_BACKPACK_USE.with(ChatColor.YELLOW).translate(locale).toLegacyText());
    }

    meta.setLore(lore);
    icon.setItemMeta(meta);

    return icon;
  }

  @Override
  public void onClick(ClickType type) {
    if (this.menu.isTrashEnabled()) {
      new ConfirmationDialog(this.player, () -> {
        this.gadget.getManager().getGadgets().deleteBackpackGadget(this.gadget);
        this.menu.open();
        this.menu.refreshGadgetItems();
      }, this.menu::open).open();
    } else if (Hook.atlas() && !gadget.getGadget().isAllowedInMatches()) {
      this.player.sendMessage(Messages.ERROR_CANNOT_USE.with(ChatColor.RED));
    } else {
      HookTask.of(() -> {
        // Async: Use gadget (may use database)
        try {
          this.gadget.getManager().getGadgets().use(this.player, this.gadget);
        } catch (Exception e) {
          e.printStackTrace();
        }

        // Sync: Update menu due to changes in backpack (uses Bukkit API)
        HookTask.of(() -> this.menu.update(true)).now();
      }).nowAsync();
    }
  }
}
