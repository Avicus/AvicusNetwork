package net.avicus.hook.gadgets.types.item;

import com.google.gson.JsonObject;
import net.avicus.hook.utils.HookTask;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.module.gadgets.GadgetManager;
import net.avicus.magma.module.gadgets.Gadgets;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemManager implements GadgetManager<ItemGadget, ItemContext> {

  public static final ItemManager INSTANCE = new ItemManager();

  private final Gadgets gadgets;

  private ItemManager() {
    this.gadgets = getGadgets();
  }

  @Override
  public String getType() {
    return "item";
  }

  @Override
  public void init() {
    // Nothing fancy to do
  }

  @Override
  public void onAsyncLoad(User user, ItemContext context) {
    // Nothing fancy to do
  }

  @Override
  public void onAsyncUnload(User user, ItemContext context) {
    // Nothing fancy to do
  }

  @Override
  public void onUse(Player player, ItemContext context) {
    // Items are one-use gadgets
    gadgets.deleteBackpackGadget(context);

    // Give them the item
    HookTask.of(() -> player.getInventory().addItem(context.getItemStack())).now();
  }

  @Override
  public ItemGadget deserializeGadget(JsonObject json) {
    Material material = Material.valueOf(json.get("material").getAsString().toUpperCase());
    return new ItemGadget(new ItemStack(material));
  }
}
