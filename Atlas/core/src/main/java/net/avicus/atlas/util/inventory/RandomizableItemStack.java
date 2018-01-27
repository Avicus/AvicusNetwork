package net.avicus.atlas.util.inventory;

import java.util.Optional;
import java.util.Random;
import lombok.ToString;
import net.avicus.atlas.util.ScopableItemStack;
import org.bukkit.inventory.ItemStack;

/**
 * Represents an item that could be populated in an inventory with a potentially random stack size.
 */
@ToString
public class RandomizableItemStack {

  /**
   * Static random instance,
   */
  private static final Random random = new Random();

  /**
   * Stack to be used for population.
   */
  private final ScopableItemStack itemStack;
  /**
   * Minimum size of the stack.
   */
  private final Optional<Integer> min;
  /**
   * Maximum size of the stack.
   */
  private final Optional<Integer> max;

  /**
   * Constructor.
   *
   * @param itemStack stack to be used for population
   * @param min minimum size of the stack
   * @param max maximum size of the stack
   */
  public RandomizableItemStack(ScopableItemStack itemStack, Optional<Integer> min,
      Optional<Integer> max) {
    this.itemStack = itemStack;
    this.min = min;
    this.max = max;
  }

  /**
   * Get the item stack that should be used for population.
   * <p>
   * FIXME: Should use scopable item stack here.
   *
   * @return the item stack that should be used for population
   */
  public ItemStack getItemStack() {
    ItemStack stack = this.itemStack.getItemStack();

    int min = this.min.orElse(stack.getAmount());
    int max = Math.max(min, this.max.orElse(stack.getAmount()));

    stack.setAmount(random.nextInt(max - min + 1) + min);
    return stack;
  }
}
