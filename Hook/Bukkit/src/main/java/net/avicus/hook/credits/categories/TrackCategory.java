package net.avicus.hook.credits.categories;

import net.avicus.hook.credits.CategoryMenu;
import net.avicus.hook.credits.GadgetRanksRequirement;
import net.avicus.hook.credits.GadgetStore;
import net.avicus.hook.gadgets.types.track.TrackGadget;
import net.avicus.hook.gadgets.types.track.TrackType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TrackCategory extends CategoryMenu {

  public TrackCategory(Player player, GadgetStore store, int index) {
    super(player, store, index);

    add(new TrackGadget(TrackType.THIRTEEN, 50), 8500);
    add(new TrackGadget(TrackType.CAT, 50), 10000);
    add(new TrackGadget(TrackType.BLOCKS, 50), 12500);
    add(new TrackGadget(TrackType.CHIRP, 50), 15000);
    add(new TrackGadget(TrackType.FAR, 50), 15000);
    add(new TrackGadget(TrackType.MALL, 50), 17500);
    add(new TrackGadget(TrackType.MELLOHI, 50), 25000,
        new GadgetRanksRequirement("Gold", "Emerald", "Diamond"));
    add(new TrackGadget(TrackType.STAL, 50), 30000,
        new GadgetRanksRequirement("Gold", "Emerald", "Diamond"));
    add(new TrackGadget(TrackType.STRAD, 50), 37500,
        new GadgetRanksRequirement("Gold", "Emerald", "Diamond"));
    add(new TrackGadget(TrackType.WAIT, 50), 50000,
        new GadgetRanksRequirement("Emerald", "Diamond"));
    add(new TrackGadget(TrackType.ELEVEN, 50), 75000, new GadgetRanksRequirement("Diamond"));
    add(new TrackGadget(TrackType.WARD, 50), 100000, new GadgetRanksRequirement("Diamond"));
  }

  @Override
  public ItemStack getItemStack() {
    ItemStack stack = new ItemStack(Material.JUKEBOX);
    ItemMeta meta = stack.getItemMeta();

    meta.setDisplayName(ChatColor.GREEN + "Tracks");

    stack.setItemMeta(meta);
    return stack;
  }
}
