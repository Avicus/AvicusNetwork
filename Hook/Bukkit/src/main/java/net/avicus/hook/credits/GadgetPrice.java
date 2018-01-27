package net.avicus.hook.credits;

import com.google.common.base.Preconditions;
import net.avicus.compendium.locale.text.LocalizedNumber;
import org.bukkit.entity.Player;

/**
 * Represents the price of a gadget.
 */
public class GadgetPrice {

  private final int amount;
  private double discount;

  public GadgetPrice(int amount) {
    this.amount = amount;
    this.discount = 0;
  }

  public boolean canAfford(Player player) {
    return Credits.hasAtLeast(player, discountedAmount());
  }

  /**
   * Set the discount.
   *
   * @param discount The percentage off, within [0, 1].
   */
  public void setDiscount(double discount) {
    Preconditions.checkArgument(discount >= 0 && discount <= 1.0);
    this.discount = discount;
  }

  /**
   * Sets the discount to 0.
   */
  public void resetDiscount() {
    setDiscount(0);
  }

  /**
   * Get the original, undiscounted amount.
   */
  public int getOriginalAmount() {
    return this.amount;
  }

  /**
   * Generate the discounted amount, the price to be paid.
   */
  public int discountedAmount() {
    return (int) Math.floor((double) amount * (1.0 - this.discount));
  }

  /**
   * Get the display of this price.
   */
  public LocalizedNumber getDisplay() {
    return new LocalizedNumber(discountedAmount());
  }
}
