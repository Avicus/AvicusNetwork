package net.avicus.magma.module.gadgets.ranks;

import static net.avicus.magma.module.gadgets.ranks.RankManager.DATE_FORMAT;

import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import lombok.Getter;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizedDate;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.magma.database.model.impl.Rank;
import net.avicus.magma.module.gadgets.EmptyGadgetContext;
import net.avicus.magma.module.gadgets.Gadget;
import net.avicus.magma.module.gadgets.GadgetManager;
import net.avicus.magma.util.MagmaTranslations;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Getter
public class RankGadget implements Gadget<EmptyGadgetContext<RankGadget>> {

  private final Rank rank;
  private final Optional<Date> expires;

  public RankGadget(Rank rank, Optional<Date> expires) {
    this.rank = rank;
    this.expires = expires;
  }

  @Override
  public Localizable getName() {
    return MagmaTranslations.GUI_RANK_GADGET.with(new UnlocalizedText(this.rank.getName()));
  }

  @Override
  public ItemStack icon(Locale locale) {
    ItemStack stack = new ItemStack(Material.COMMAND_MINECART);
    ItemMeta meta = stack.getItemMeta();
    meta.setDisplayName(getName().translate(locale).toLegacyText());
    this.expires.ifPresent(e -> meta.setLore(Arrays.asList("This rank will expire in",
        new LocalizedDate(e).translate(locale).toLegacyText())));
    stack.setItemMeta(meta);
    return stack;
  }

  @Override
  public JsonObject serialize() {
    JsonObject json = new JsonObject();
    json.addProperty("rank", this.rank.getId());
    expires.ifPresent(e -> json.addProperty("expires", DATE_FORMAT.format(e)));
    return json;
  }

  @Override
  public EmptyGadgetContext<RankGadget> defaultContext() {
    return new EmptyGadgetContext<RankGadget>(this);
  }

  @Override
  public GadgetManager getManager() {
    return RankManager.INSTANCE;
  }

  @Override
  public EmptyGadgetContext<RankGadget> deserializeContext(JsonObject json) {
    return defaultContext();
  }

  @Override
  public boolean isAllowedInMatches() {
    return true;
  }
}
