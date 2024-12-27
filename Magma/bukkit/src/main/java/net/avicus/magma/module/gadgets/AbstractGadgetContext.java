package net.avicus.magma.module.gadgets;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractGadgetContext<G extends Gadget> implements GadgetContext<G> {

  private final G gadget;

  public AbstractGadgetContext(G gadget) {
    this.gadget = gadget;
  }

  @Override
  public ItemStack icon(Player player) {
    return this.gadget.icon(player);
  }

  @Override
  public G getGadget() {
    return this.gadget;
  }
}
