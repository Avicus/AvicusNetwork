package net.avicus.magma.module.gadgets;

import java.util.Locale;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractGadgetContext<G extends Gadget> implements GadgetContext<G> {

  private final G gadget;

  public AbstractGadgetContext(G gadget) {
    this.gadget = gadget;
  }

  @Override
  public ItemStack icon(Locale locale) {
    return this.gadget.icon(locale);
  }

  @Override
  public G getGadget() {
    return this.gadget;
  }
}
