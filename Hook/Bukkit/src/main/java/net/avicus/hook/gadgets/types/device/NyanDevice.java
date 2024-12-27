package net.avicus.hook.gadgets.types.device;

import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import net.avicus.atlas.util.AtlasTask;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.hook.utils.Messages;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class NyanDevice extends DeviceGadget {

  public NyanDevice(boolean infiniteUse, int maxUsages) {
    super(infiniteUse, maxUsages);
  }

  @Override
  public Localizable getName() {
    return Messages.UI_NYAN.with(ChatColor.AQUA);
  }

  @Override
  public ItemStack icon(Player player) {
    ItemStack stack = new ItemStack(Material.WOOL);

    ItemMeta meta = stack.getItemMeta();
    meta.setDisplayName(getName().render(player).toLegacyText());
    stack.setItemMeta(meta);
    return stack;
  }

  @Override
  public JsonObject serialize() {
    JsonObject json = new JsonObject();
    json.addProperty("type", "nyan-cat");
    json.addProperty("infinite", this.isInfiniteUse());
    json.addProperty("max-usages", this.getMaxUsages());
    return json;
  }

  @Override
  public ItemStack getStack(DeviceContext context, Player player) {
    ItemStack stack = new ItemStack(Material.WOOL);
    ItemMeta meta = stack.getItemMeta();
    meta.setDisplayName(getName().render(player).toLegacyText());
    Localizable used = new LocalizedNumber(context.getUsages(), TextStyle.ofColor(ChatColor.WHITE));
    Localizable total = new LocalizedNumber(context.getGadget().getMaxUsages(),
        TextStyle.ofColor(ChatColor.WHITE));
    meta.setLore(Arrays.asList(
        Messages.UI_USAGES.with(ChatColor.GRAY, used, total).render(player).toLegacyText()
    ));
    DeviceGadget.IS_DEVICE.set(meta, true);
    DeviceGadget.DEVICE_TYPE.set(meta, getId());
    stack.setItemMeta(meta);
    return stack;
  }

  @Override
  public String getId() {
    return "nyan-cat";
  }

  @Override
  public boolean handleClick(PlayerInteractEvent event) {
    AtomicInteger time = new AtomicInteger();

    AtomicInteger i = new AtomicInteger();

    AtlasTask.of(() -> {
      if (event.getPlayer() == null || !event.getPlayer().isOnline()
          || time.get() >= 10 * 2 * 5) {
        return;
      }

      if (i.get() > 15) {
        time.set(0);
      }

      final Item item = event.getPlayer().getLocation().getWorld()
          .dropItem(event.getPlayer().getLocation(),
              new ItemStack(Material.WOOL, 1, (short) i.get()));
      item.setVelocity(new Vector(0, 0, 0));
      item.setPickupDelay(9999);

      AtlasTask.of(() -> item::remove).later(60);

      i.incrementAndGet();
      time.addAndGet(10);
    }).repeat(0, 10);

    return true;
  }

  @Override
  public void removeItem(Player player) {
    ItemStack stack = findStack(player);
    if (stack == null) {
      return;
    }

    player.getInventory().remove(stack);
  }

  private ItemStack findStack(Player player) {
    String name = getName().render(player).toLegacyText();
    for (ItemStack stack : player.getInventory().getContents()) {
      if (stack != null && stack.hasItemMeta() && stack.getItemMeta().hasDisplayName() && stack
          .getItemMeta().getDisplayName().equals(name)) {
        return stack;
      }
    }
    return null;
  }

  @Override
  public void updateItem(DeviceContext context, Player player) {
    ItemStack stack = findStack(player);

    if (stack == null) {
      return;
    }

    stack.setDurability(context.usagesToDurability(stack));
    stack.setItemMeta(getStack(context, player).getItemMeta());
  }
}
