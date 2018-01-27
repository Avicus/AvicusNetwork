package net.avicus.magma.module.gadgets;

import com.google.gson.JsonObject;
import net.avicus.magma.Magma;
import net.avicus.magma.database.model.impl.User;
import org.bukkit.entity.Player;

public interface GadgetManager<G extends Gadget<C>, C extends GadgetContext<G>> {

  /**
   * The unique name of this gadget.
   */
  String getType();

  /**
   * Initialize this manager.
   */
  void init();

  /**
   * Called when a gadget is loaded (player join, purchase, etc.)
   */
  void onAsyncLoad(User user, C context);

  /**
   * Called when a gadget is unloaded (player leave).
   */
  void onAsyncUnload(User user, C context);

  /**
   * Called when a gadget is used (clicked in their backpack).
   */
  void onUse(Player player, C context);

  G deserializeGadget(JsonObject json);

  /**
   * Shortcut to deserializing a gadget, then deserializing a gadget context.
   *
   * @param jsonGadget The json gadget.
   * @param jsonContext The json context.
   * @return The gadget context.
   */
  default C deserializeContext(JsonObject jsonGadget, JsonObject jsonContext) {
    G gadget = deserializeGadget(jsonGadget);
    return gadget.deserializeContext(jsonContext);
  }

  default Gadgets getGadgets() {
    return Magma.get().getMm().get(Gadgets.class);
  }
}
