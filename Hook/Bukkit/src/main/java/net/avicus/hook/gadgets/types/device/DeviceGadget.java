package net.avicus.hook.gadgets.types.device;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.avicus.magma.item.ItemTag;
import net.avicus.magma.module.gadgets.AbstractGadget;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@Getter
public abstract class DeviceGadget extends AbstractGadget<DeviceContext> implements Listener {

  public static final ItemTag.Boolean ALLOW_DROP = new ItemTag.Boolean("allow-drop", false);
  public static final ItemTag.Boolean IS_DEVICE = new ItemTag.Boolean("gadget-device", false);
  public static final ItemTag.String DEVICE_TYPE = new ItemTag.String("gadget-device.type", "");
  public static final ItemTag.String DEVICE_DATA = new ItemTag.String("gadget-device.data", "");

  private final boolean infiniteUse;
  private final int maxUsages;

  public DeviceGadget(boolean infiniteUse, int maxUsages) {
    super(DeviceManager.INSTANCE);
    this.infiniteUse = infiniteUse;
    this.maxUsages = maxUsages;
  }

  public abstract ItemStack getStack(DeviceContext context, Player player);

  public abstract boolean handleClick(PlayerInteractEvent event);

  public abstract void removeItem(Player player);

  public abstract void updateItem(DeviceContext context, Player player);

  public abstract String getId();

  @Override
  public DeviceContext defaultContext() {
    return new DeviceContext(this, 0);
  }

  @Override
  public DeviceContext deserializeContext(JsonObject json) {
    int usages = json.get("usages").getAsInt();
    return new DeviceContext(this, usages);
  }

  @Override
  public boolean isAllowedInMatches() {
    return false;
  }
}
