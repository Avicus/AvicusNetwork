package net.avicus.magma.module.gadgets;

import com.google.gson.JsonObject;
import java.util.Locale;
import net.avicus.compendium.locale.text.Localizable;
import org.bukkit.inventory.ItemStack;

public interface Gadget<C extends GadgetContext> {

  GadgetManager getManager();

  Localizable getName();

  ItemStack icon(Locale locale);

  JsonObject serialize();

  C defaultContext();

  C deserializeContext(JsonObject json);

  boolean isAllowedInMatches();
}
