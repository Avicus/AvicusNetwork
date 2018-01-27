package net.avicus.atlas.module.groups.menu;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.avicus.atlas.command.JoinCommands;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.menu.inventory.ClickableInventoryMenuItem;
import net.avicus.compendium.menu.inventory.InventoryMenuItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class GroupMenuItem implements InventoryMenuItem, ClickableInventoryMenuItem {

  private final Player player;
  private final Match match;
  private final Group group;

  public GroupMenuItem(Player player, Match match, Group group) {
    this.player = player;
    this.match = match;
    this.group = group;
  }

  @Override
  public ItemStack getItemStack() {
    ItemStack stack = new ItemStack(Material.LEATHER_HELMET);
    LeatherArmorMeta meta = (LeatherArmorMeta) stack.getItemMeta();

    Locale locale = this.player.getLocale();

    meta.setColor(this.group.getTeamColor().getColor());

    Localizable teamName = this.group.getName().toText(this.group.getChatColor());
    meta.setDisplayName(
        Messages.UI_JOIN_TEAM.with(TextStyle.ofColor(ChatColor.WHITE).bold(), teamName)
            .translate(locale).toLegacyText());

    String maxPlayers =
        this.group.getMaxPlayers() == Integer.MAX_VALUE ? "∞" : this.group.getMaxPlayers() + "";

    List<String> lore = new ArrayList<>();

    // Players
    String players = MessageFormat.format(
        ChatColor.GREEN + "{0} " + ChatColor.DARK_RED + "/ " + ChatColor.AQUA + "{1}",
        this.group.getMembers().size(),
        maxPlayers);
    lore.add(players);

    // Cannot pick team
    if (!this.hasPermissionToChoose()) {
      lore.add(
          Messages.ERROR_CANNOT_PICK_TEAM.with(ChatColor.RED).translate(locale).toLegacyText());
    }

    meta.setLore(lore);

    stack.setItemMeta(meta);
    return stack;
  }

  /**
   * Determine if the player is able to choose the group represented by this item.
   *
   * @return {@code true} ifif the player is able to choose the group represented by this item,
   * {@code false} otherwise
   */
  protected boolean hasPermissionToChoose() {
    return this.player.hasPermission(JoinCommands.PICK_PERMISSION);
  }

  @Override
  public boolean shouldUpdate() {
    return true;
  }

  @Override
  public void onUpdate() {
    // not needed
  }

  @Override
  public void onClick(ClickType type) {
    GroupsModule groups = this.match.getRequiredModule(GroupsModule.class);
    Group previousGroup = groups.getGroup(this.player);

    String name = ChatColor.stripColor(this.group.getName().translate(this.player));
    Bukkit.dispatchCommand(this.player, "join " + name);

    Group updatedGroup = groups.getGroup(this.player);

    boolean success = !previousGroup.equals(updatedGroup) && this.group.equals(updatedGroup);

    if (success) {
      this.player.closeInventory();
      this.player.playSound(this.player.getLocation(), Sound.CLICK, 1F, 2F);
    }
  }
}
