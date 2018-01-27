package net.avicus.atlas.util;

import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import java.util.HashMap;
import java.util.UUID;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.map.AtlasMap;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EnderChestStore {

  private static final Table<String, UUID, HashMap<Integer, ItemStack>> chestStore = TreeBasedTable
      .create();

  public static HashMap<Integer, ItemStack> getChest(UUID uuid) {
    HashMap<Integer, ItemStack> res = Maps.newHashMap();
    Atlas.performOnMatch(match -> {
      HashMap<Integer, ItemStack> found = chestStore.get(normalizeName(match.getMap()), uuid);
      if (found != null) {
        res.putAll(found);
      }
    });
    return res;
  }

  public static void store(UUID uuid, Inventory inventory) {
    HashMap<Integer, ItemStack> toStore = Maps.newHashMap();
    for (int i = 0; i < inventory.getContents().length; i++) {
      ItemStack stack = inventory.getContents()[i];
      if (stack == null || stack.getType() == Material.AIR) {
        continue;
      }
      toStore.put(i, stack);
    }
    Atlas.performOnMatch(match -> chestStore.put(normalizeName(match.getMap()), uuid, toStore));
  }

  private static String normalizeName(AtlasMap map) {
    return map.getName().toLowerCase().replaceAll(" ", "_") + "_" + map.getVersion().toString();
  }
}
