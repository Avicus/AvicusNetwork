package net.avicus.magma.module.gadgets.crates;

import com.google.gson.JsonObject;
import net.avicus.magma.NetworkIdentification;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.module.gadgets.GadgetContext;
import net.avicus.magma.module.gadgets.GadgetManager;
import net.avicus.magma.module.gadgets.Gadgets;
import net.avicus.magma.module.gadgets.crates.reveal.CrateRevealMenu;
import net.avicus.magma.util.MagmaTranslations;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CrateManager implements GadgetManager<CrateGadget, CrateContext> {

  public static final CrateManager INSTANCE = new CrateManager();

  private final Gadgets gadgets;

  private CrateManager() {
    this.gadgets = getGadgets();
  }

  @Override
  public String getType() {
    return "crate";
  }

  @Override
  public void init() {

  }

  @Override
  public void onAsyncLoad(User user, CrateContext context) {

  }

  @Override
  public void onAsyncUnload(User user, CrateContext context) {

  }

  @Override
  public void onUse(Player player, CrateContext crate) {
    GadgetContext key = null;
    for (GadgetContext context : gadgets.getGadgets(player.getUniqueId())) {
      if (crate.getGadget().isMatchingKey(context)) {
        key = context;
        break;
      }
    }

    if (key == null) {
      player.sendMessage(
          MagmaTranslations.ERROR_NO_KEY.with(ChatColor.RED, NetworkIdentification.URL + "/shop"));
      return;
    }

    // One-use gadgets
    gadgets.deleteBackpackGadget(crate);
    gadgets.deleteBackpackGadget(key);

    player.closeInventory();
    CrateRevealMenu menu = new CrateRevealMenu(player, crate.getGadget());
    menu.spin();
  }

  @Override
  public CrateGadget deserializeGadget(JsonObject json) {
    TypeManager.CrateType type = TypeManager.getType(json.get("type").getAsString().toUpperCase());
    return new CrateGadget(type);
  }
}
