package net.avicus.hook.gadgets.types.badge;

import com.google.gson.JsonObject;
import java.util.Locale;
import lombok.ToString;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.module.gadgets.AbstractGadget;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@ToString
public class BadgeGadget extends AbstractGadget<BadgeContext> {

  private final BadgeSymbol symbol;
  private final ChatColor color;

  public BadgeGadget(BadgeSymbol symbol, ChatColor color) {
    super(BadgeManager.INSTANCE);
    this.symbol = symbol;
    this.color = color;
  }

  public String getDisplay() {
    return this.color + "" + this.symbol;
  }

  @Override
  public Localizable getName() {
    Localizable symbol = new UnlocalizedText(this.symbol + "", this.color);
    return Messages.UI_BADGE.with(ChatColor.WHITE, symbol);
  }

  @Override
  public ItemStack icon(Locale locale) {
    ItemStack stack = new ItemStack(Material.NETHER_STAR);
    ItemMeta meta = stack.getItemMeta();

    meta.setDisplayName(getName().translate(locale).toLegacyText());

    // Todo: Add example of how it would look with your name? <funkystudios*>: Hey, chat!

    stack.setItemMeta(meta);
    return stack;
  }

  @Override
  public JsonObject serialize() {
    JsonObject json = new JsonObject();
    json.addProperty("symbol", this.symbol.name());
    json.addProperty("color", this.color.name());
    return json;
  }

  @Override
  public BadgeContext defaultContext() {
    return new BadgeContext(this, false);
  }

  @Override
  public BadgeContext deserializeContext(JsonObject json) {
    boolean enabled = json.get("enabled").getAsBoolean();
    return new BadgeContext(this, enabled);
  }

  @Override
  public boolean isAllowedInMatches() {
    return true;
  }
}
