package net.avicus.magma.module.gadgets.crates;

import com.google.gson.JsonObject;
import net.avicus.magma.NetworkIdentification;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.module.gadgets.EmptyGadgetContext;
import net.avicus.magma.module.gadgets.GadgetContext;
import net.avicus.magma.module.gadgets.GadgetManager;
import net.avicus.magma.module.gadgets.Gadgets;
import net.avicus.magma.util.MagmaTranslations;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KeyManager implements GadgetManager<KeyGadget, EmptyGadgetContext<KeyGadget>> {

  public static final KeyManager INSTANCE = new KeyManager();

  private final Gadgets gadgets;

  private KeyManager() {
    this.gadgets = getGadgets();
  }

  @Override
  public String getType() {
    return "key";
  }

  @Override
  public void init() {

  }

  @Override
  public void onAsyncLoad(User user, EmptyGadgetContext<KeyGadget> context) {

  }

  @Override
  public void onAsyncUnload(User user, EmptyGadgetContext<KeyGadget> context) {

  }

  @Override
  public void onUse(Player player, EmptyGadgetContext<KeyGadget> key) {
    for (GadgetContext context : gadgets.getGadgets(player.getUniqueId())) {
      if (key.getGadget().isMatchingCrate(context)) {
        CrateContext crate = (CrateContext) context;
        CrateManager.INSTANCE.onUse(player, crate);
        return;
      }
    }

    player.sendMessage(
        MagmaTranslations.ERROR_NO_KEY.with(ChatColor.RED, NetworkIdentification.URL + "/shop"));
  }

  @Override
  public KeyGadget deserializeGadget(JsonObject json) {
    TypeManager.CrateType crate = TypeManager
        .getType(json.get("crate").getAsString().toUpperCase());
    return new KeyGadget(crate);
  }
}
