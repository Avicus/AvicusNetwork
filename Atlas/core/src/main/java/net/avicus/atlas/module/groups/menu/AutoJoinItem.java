package net.avicus.atlas.module.groups.menu;

import java.util.Collections;
import java.util.Locale;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.menu.inventory.ClickableInventoryMenuItem;
import net.avicus.compendium.menu.inventory.StaticInventoryMenuItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AutoJoinItem extends StaticInventoryMenuItem implements ClickableInventoryMenuItem {

  private final Match match;
  private final Player player;

  public AutoJoinItem(Match match, Player player) {
    this.match = match;
    this.player = player;
  }

  @Override
  public ItemStack getItemStack() {
    ItemStack stack = new ItemStack(Material.CHAINMAIL_HELMET);
    ItemMeta meta = stack.getItemMeta();

    Locale locale = this.player.getLocale();
    meta.setDisplayName(
        Messages.UI_AUTO_JOIN.with(TextStyle.ofColor(ChatColor.WHITE).bold()).translate(locale)
            .toLegacyText());
    meta.setLore(Collections.singletonList(
        Messages.UI_AUTO_JOIN_TEXT.with(TextStyle.ofColor(ChatColor.GRAY)).translate(locale)
            .toLegacyText()
    ));

    stack.setItemMeta(meta);
    return stack;
  }

  @Override
  public void onClick(InventoryClickEvent event) {
    GroupsModule groups = this.match.getRequiredModule(GroupsModule.class);
    Group previousGroup = groups.getGroup(this.player);

    Bukkit.dispatchCommand(this.player, "join");

    Group updatedGroup = groups.getGroup(this.player);

    if (!previousGroup.equals(updatedGroup)) {
      this.player.closeInventory();
      this.player.playSound(this.player.getLocation(), Sound.CLICK, 1F, 2F);
    }
  }
}
