package net.avicus.atlas.module.groups.menu;

import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.Spectators;
import net.avicus.compendium.menu.IndexedMenuItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpectatorItem extends GroupMenuItem implements IndexedMenuItem {

  public SpectatorItem(Player player, Match match, Spectators spectators) {
    super(player, match, spectators);
  }

  @Override
  public ItemStack getItemStack() {
    ItemStack stack = super.getItemStack();
    stack.setType(Material.COMPASS);
    return stack;
  }

  @Override
  protected boolean hasPermissionToChoose() {
    // You can always choose to join the spectators group
    return true;
  }

  @Override
  public int getIndex() {
    return 17;
  }
}
