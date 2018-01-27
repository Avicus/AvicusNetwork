package net.avicus.magma.module.gadgets.crates;

import com.google.gson.JsonObject;
import java.util.Locale;
import lombok.Getter;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.WeightedRandomizer;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.magma.module.gadgets.AbstractGadget;
import net.avicus.magma.module.gadgets.Gadget;
import net.avicus.magma.module.gadgets.GadgetContext;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CrateGadget extends AbstractGadget<CrateContext> {

  @Getter
  private final TypeManager.CrateType type;

  public CrateGadget(TypeManager.CrateType type) {
    super(CrateManager.INSTANCE);
    this.type = type;
  }

  public boolean isMatchingKey(Gadget gadget) {
    return gadget instanceof KeyGadget && ((KeyGadget) gadget).isMatchingCrate(this);
  }

  public boolean isMatchingKey(GadgetContext context) {
    return isMatchingKey(context.getGadget());
  }

  public WeightedRandomizer<Gadget> getRandomizer() {
    return this.type.getRandomizer();
  }

  @Override
  public Localizable getName() {
    return new UnlocalizedText(this.type.getCrateName(), TextStyle.ofColor(ChatColor.WHITE));
  }

  @Override
  public ItemStack icon(Locale locale) {
    ItemStack stack = new ItemStack(Material.CHEST);
    ItemMeta meta = stack.getItemMeta();

    meta.setDisplayName(getName().translate(locale).toLegacyText());

    stack.setItemMeta(meta);
    return stack;
  }

  @Override
  public JsonObject serialize() {
    JsonObject json = new JsonObject();
    json.addProperty("type", this.type.getId());
    return json;
  }

  @Override
  public CrateContext defaultContext() {
    return new CrateContext(this);
  }

  @Override
  public CrateContext deserializeContext(JsonObject json) {
    return new CrateContext(this);
  }

  @Override
  public boolean isAllowedInMatches() {
    return true;
  }
}
