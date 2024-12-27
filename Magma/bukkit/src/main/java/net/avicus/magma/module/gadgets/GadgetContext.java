package net.avicus.magma.module.gadgets;

import com.google.gson.JsonObject;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface GadgetContext<G extends Gadget> {

  G getGadget();

  ItemStack icon(Player player);

  JsonObject serialize();

  /**
   * Convenience method to get the manager of this gadget context.
   */
  default GadgetManager getManager() {
    return getGadget().getManager();
  }
}
