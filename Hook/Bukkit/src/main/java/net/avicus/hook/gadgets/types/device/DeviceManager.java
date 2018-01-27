package net.avicus.hook.gadgets.types.device;

import com.google.gson.JsonObject;
import net.avicus.hook.gadgets.types.device.device.CreditBlaster;
import net.avicus.hook.gadgets.types.device.entity.EntityGun;
import net.avicus.hook.gadgets.types.device.entity.GunType;
import net.avicus.hook.utils.Events;
import net.avicus.hook.utils.HookTask;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.module.gadgets.GadgetManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DeviceManager implements GadgetManager<DeviceGadget, DeviceContext> {

  public static final DeviceManager INSTANCE = new DeviceManager();

  private DeviceManager() {

  }

  @Override
  public String getType() {
    return "device";
  }

  @Override
  public void init() {
    // Nothing fancy to do
  }

  @Override
  public void onAsyncLoad(User user, DeviceContext context) {
    // Nothing fancy to do
  }

  @Override
  public void onAsyncUnload(User user, DeviceContext context) {
    // Nothing fancy to do
  }

  @Override
  public void onUse(Player player, DeviceContext context) {
    Events.register(context);
    Events.register(context.getGadget());
    HookTask.of(() -> {
      ItemStack stack = context.getGadget().getStack(context, player);
      if (!player.getInventory().contains(stack)) {
        player.getInventory().addItem(stack);
      }
    }).now();
  }

  @Override
  public DeviceGadget deserializeGadget(JsonObject json) {
    String type = json.get("type").getAsString();
    boolean infinite = json.get("infinite").getAsBoolean();
    int maxUsages = json.get("max-usages").getAsInt();
    switch (type) {
      case "entity-gun":
        GunType entityType = GunType.valueOf(json.get("entity").getAsString().toUpperCase());
        return new EntityGun(infinite, maxUsages, entityType);
      case "nyan-cat":
        return new NyanDevice(infinite, maxUsages);
      case "credit-blaster":
        int capacity = json.get("capacity").getAsInt();
        return new CreditBlaster(capacity);
      default:
        throw new RuntimeException("Unknown device type " + type);
    }
  }
}
