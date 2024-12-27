package net.avicus.hook.credits.categories;

import java.util.Locale;
import net.avicus.compendium.sound.SoundLocation;
import net.avicus.compendium.sound.SoundType;
import net.avicus.hook.credits.CategoryMenu;
import net.avicus.hook.credits.GadgetItem;
import net.avicus.hook.credits.GadgetRanksRequirement;
import net.avicus.hook.credits.GadgetStore;
import net.avicus.hook.gadgets.types.sound.SoundGadget;
import net.avicus.magma.database.model.impl.User;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SoundCategory extends CategoryMenu {

  private final SoundLocation location;

  public SoundCategory(Player player, GadgetStore store, int index, SoundLocation location) {
    super(player, store, index);

    this.location = location;

    add(new SoundGadget(location, SoundType.NONE), 0);

    add(new SoundGadget(location, SoundType.DING), 1000);
    add(new SoundGadget(location, SoundType.LEVEL_UP), 1300);
    add(new SoundGadget(location, SoundType.DRINK), 5000,
        new GadgetRanksRequirement("Gold", "Emerald", "Diamond"));
    add(new SoundGadget(location, SoundType.EAT), 5000);
    add(new SoundGadget(location, SoundType.BURP), 1000);
    add(new SoundGadget(location, SoundType.CLICK), 500);
    add(new SoundGadget(location, SoundType.HIT), 700);

    add(new SoundGadget(location, SoundType.SNARE), 5000);
    add(new SoundGadget(location, SoundType.BASS), 15000);
    add(new SoundGadget(location, SoundType.PIANO), 17500);
    add(new SoundGadget(location, SoundType.PLING), 25000,
        new GadgetRanksRequirement("Gold", "Emerald", "Diamond"));

    add(new SoundGadget(location, SoundType.FIREWORK), 7500);
    add(new SoundGadget(location, SoundType.LAUNCH), 2000);
    add(new SoundGadget(location, SoundType.PLOP), 3000,
        new GadgetRanksRequirement("Emerald", "Diamond"));
    add(new SoundGadget(location, SoundType.EXPLOSION), 9000,
        new GadgetRanksRequirement("Emerald", "Diamond"));
    add(new SoundGadget(location, SoundType.POP), 700);

    add(new SoundGadget(location, SoundType.ANVIL), 5000);
    add(new SoundGadget(location, SoundType.DOOR_HIT), 1000);
    add(new SoundGadget(location, SoundType.DOOR_BREAK), 50000,
        new GadgetRanksRequirement("Diamond"));

    add(new SoundGadget(location, SoundType.MEOW), 5000,
        new GadgetRanksRequirement("Gold", "Emerald", "Diamond"));
    add(new SoundGadget(location, SoundType.ENDERDRAGON), 5000,
        new GadgetRanksRequirement("Diamond"));
    add(new SoundGadget(location, SoundType.GOLEM_DEATH), 75000);
    add(new SoundGadget(location, SoundType.BARK), 800);
    add(new SoundGadget(location, SoundType.ZOMBIE_MOAN), 1000);
    add(new SoundGadget(location, SoundType.BLAZE_BREATH), 2000,
        new GadgetRanksRequirement("Gold", "Emerald", "Diamond"));
    add(new SoundGadget(location, SoundType.BLAZE_HIT), 2000,
        new GadgetRanksRequirement("Gold", "Emerald", "Diamond"));
    add(new SoundGadget(location, SoundType.BLAZE_DEATH), 2500,
        new GadgetRanksRequirement("Diamond"));
    add(new SoundGadget(location, SoundType.TELEPORT), 1300);
    add(new SoundGadget(location, SoundType.GHAST_DEATH), 1500);
    add(new SoundGadget(location, SoundType.MOAN), 1500,
        new GadgetRanksRequirement("Gold", "Emerald", "Diamond"));
    add(new SoundGadget(location, SoundType.SPIDER), 8600);
    add(new SoundGadget(location, SoundType.HMMM), 5090);
    add(new SoundGadget(location, SoundType.WITHER_SPAWN), 10500,
        new GadgetRanksRequirement("Emerald", "Diamond"));
    add(new SoundGadget(location, SoundType.WITHER_SHOOT), 11500,
        new GadgetRanksRequirement("Emerald", "Diamond"));
    add(new SoundGadget(location, SoundType.WITHER_DEATH), 21500,
        new GadgetRanksRequirement("Diamond"));
    add(new SoundGadget(location, SoundType.REMEDY), 40000);
  }

  @Override
  public void onRightClick(GadgetItem item, User clicked, Player playerLocale) {
    SoundGadget gadget = (SoundGadget) item.getGadget();
    gadget.play(item.getPlayer());
  }

  @Override
  public ItemStack getItemStack() {
    ItemStack stack = new ItemStack(Material.NOTE_BLOCK);
    ItemMeta meta = stack.getItemMeta();

    meta.setDisplayName(ChatColor.YELLOW + this.location.prettyName() + " Sounds");

    stack.setItemMeta(meta);
    return stack;
  }

  public static class SoundCategoryRoot extends CategoryMenu {

    public SoundCategoryRoot(Player player, GadgetStore store, int index) {
      super(player, store, index);

      int where = 1;
      for (SoundLocation location : SoundLocation.values()) {
        add(new SoundCategory(player, store, where, location));
        switch (where) {
          case 7:
          case 16:
          case 25:
            where += 3;
            continue;
          default:
            where += 2;
        }
      }
    }

    @Override
    public ItemStack getItemStack() {
      ItemStack stack = new ItemStack(Material.NOTE_BLOCK);
      ItemMeta meta = stack.getItemMeta();

      meta.setDisplayName(ChatColor.YELLOW + "Sounds");

      stack.setItemMeta(meta);
      return stack;
    }
  }
}
