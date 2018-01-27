package net.avicus.hook.credits.categories;

import net.avicus.hook.credits.CategoryMenu;
import net.avicus.hook.credits.GadgetRanksRequirement;
import net.avicus.hook.credits.GadgetStore;
import net.avicus.hook.gadgets.types.morph.MorphEntity;
import net.avicus.hook.gadgets.types.morph.MorphGadget;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MorphCategory extends CategoryMenu {

  public MorphCategory(Player player, GadgetStore store, int index) {
    super(player, store, index);

    // TODO: Remove Dec. 31
    add(new MorphGadget(MorphEntity.SNOW_GOLEM), 0);
    add(new MorphGadget(MorphEntity.SANTA), 0);
    add(new MorphGadget(MorphEntity.WHITE_RABBIT), 0);

    add(new MorphGadget(MorphEntity.COW), 12000);
    add(new MorphGadget(MorphEntity.BAT), 15000);
    add(new MorphGadget(MorphEntity.WOLF), 22000);
    add(new MorphGadget(MorphEntity.PIG), 24000);
    add(new MorphGadget(MorphEntity.SHEEP), 1400);
    add(new MorphGadget(MorphEntity.VILLAGER), 60000);
    add(new MorphGadget(MorphEntity.BLAZE), 80000,
        new GadgetRanksRequirement("Gold", "Emerald", "Diamond"));
    add(new MorphGadget(MorphEntity.SLIME), 10000,
        new GadgetRanksRequirement("Gold", "Emerald", "Diamond"));
    add(new MorphGadget(MorphEntity.ENDERMAN), 200000,
        new GadgetRanksRequirement("Gold", "Emerald", "Diamond"));
    add(new MorphGadget(MorphEntity.SPIDER), 20000);
    add(new MorphGadget(MorphEntity.SILVERFISH), 52000);
    add(new MorphGadget(MorphEntity.OCELOT), 32000);
    add(new MorphGadget(MorphEntity.IRON_GOLEM), 300000);
    add(new MorphGadget(MorphEntity.CHICKEN), 10000);

    add(new MorphGadget(MorphEntity.TNT), 50000,
        new GadgetRanksRequirement("Emerald", "Diamond"));
    add(new MorphGadget(MorphEntity.BOAT), 20000);
    add(new MorphGadget(MorphEntity.MINECART), 12000);

    add(new MorphGadget(MorphEntity.CREEPER), 430000, new GadgetRanksRequirement("Diamond"));
    add(new MorphGadget(MorphEntity.SKELETON), 41000);
    add(new MorphGadget(MorphEntity.GUARDIAN), 23000,
        new GadgetRanksRequirement("Gold", "Emerald", "Diamond"));

    add(new MorphGadget(MorphEntity.SQUID), 54000);
    add(new MorphGadget(MorphEntity.RABBIT), 6000);
    add(new MorphGadget(MorphEntity.MAGMA_CUBE), 31000);
  }

  @Override
  public ItemStack getItemStack() {
    ItemStack stack = new ItemStack(Material.ARMOR_STAND);
    ItemMeta meta = stack.getItemMeta();

    meta.setDisplayName(ChatColor.AQUA + "Morphs");

    stack.setItemMeta(meta);
    return stack;
  }
}
