package net.avicus.hook.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.menu.IndexedMenuItem;
import net.avicus.compendium.menu.inventory.ClickableInventoryMenuItem;
import net.avicus.compendium.menu.inventory.InventoryMenu;
import net.avicus.compendium.menu.inventory.StaticInventoryMenuItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * A pop-up inventory menu for a user to confirm or cancel an action.
 */
public class ConfirmationDialog extends InventoryMenu {

  private static final int ROWS = 3;
  private static final List<Integer> CONFIRM = Arrays.asList(
      0, 1, 2,
      9, 10, 11,
      18, 19, 20
  );
  private static final List<Integer> CANCEL = Arrays.asList(
      6, 7, 8,
      15, 16, 17,
      24, 25, 26
  );

  private final Runnable confirm;
  private final Runnable cancel;

  public ConfirmationDialog(Player player, Runnable confirm, Runnable cancel) {
    this(player, confirm, cancel, Optional.empty());
  }

  public ConfirmationDialog(Player player, Runnable confirm, Runnable cancel, Localizable title) {
    this(player, confirm, cancel, Optional.of(title));
  }

  public ConfirmationDialog(Player player, Runnable confirm, Runnable cancel,
      Optional<Localizable> title) {
    super(player, generateTitle(player, title), ROWS);
    this.confirm = confirm;
    this.cancel = cancel;

    Locale locale = player.getLocale();

    for (int index : CONFIRM) {
      add(new Action(index, true, locale));
    }

    for (int index : CANCEL) {
      add(new Action(index, false, locale));
    }
  }

  private static String generateTitle(Player player, Optional<Localizable> providedTitle) {
    Locale locale = player.getLocale();
    return providedTitle.orElse(Messages.UI_CONFIRM_CANCEL.with(ChatColor.DARK_GRAY))
        .translate(locale).toLegacyText();
  }

  private void onConfirm() {
    HookTask.of(this.confirm).now();
  }

  private void onCancel() {
    HookTask.of(this.cancel).now();
  }

  @Override
  public void onExit() {
    onCancel();
  }

  private class Action extends StaticInventoryMenuItem implements ClickableInventoryMenuItem,
      IndexedMenuItem {

    private final int index;
    private final boolean confirm;
    private final Locale locale;

    public Action(int index, boolean confirm, Locale locale) {
      this.index = index;
      this.confirm = confirm;
      this.locale = locale;
    }

    @Override
    public void onClick(ClickType type) {
      close();

      if (this.confirm) {
        onConfirm();
      } else {
        onCancel();
      }
    }

    @Override
    public int getIndex() {
      return this.index;
    }

    @Override
    public ItemStack getItemStack() {
      if (this.confirm) {
        ItemStack stack = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta meta = stack.getItemMeta();

        Localizable title = Messages.UI_CONFIRM.with(TextStyle.ofColor(ChatColor.GREEN).bold());
        meta.setDisplayName(title.translate(this.locale).toLegacyText());

        stack.setItemMeta(meta);
        return stack;
      } else {
        ItemStack stack = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta meta = stack.getItemMeta();

        Localizable title = Messages.UI_CANCEL.with(TextStyle.ofColor(ChatColor.DARK_RED).bold());
        meta.setDisplayName(title.translate(this.locale).toLegacyText());

        stack.setItemMeta(meta);
        return stack;
      }
    }
  }
}
