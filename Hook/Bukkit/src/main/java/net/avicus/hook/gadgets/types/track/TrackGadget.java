package net.avicus.hook.gadgets.types.track;

import com.google.gson.JsonObject;
import java.util.Arrays;

import lombok.Getter;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.module.gadgets.AbstractGadget;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TrackGadget extends AbstractGadget<TrackContext> {

  @Getter
  private final TrackType type;
  @Getter
  private final int maxUsages;

  public TrackGadget(TrackType type, int maxUsages) {
    super(TrackManager.INSTANCE);
    this.type = type;
    this.maxUsages = maxUsages;
  }

  @Override
  public Localizable getName() {
    Localizable name = new UnlocalizedText(this.type.getName(), TextStyle.ofColor(ChatColor.GREEN));
    return Messages.UI_TRACK.with(ChatColor.WHITE, name);
  }

  @Override
  public ItemStack icon(Player player) {
    ItemStack stack = new ItemStack(this.type.getMaterial());
    ItemMeta meta = stack.getItemMeta();

    meta.setDisplayName(getName().render(player).toLegacyText());

    Localizable usages = new LocalizedNumber(0, TextStyle.ofColor(ChatColor.WHITE));
    Localizable max = new LocalizedNumber(this.maxUsages, TextStyle.ofColor(ChatColor.WHITE));
    meta.setLore(Arrays.asList(
        Messages.UI_USAGES.with(ChatColor.GRAY, usages, max).render(player).toLegacyText(),
        Messages.UI_CLICK_TRACK.with(ChatColor.WHITE).render(player).toLegacyText()
    ));

    stack.setItemMeta(meta);
    return stack;
  }

  @Override
  public JsonObject serialize() {
    JsonObject json = new JsonObject();
    json.addProperty("type", this.type.name());
    json.addProperty("max-usages", this.maxUsages);
    return json;
  }

  @Override
  public TrackContext defaultContext() {
    return new TrackContext(this, 0);
  }

  @Override
  public TrackContext deserializeContext(JsonObject json) {
    int usages = json.get("usages").getAsInt();
    return new TrackContext(this, usages);
  }

  @Override
  public boolean isAllowedInMatches() {
    return false;
  }
}
