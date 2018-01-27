package net.avicus.magma.item;

import javax.annotation.Nullable;
import net.minecraft.server.v1_8_R3.NBTBase;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftMetaItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class NBTUtils {

  private static final String KEY = "MAGMA_INFO";

  /**
   * Return the private namespace tag, or null if it is not present in the stack
   */
  public static
  @Nullable
  NBTTagCompound getCustomTag(@Nullable ItemMeta meta) {
    if (meta == null) {
      return null;
    }
    final NBTBase tag = ((CraftMetaItem) meta).getUnhandledTags().get(KEY);
    return tag instanceof NBTTagCompound ? (NBTTagCompound) tag : null;
  }

  public static
  @Nullable
  NBTTagCompound getCustomTag(@Nullable ItemStack stack) {
    if (stack == null || !stack.hasItemMeta()) {
      return null;
    }
    return getCustomTag(stack.getItemMeta());
  }

  /**
   * Return the private namespace tag, creating it if it does not exist
   */
  public static NBTTagCompound getOrCreateCustomTag(ItemMeta meta) {
    NBTTagCompound tag = getCustomTag(meta);
    if (tag == null) {
      tag = new NBTTagCompound();
      ((CraftMetaItem) meta).getUnhandledTags().put(KEY, tag);
    }
    return tag;
  }

  public static NBTTagCompound getOrCreateCustomTag(ItemStack stack) {
    return getOrCreateCustomTag(stack.getItemMeta());
  }

  /**
   * Remove the custom and/or root tag if either is empty.
   * <p>
   * This should be called after removing anything so that
   * item stacking works properly.
   */
  public static void prune(@Nullable ItemMeta meta) {
    if (meta == null) {
      return;
    }
    final NBTTagCompound tag = getCustomTag(meta);
    if (tag == null || !tag.isEmpty()) {
      return;
    }
    ((CraftMetaItem) meta).getUnhandledTags().remove(KEY);
  }

  public static void prune(@Nullable ItemStack stack) {
    ItemUtils.updateMetaIfPresent(stack, NBTUtils::prune);
  }
}
