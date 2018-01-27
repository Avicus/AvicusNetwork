package net.avicus.hook.gadgets.types.device;

import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.Locale;
import lombok.Getter;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.hook.utils.Events;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.module.gadgets.AbstractGadgetContext;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DeviceContext extends AbstractGadgetContext<DeviceGadget> implements Listener {

  @Getter
  private int usages;

  public DeviceContext(DeviceGadget gadget, int usages) {
    super(gadget);
    this.usages = usages;
  }

  public boolean hasUsages() {
    if (getGadget().isInfiniteUse()) {
      return true;
    }
    return this.usages < this.getGadget().getMaxUsages();
  }

  public short usagesToDurability(ItemStack stack) {
    if (getGadget().isInfiniteUse()) {
      return 0;
    }

    short max = stack.getType().getMaxDurability();
    double usagePercent = (double) this.usages / getGadget().getMaxUsages();
    return (short) (max * usagePercent);
  }

  @Override
  public JsonObject serialize() {
    JsonObject json = new JsonObject();
    json.addProperty("usages", this.usages);
    return json;
  }

  @Override
  public ItemStack icon(Locale locale) {
    ItemStack stack = super.icon(locale);
    ItemMeta meta = stack.getItemMeta();
    Localizable used = new LocalizedNumber(this.usages, TextStyle.ofColor(ChatColor.WHITE));
    Localizable total = new LocalizedNumber(getGadget().getMaxUsages(),
        TextStyle.ofColor(ChatColor.WHITE));
    meta.setLore(Arrays.asList(
        Messages.UI_USAGES.with(ChatColor.GRAY, used, total).translate(locale).toLegacyText()
    ));
    stack.setItemMeta(meta);
    return stack;
  }


  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerClick(PlayerInteractEvent event) {
    if (event.getItem() != null && DeviceGadget.IS_DEVICE.has(event.getItem()) &&
        DeviceGadget.DEVICE_TYPE.has(event.getItem()) && DeviceGadget.DEVICE_TYPE
        .get(event.getItem()).equals(getGadget().getId())) {
      event.setCancelled(true);
      if (getGadget().handleClick(event)) {
        this.usages++;
        if (hasUsages()) {
          getManager().getGadgets().updateBackpackGadget(this);
          getGadget().updateItem(this, event.getPlayer());
        } else {
          getManager().getGadgets().deleteBackpackGadget(this);
          getGadget().removeItem(event.getPlayer());
          Events.unregister(this);
          Events.unregister(getGadget());
        }
      }
    }
  }
}
