package net.avicus.magma.module.gadgets.crates;

import com.google.gson.JsonObject;
import java.util.Arrays;

import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.magma.module.gadgets.AbstractGadget;
import net.avicus.magma.module.gadgets.EmptyGadgetContext;
import net.avicus.magma.module.gadgets.Gadget;
import net.avicus.magma.module.gadgets.GadgetContext;
import net.avicus.magma.util.MagmaTranslations;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KeyGadget extends AbstractGadget<EmptyGadgetContext<KeyGadget>> {

  private final TypeManager.CrateType crate;

  public KeyGadget(TypeManager.CrateType crate) {
    super(KeyManager.INSTANCE);
    this.crate = crate;
  }

  public boolean isMatchingCrate(Gadget gadget) {
    return gadget instanceof CrateGadget && ((CrateGadget) gadget).getType() == this.crate;
  }

  public boolean isMatchingCrate(GadgetContext context) {
    return isMatchingCrate(context.getGadget());
  }

  @Override
  public Localizable getName() {
    return new UnlocalizedText(this.crate.getKeyName(), ChatColor.WHITE);
  }

  @Override
  public ItemStack icon(Player player) {
    ItemStack stack = new ItemStack(Material.FEATHER);
    ItemMeta meta = stack.getItemMeta();

    meta.setDisplayName(getName().render(player).toLegacyText());

    meta.setLore(Arrays.asList(
        MagmaTranslations.GUI_CLICK_KEY.with(ChatColor.GRAY).render(player).toLegacyText()
    ));

    stack.setItemMeta(meta);
    return stack;
  }

  @Override
  public JsonObject serialize() {
    JsonObject json = new JsonObject();
    json.addProperty("crate", this.crate.getId());
    return json;
  }

  @Override
  public EmptyGadgetContext<KeyGadget> defaultContext() {
    return new EmptyGadgetContext<>(this);
  }

  @Override
  public EmptyGadgetContext<KeyGadget> deserializeContext(JsonObject json) {
    return new EmptyGadgetContext<>(this);
  }

  @Override
  public boolean isAllowedInMatches() {
    return true;
  }
}
