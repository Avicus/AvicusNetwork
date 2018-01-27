package net.avicus.hook.gadgets.types.statreset;

import com.google.gson.JsonObject;
import net.avicus.hook.Hook;
import net.avicus.hook.utils.ConfirmationDialog;
import net.avicus.hook.utils.HookTask;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.module.gadgets.EmptyGadgetContext;
import net.avicus.magma.module.gadgets.GadgetManager;
import net.avicus.magma.module.gadgets.Gadgets;
import net.avicus.magma.network.user.Users;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class StatResetManager implements
    GadgetManager<StatResetGadget, EmptyGadgetContext<StatResetGadget>> {

  public static final StatResetManager INSTANCE = new StatResetManager();

  private final Gadgets gadgets;

  private StatResetManager() {
    this.gadgets = getGadgets();
  }

  @Override
  public String getType() {
    return "stat-reset";
  }

  @Override
  public void init() {

  }

  @Override
  public void onAsyncLoad(User user, EmptyGadgetContext<StatResetGadget> context) {

  }

  @Override
  public void onAsyncUnload(User user, EmptyGadgetContext<StatResetGadget> context) {

  }

  @Override
  public void onUse(Player player, EmptyGadgetContext<StatResetGadget> context) {
    player.closeInventory();

    User user = Users.user(player);

    ConfirmationDialog dialog = new ConfirmationDialog(player, () -> {
      HookTask.of(() -> {
        player.closeInventory();

        player.sendMessage(Messages.GENERIC_RESETTING_STATS.with(ChatColor.YELLOW));

        // Delete gadget
        gadgets.deleteBackpackGadget(context);

        // Hide deaths
        Hook.database().getDeaths().hideStats(user.getId());

        // Hide objectives
        Hook.database().getObjectiveCompletions().hideStats(user.getId());

        // This task may have taken some time...
        if (!player.isOnline()) {
          return;
        }

        // Done!
        player.sendMessage(Messages.GENERIC_STATS_RESET.with(ChatColor.YELLOW));
      }).nowAsync();
    }, () -> {
      player.sendMessage(Messages.GENERIC_STATS_RESET_CANCELLED.with(ChatColor.RED));
      player.closeInventory();
    }, Messages.UI_STATS_RESET.with(ChatColor.DARK_GRAY));

    dialog.open();
  }

  @Override
  public StatResetGadget deserializeGadget(JsonObject json) {
    return new StatResetGadget();
  }
}
