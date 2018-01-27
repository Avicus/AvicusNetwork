package net.avicus.hook.gadgets.types.morph;

import com.google.gson.JsonObject;
import java.util.Locale;
import lombok.Getter;
import lombok.ToString;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.module.gadgets.AbstractGadget;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@ToString
public class MorphGadget extends AbstractGadget<MorphContext> {

  @Getter
  private final MorphEntity entity;

  public MorphGadget(MorphEntity entity) {
    super(MorphManager.INSTANCE);
    this.entity = entity;
  }

  @Override
  public Localizable getName() {
    return Messages.UI_MORPH.with(ChatColor.WHITE, this.entity.prettyName());
  }

  @Override
  public ItemStack icon(Locale locale) {
    ItemStack stack = new ItemStack(Material.ARMOR_STAND);
    ItemMeta meta = stack.getItemMeta();

    meta.setDisplayName(getName().translate(locale).toLegacyText());

    stack.setItemMeta(meta);
    return stack;
  }

  @Override
  public JsonObject serialize() {
    JsonObject json = new JsonObject();
    json.addProperty("entity", this.entity.name());
    return json;
  }

  @Override
  public MorphContext defaultContext() {
    return new MorphContext(this, false);
  }

  @Override
  public MorphContext deserializeContext(JsonObject json) {
    boolean enabled = json.get("enabled").getAsBoolean();
    return new MorphContext(this, enabled);
  }

  @Override
  public boolean isAllowedInMatches() {
    return false;
  }
}
