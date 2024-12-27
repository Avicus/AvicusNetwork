package net.avicus.atlas.module.groups.menu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.groups.Spectators;
import net.avicus.atlas.util.AtlasTask;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.menu.inventory.InventoryMenu;
import net.avicus.compendium.menu.inventory.InventoryMenuItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GroupMenu extends InventoryMenu {

  private final AtlasTask task;

  public GroupMenu(Player player, Match match) {
    super(player, createTitle(player), 2, createItems(player, match));

    this.task = new AtlasTask() {
      @Override
      public void run() {
        update(false);
      }
    };
  }

  private static Collection<InventoryMenuItem> createItems(Player player, Match match) {
    List<InventoryMenuItem> items = new ArrayList<>();
    items.add(new AutoJoinItem(match, player));

    GroupsModule module = match.getRequiredModule(GroupsModule.class);
    Spectators spectators = module.getSpectators();

    items.add(new SpectatorItem(player, match, spectators));
    items.addAll(module.getGroups()
        .stream()
        .filter(group -> !group.equals(spectators))
        .map(group -> new GroupMenuItem(player, match, group))
        .collect(Collectors.toList()));

    return items;
  }

  private static String createTitle(Player player) {
    return Messages.UI_TEAM_MENU.with(ChatColor.DARK_GRAY).render(player)
        .toLegacyText();
  }

  public static ItemStack createMenuOpener(Player player) {
    ItemStack stack = new ItemStack(Material.NETHER_STAR);
    ItemMeta meta = stack.getItemMeta();
    meta.setDisplayName(
        Messages.UI_TEAM_MENU.with(ChatColor.GREEN).render(player).toLegacyText());
    meta.setLore(Collections.singletonList(ChatColor.BLACK + "Team Menu"));

    stack.setItemMeta(meta);
    return stack;
  }

  public static boolean isMenuOpener(ItemStack stack) {
    if (stack == null) {
      return false;
    }

    ItemMeta meta = stack.getItemMeta();

    return meta.hasLore() && meta.getLore().contains(ChatColor.BLACK + "Team Menu");
  }

  @Override
  public void open() {
    super.open();
    this.task.repeat(0, 20);
  }

  @Override
  public void close() {
    super.close();
    this.task.cancel0();
  }
}
