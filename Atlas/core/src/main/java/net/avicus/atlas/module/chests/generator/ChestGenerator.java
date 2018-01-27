package net.avicus.atlas.module.chests.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.LocationVariable;
import net.avicus.atlas.util.inventory.RandomizableItemStack;
import net.avicus.atlas.util.inventory.populator.InventoryPopulator;
import net.avicus.compendium.WeightedRandomizer;
import net.avicus.magma.util.region.Region;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.joda.time.Duration;

/**
 * Utility used to systematically (re)generate the contents of containers in a region.
 */
@ToString
public class ChestGenerator {

  /**
   * Static random instance.
   */
  private static final Random random = new Random();

  /**
   * Region that the containers are in.
   */
  private final Region region;
  /**
   * If the containers should first be emptied before population.
   */
  private final boolean clear;
  /**
   * Populator used to add items.
   */
  private final InventoryPopulator populator;
  /**
   * Minimum item stack size.
   */
  private final int min;
  /**
   * Maximum item stack size.
   */
  private final int max;
  /**
   * Item randomizer.
   */
  private final WeightedRandomizer<RandomizableItemStack> items;
  /**
   * Check to be ran before population begins.
   */
  private final Optional<Check> populateCheck;
  /**
   * Time before a container's contents are re-populated.
   */
  private final Optional<Duration> regenerateCountdown;
  /**
   * Number of times a container's items should be re-generated.
   */
  private final int regenerateCount;
  /**
   * If duplicate items of the same type are allowed to be populated.
   */
  private final boolean allowDuplicates;

  /**
   * Constructor.
   *
   * @param region region that the containers are in
   * @param clear if the containers should first be emptied before population
   * @param populator populator used to add items
   * @param min minimum item stack size
   * @param max maximum item stack size
   * @param items item randomizer
   * @param populateCheck check to be ran before population begins
   * @param regenerateCountdown time before a container's contents are re-populated
   * @param regenerateCount number of times a container's items should be re-generated
   * @param allowDuplicates if duplicate items of the same type are allowed to be populated
   */
  public ChestGenerator(Region region,
      boolean clear,
      InventoryPopulator populator,
      int min,
      int max,
      WeightedRandomizer<RandomizableItemStack> items,
      Optional<Check> populateCheck,
      Optional<Duration> regenerateCountdown,
      int regenerateCount,
      boolean allowDuplicates) {
    this.region = region;
    this.clear = clear;
    this.populator = populator;
    this.min = min;
    this.max = max;
    this.items = items;
    this.populateCheck = populateCheck;
    this.regenerateCountdown = regenerateCountdown;
    this.regenerateCount = regenerateCount;
    this.allowDuplicates = allowDuplicates;
  }

  /**
   * Get the time delay between re-generations.
   *
   * @return the time delay between re-generations
   */
  public Optional<Duration> getRegenerateCountdown() {
    return this.regenerateCountdown;
  }

  /**
   * Get the max number of times this generator may re-generate contents.
   *
   * @return the max number of times this generator may re-generate contents
   */
  public int getRegenerateCount() {
    return this.regenerateCount;
  }

  /**
   * Check if population is allowed for the certain block.
   *
   * @param match math the block is in
   * @param block block to check
   * @return if population is allowed for the certain block
   */
  public boolean shouldPopulate(Match match, Block block) {
    if (!this.region.contains(block)) {
      return false;
    }

    if (this.populateCheck.isPresent()) {
      CheckContext context = new CheckContext(match);
      context.add(new LocationVariable(block.getLocation()));
      return this.populateCheck.get().test(context).passes();
    }

    return true;
  }

  /**
   * Populate an inventory using this generator's populator with this generator's items.
   *
   * @param inventory inventory to populate
   */
  public void populate(Inventory inventory) {
    if (this.clear) {
      inventory.clear();
    }

    int count = random.nextInt(this.max - this.min + 1) + this.min;

    List<ItemStack> items = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      ItemStack stack = this.items.next().getItemStack();
      if (allowDuplicates || !items.contains(stack)) {
        items.add(stack);
      }
    }

    this.populator.populate(inventory, items);
  }
}
