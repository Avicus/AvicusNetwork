package net.avicus.hook.gadgets.types.device.device;

import com.google.gson.JsonObject;
import java.util.Locale;
import java.util.Random;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.hook.credits.CreditsListener;
import net.avicus.hook.gadgets.types.device.DeviceContext;
import net.avicus.hook.gadgets.types.device.DeviceGadget;
import net.avicus.hook.utils.Messages;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class CreditBlaster extends DeviceGadget {

  private final int capacity;
  private final Random random = new Random();

  public CreditBlaster(int capacity) {
    super(false, (int) Math.ceil(capacity / 10));
    if (capacity % 10 != 0) {
      throw new RuntimeException("Credit blasters must have capacities as multiples of 10.");
    }
    this.capacity = capacity;
  }

  @Override
  public Localizable getName() {
    return Messages.UI_BLASTER.with(ChatColor.AQUA, new LocalizedNumber(this.capacity));
  }

  @Override
  public ItemStack icon(Locale locale) {
    ItemStack stack = new ItemStack(Material.GOLD_AXE);

    ItemMeta meta = stack.getItemMeta();
    meta.setDisplayName(getName().translate(locale).toLegacyText());
    stack.setItemMeta(meta);
    return stack;
  }

  @Override
  public JsonObject serialize() {
    JsonObject json = new JsonObject();
    json.addProperty("type", "credit-blaster");
    json.addProperty("capacity", capacity);
    json.addProperty("infinite", this.isInfiniteUse());
    json.addProperty("max-usages", this.getMaxUsages());
    return json;
  }

  @Override
  public String getId() {
    return "credit-blaster";
  }

  @Override
  public ItemStack getStack(DeviceContext context, Player player) {
    ItemStack stack = new ItemStack(Material.GOLD_AXE);
    ItemMeta meta = stack.getItemMeta();
    meta.setDisplayName(getName().translate(player).toLegacyText());
    DeviceGadget.IS_DEVICE.set(meta, true);
    DeviceGadget.DEVICE_TYPE.set(meta, getId());
    stack.setItemMeta(meta);
    return stack;
  }

  private void dropItem(Location location) {
    Vector vel = location.getDirection().multiply(2);
    vel = randomize(vel);
    ItemStack stack = new ItemStack(Material.GOLDEN_CARROT);
    ItemMeta meta = stack.getItemMeta();
    CreditsListener.GIVE_CREDITS.set(meta, true);
    CreditsListener.CREDIT_AMOUNT.set(meta, 10);
    // Prevent stacking
    meta.setDisplayName(Strings.repeat('!', 5 + (int) (random.nextDouble() * 5)));
    DeviceGadget.ALLOW_DROP.set(meta, true);
    stack.setItemMeta(meta);
    Item item = location.getWorld().dropItem(location, stack);
    item.setVelocity(vel);
  }

  private Vector randomize(Vector vector) {
    return vector
        .setX(vector.getX() + random.nextDouble() - random.nextDouble())
        .setY(vector.getY() + random.nextDouble() - random.nextDouble())
        .setZ(vector.getZ() + random.nextDouble() - random.nextDouble());
  }

  @Override
  public boolean handleClick(PlayerInteractEvent event) {
    if (DeviceGadget.DEVICE_TYPE.get(event.getItem()).equals(getId())) {
      dropItem(event.getPlayer().getLocation());

      return true;
    }

    return false;
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
    String name = getName().translate(player).toLegacyText();
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
