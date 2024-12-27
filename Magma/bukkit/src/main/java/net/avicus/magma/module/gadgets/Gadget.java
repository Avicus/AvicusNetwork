package net.avicus.magma.module.gadgets;

import com.google.gson.JsonObject;
import net.avicus.compendium.locale.text.Localizable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Gadget<C extends GadgetContext> {

  GadgetManager getManager();

  Localizable getName();

  ItemStack icon(Player player);

  JsonObject serialize();

  C defaultContext();

  C deserializeContext(JsonObject json);

  boolean isAllowedInMatches();
}
