package net.avicus.magma.item;

import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtils {

  public static Optional<ItemMeta> tryMeta(ItemStack item) {
    return item.hasItemMeta() ? Optional.of(item.getItemMeta())
        : Optional.empty();
  }

  public static void updateMeta(ItemStack item, Consumer<ItemMeta> mutator) {
    final ItemMeta meta = item.getItemMeta();
    mutator.accept(meta);
    item.setItemMeta(meta);
  }

  public static void updateMetaIfPresent(@Nullable ItemStack item, Consumer<ItemMeta> mutator) {
    if (item != null && item.hasItemMeta()) {
      updateMeta(item, mutator);
    }
  }

  public static ItemStack normalize(ItemStack item) {
    // Ignore non-data durability
    if (item.getType().getMaxDurability() != 0) {
      item.setDurability((short) 0);
    }

    LockingSharingListener.UN_SHAREABLE.clear(item);
    LockingSharingListener.LOCKED.clear(item);

    return item;
  }
}
