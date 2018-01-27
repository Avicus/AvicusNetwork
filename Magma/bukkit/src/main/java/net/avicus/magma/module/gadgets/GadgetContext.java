package net.avicus.magma.module.gadgets;

import com.google.gson.JsonObject;
import java.util.Locale;
import org.bukkit.inventory.ItemStack;

public interface GadgetContext<G extends Gadget> {

  G getGadget();

  ItemStack icon(Locale locale);

  JsonObject serialize();

  /**
   * Convenience method to get the manager of this gadget context.
   */
  default GadgetManager getManager() {
    return getGadget().getManager();
  }
}
