package net.avicus.hook.credits.categories;

import net.avicus.hook.credits.CategoryMenu;
import net.avicus.hook.credits.GadgetRanksRequirement;
import net.avicus.hook.credits.GadgetStore;
import net.avicus.hook.gadgets.types.device.NyanDevice;
import net.avicus.hook.gadgets.types.device.device.CreditBlaster;
import net.avicus.hook.gadgets.types.device.entity.EntityGun;
import net.avicus.hook.gadgets.types.device.entity.GunType;
import net.avicus.magma.Features;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DeviceCategory extends CategoryMenu {

  public DeviceCategory(Player player, GadgetStore store, int index) {
    super(player, store, index);

    add(new EntityGun(false, 10, GunType.TNT), 2200,
        new GadgetRanksRequirement("Emerald", "Diamond"));
    add(new EntityGun(false, 10, GunType.GOLEM), 1800, new GadgetRanksRequirement("Diamond"));
    add(new EntityGun(false, 25, GunType.OCELOT), 1200);
    add(new EntityGun(false, 30, GunType.SQUID), 1200,
        new GadgetRanksRequirement("Gold", "Emerald", "Diamond"));
    add(new EntityGun(false, 45, GunType.BAT), 1000);
    add(new EntityGun(false, 10, GunType.SKELETON), 1600);
    add(new EntityGun(false, 30, GunType.CHICKEN), 900);
    add(new EntityGun(false, 25, GunType.BOAT), 1500);
    add(new EntityGun(false, 60, GunType.EGG), 850);

    if (Features.Gadgets.newGunsOne()) {
      add(new EntityGun(false, 20, GunType.SLIME), 543);
      add(new EntityGun(false, 65, GunType.WITCH), 989);
      add(new EntityGun(false, 45, GunType.ENDERMITE), 1333,
          new GadgetRanksRequirement("Gold", "Emerald", "Diamond"));
    }

    add(new EntityGun(false, 22, GunType.ENDERMAN), 855);
    add(new EntityGun(false, 100, GunType.CREEPER), 5000,
        new GadgetRanksRequirement("Emerald", "Diamond"));
    add(new EntityGun(false, 300, GunType.SILVERFISH), 40000);

    add(new NyanDevice(false, 1), 500);

    add(new CreditBlaster(10), 15);
    add(new CreditBlaster(20), 30);
    add(new CreditBlaster(40), 45);
    add(new CreditBlaster(80), 100);
    add(new CreditBlaster(100), 115);
    add(new CreditBlaster(1000), 1100);
    add(new CreditBlaster(10000), 12000);
    add(new CreditBlaster(100000), 111000);

  }

  @Override
  public ItemStack getItemStack() {
    ItemStack stack = new ItemStack(Material.NOTE_BLOCK);
    ItemMeta meta = stack.getItemMeta();

    meta.setDisplayName(
        ChatColor.GOLD.toString() + ChatColor.BOLD + "[NEW]" + ChatColor.AQUA + " Devices");

    stack.setItemMeta(meta);
    return stack;
  }
}