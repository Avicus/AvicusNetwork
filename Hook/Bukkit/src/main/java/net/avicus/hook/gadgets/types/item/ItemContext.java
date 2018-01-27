package net.avicus.hook.gadgets.types.item;

import net.avicus.magma.module.gadgets.EmptyGadgetContext;
import org.bukkit.inventory.ItemStack;

public class ItemContext extends EmptyGadgetContext<ItemGadget> {

  public ItemContext(ItemGadget gadget) {
    super(gadget);
  }

  public ItemStack getItemStack() {
    return getGadget().getItemStack();
  }
}
