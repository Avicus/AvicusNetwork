package net.avicus.hook.gadgets.types.device.entity;

import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.Random;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.hook.gadgets.types.device.DeviceContext;
import net.avicus.hook.gadgets.types.device.DeviceGadget;
import net.avicus.hook.utils.HookTask;
import net.avicus.hook.utils.Messages;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class EntityGun extends DeviceGadget {

  private final GunType type;
  private final Random random = new Random();

  public EntityGun(boolean infiniteUse, int maxUsages, GunType type) {
    super(infiniteUse, maxUsages);
    this.type = type;
  }

  @Override
  public ItemStack getStack(DeviceContext context, Player player) {
    ItemStack stack = new ItemStack(Material.DIAMOND_HOE);
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
    DeviceGadget.DEVICE_DATA.set(meta, this.type.name());
    stack.setItemMeta(meta);
    return stack;
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

  @Override
  public String getId() {
    return "entity-gun";
  }

  @Override
  public void removeItem(Player player) {
    ItemStack stack = findStack(player);
    if (stack == null) {
      return;
    }

    player.getInventory().remove(stack);
  }

  private Vector randomize(Vector vector) {
    return vector
        .setX(vector.getX() + random.nextDouble() - random.nextDouble())
        .setY(vector.getY() + random.nextDouble() - random.nextDouble())
        .setZ(vector.getZ() + random.nextDouble() - random.nextDouble());
  }

  public boolean handleClick(PlayerInteractEvent event) {
    if (DeviceGadget.DEVICE_DATA.has(event.getItem()) && DeviceGadget.DEVICE_DATA
        .get(event.getItem()).equals(this.type.name())) {

      Vector vector = event.getPlayer().getLocation().getDirection().normalize().multiply(4);
      vector = randomize(vector);

      if (this.type == GunType.EGG) {
        event.getPlayer().launchProjectile(Egg.class, vector);
      } else {
        Entity entity = event.getPlayer().getWorld()
            .spawnEntity(event.getPlayer().getLocation().clone().add(0, 1, 0), this.type.getType());

        entity.setVelocity(vector);

        HookTask.of(() -> entity::remove).later(5 * 20);
      }

      event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.WITHER_SHOOT, 1F,
          0.5F + random.nextFloat());

      return true;
    }
    return false;
  }

  @Override
  public Localizable getName() {
    return Messages.UI_GUN
        .with(ChatColor.GOLD, new UnlocalizedText(this.type.getHuman(), ChatColor.GREEN));
  }

  @Override
  public ItemStack icon(Player player) {
    ItemStack stack = new ItemStack(type.getIcon(), 1,
        type.getIcon() == Material.MONSTER_EGG ? type.getType().getTypeId() : 0);

    ItemMeta meta = stack.getItemMeta();
    meta.setDisplayName(getName().render(player).toLegacyText());
    stack.setItemMeta(meta);
    return stack;
  }

  @Override
  public JsonObject serialize() {
    JsonObject json = new JsonObject();
    json.addProperty("type", "entity-gun");
    json.addProperty("infinite", this.isInfiniteUse());
    json.addProperty("max-usages", this.getMaxUsages());
    json.addProperty("entity", this.type.name());
    return json;
  }
}
