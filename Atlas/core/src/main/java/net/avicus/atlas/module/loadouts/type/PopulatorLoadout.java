package net.avicus.atlas.module.loadouts.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import lombok.ToString;
import net.avicus.atlas.module.loadouts.Loadout;
import net.avicus.atlas.util.inventory.RandomizableItemStack;
import net.avicus.atlas.util.inventory.populator.InventoryPopulator;
import net.avicus.compendium.WeightedRandomizer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@ToString(callSuper = true)
public class PopulatorLoadout extends Loadout {

  private static final Random random = new Random();

  private final WeightedRandomizer<RandomizableItemStack> items;
  private final InventoryPopulator populator;

  private final boolean allowDuplicates;

  private final int min;
  private final int max;

  public PopulatorLoadout(boolean force,
      @Nullable Loadout parent,
      WeightedRandomizer<RandomizableItemStack> items,
      InventoryPopulator populator,
      boolean allowDuplicates,
      int min, int max) {
    super(force, parent);
    this.items = items;
    this.populator = populator;
    this.allowDuplicates = allowDuplicates;
    this.min = min;
    this.max = max;
  }

  @Override
  public void give(Player player, boolean force) {
    int count = random.nextInt(this.max - this.min + 1) + this.min;

    List<ItemStack> items = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      ItemStack stack = this.items.next().getItemStack();
      if (allowDuplicates || !items.contains(stack)) {
        items.add(stack);
      }
    }

    this.populator.populate(player.getInventory(), items);
  }
}
