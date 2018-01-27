package net.avicus.hook.credits;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.Getter;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.compendium.menu.inventory.ClickableInventoryMenuItem;
import net.avicus.compendium.menu.inventory.StaticInventoryMenuItem;
import net.avicus.hook.utils.ConfirmationDialog;
import net.avicus.hook.utils.HookTask;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.module.gadgets.Gadget;
import net.avicus.magma.module.gadgets.GadgetContext;
import net.avicus.magma.network.user.Users;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Getter
public class GadgetItem extends StaticInventoryMenuItem implements ClickableInventoryMenuItem {

  private final CategoryMenu menu;
  private final Player player;
  private final Gadget gadget;
  private final GadgetPrice price;
  private final List<GadgetPurchaseRequirement> requirements;

  public GadgetItem(CategoryMenu menu, Player player, Gadget gadget, int price,
      List<GadgetPurchaseRequirement> requirements) {
    this(menu, player, gadget, new GadgetPrice(price), requirements);
  }

  public GadgetItem(CategoryMenu menu, Player player, Gadget gadget, GadgetPrice price,
      List<GadgetPurchaseRequirement> requirements) {
    this.menu = menu;
    this.player = player;
    this.gadget = gadget;
    this.price = price;
    this.requirements = requirements;
  }

  public int discountedPrice() {
    return this.price.discountedAmount();
  }

  public List<GadgetPurchaseRequirement> unmetRequirements() {
    List<GadgetPurchaseRequirement> list = this.requirements.stream()
        .filter(requirement -> !requirement.meetsRequirement(this.player))
        .collect(Collectors.toList());
    return list;
  }

  @Override
  public void onClick(ClickType type) {
    User user = Users.user(this.player);
    Locale locale = this.player.getLocale();

    if (type.equals(ClickType.RIGHT)) {
      menu.onRightClick(this, user, locale);
    } else {
      handleClick(user, locale);
    }
  }

  public void handleClick(User user, Locale locale) {
    List<GadgetPurchaseRequirement> unmet = unmetRequirements();
    if (!unmet.isEmpty()) {
      List<String> unmetText = unmet.stream()
          .map(requirement -> requirement.getText()
              .translate(locale)
              .toPlainText())
          .collect(Collectors.toList());

      this.player.sendMessage(
          Messages.ERROR_UNMET_REQUIREMENTS.with(ChatColor.RED, Joiner.on(", ").join(unmetText)));
      return;
    }

    if (!this.price.canAfford(this.player)) {
      this.player.sendMessage(Messages.ERROR_CANNOT_AFFORD
          .with(ChatColor.RED, new LocalizedNumber(this.price.discountedAmount())));
      return;
    }

    new ConfirmationDialog(this.player, () -> {
      Credits.take(this.player, this.price.discountedAmount());
      GadgetContext context = this.gadget.defaultContext();
      HookTask.of(() -> context.getManager().getGadgets()
          .createBackpackGadget(user, context, true, new Date())).nowAsync();

      Localizable cost = new LocalizedNumber(this.price.discountedAmount());
      this.player.sendMessage(
          Messages.GENERIC_GADGET_PURCHASED.with(ChatColor.YELLOW, this.gadget.getName(), cost));
      this.player.closeInventory();
    }, this.menu::open).open();
  }

  @Override
  public ItemStack getItemStack() {
    Locale locale = this.player.getLocale();

    ItemStack stack = this.gadget.icon(locale);
    ItemMeta meta = stack.getItemMeta();

    List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
    lore.add(
        ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------");
    lore.add(
        ChatColor.GOLD + this.price.getDisplay().translate(locale).toPlainText() + ChatColor.WHITE
            + " credits");
    for (GadgetPurchaseRequirement requirement : this.requirements) {
      Localizable text = requirement.getText();
      text.style()
          .color(requirement.meetsRequirement(this.player) ? ChatColor.GREEN : ChatColor.RED);
      lore.add(ChatColor.WHITE + "* " + text.translate(locale).toLegacyText());
    }
    meta.setLore(lore);

    stack.setItemMeta(meta);
    return stack;
  }
}
