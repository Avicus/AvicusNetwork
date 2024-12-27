package net.avicus.hook.gadgets.types.item;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.magma.module.gadgets.AbstractGadget;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemGadget extends AbstractGadget<ItemContext> {

  @Getter
  private final ItemStack itemStack;

  public ItemGadget(ItemStack itemStack) {
    super(ItemManager.INSTANCE);
    this.itemStack = itemStack;
  }

  @Override
  public Localizable getName() {
    // Todo: Translate?
    return new UnlocalizedText(this.itemStack.getType().name().toLowerCase(), ChatColor.WHITE);
  }

  @Override
  public ItemStack icon(Player player) {
    return this.itemStack;
  }

  @Override
  public JsonObject serialize() {
    JsonObject json = new JsonObject();
    // Todo: Serialize ItemMeta and everything!
    json.addProperty("material", this.itemStack.getType().name());
    return json;
  }

  @Override
  public ItemContext defaultContext() {
    return new ItemContext(this);
  }

  @Override
  public ItemContext deserializeContext(JsonObject json) {
    return null;
  }

  @Override
  public boolean isAllowedInMatches() {
    return false;
  }
}
