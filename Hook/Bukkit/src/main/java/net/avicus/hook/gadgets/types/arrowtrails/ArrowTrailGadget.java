package net.avicus.hook.gadgets.types.arrowtrails;

import com.google.gson.JsonObject;
import java.util.Locale;
import lombok.Getter;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.magma.module.gadgets.AbstractGadget;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ArrowTrailGadget extends AbstractGadget<ArrowTrailContext> {

  @Getter
  private final ArrowTrailType type;

  public ArrowTrailGadget(ArrowTrailType type) {
    super(ArrowTrailManager.INSTANCE);
    this.type = type;
  }

  public Localizable getName() {
    return new UnlocalizedText(this.type.getName(), ChatColor.WHITE);
  }

  @Override
  public ItemStack icon(Locale locale) {
    ItemStack stack = new ItemStack(Material.ARROW);
    ItemMeta meta = stack.getItemMeta();

    meta.setDisplayName(getName().translate(locale).toLegacyText());

    stack.setItemMeta(meta);
    return stack;
  }

  @Override
  public JsonObject serialize() {
    JsonObject json = new JsonObject();
    json.addProperty("type", this.type.name());
    return json;
  }

  @Override
  public ArrowTrailContext defaultContext() {
    return new ArrowTrailContext(this, false);
  }

  @Override
  public ArrowTrailContext deserializeContext(JsonObject json) {
    boolean enabled = json.get("enabled").getAsBoolean();
    return new ArrowTrailContext(this, enabled);
  }

  @Override
  public boolean isAllowedInMatches() {
    return true;
  }
}
