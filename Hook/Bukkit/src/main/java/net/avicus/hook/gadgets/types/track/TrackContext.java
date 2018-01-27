package net.avicus.hook.gadgets.types.track;

import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.Locale;
import lombok.Getter;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.module.gadgets.AbstractGadgetContext;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TrackContext extends AbstractGadgetContext<TrackGadget> {

  @Getter
  private int usages;

  public TrackContext(TrackGadget gadget, int usages) {
    super(gadget);
    this.usages = usages;
  }

  @Override
  public ItemStack icon(Locale locale) {
    ItemStack stack = super.icon(locale);
    ItemMeta meta = stack.getItemMeta();
    Localizable used = new LocalizedNumber(this.usages, TextStyle.ofColor(ChatColor.WHITE));
    Localizable total = new LocalizedNumber(getGadget().getMaxUsages(),
        TextStyle.ofColor(ChatColor.WHITE));
    meta.setLore(Arrays.asList(
        Messages.UI_USAGES.with(ChatColor.GRAY, used, total).translate(locale).toLegacyText(),
        Messages.UI_CLICK_TRACK.with(ChatColor.WHITE).translate(locale).toLegacyText()
    ));
    stack.setItemMeta(meta);
    return stack;
  }

  public void play(Player playing) {
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (!player.canSee(playing)) {
        continue;
      }

      player.sendMessage(
          Messages.GENERIC_TRACK_PLAYING.with(ChatColor.GOLD, playing.getDisplayName()));
      player.playEffect(player.getLocation(), Effect.RECORD_PLAY,
          getGadget().getType().getMaterial().getId());
    }
    this.usages++;
  }

  public boolean hasUsages() {
    return this.usages < this.getGadget().getMaxUsages();
  }

  @Override
  public JsonObject serialize() {
    JsonObject json = new JsonObject();
    json.addProperty("usages", this.usages);
    return json;
  }
}
