package net.avicus.hook.credits;

import net.avicus.hook.utils.Messages;
import net.avicus.magma.event.user.AsyncHookLoginEvent;
import net.avicus.magma.event.user.AsyncHookLogoutEvent;
import net.avicus.magma.item.ItemTag;
import net.avicus.magma.item.ItemTag.Boolean;
import net.avicus.magma.item.ItemTag.Integer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class CreditsListener implements Listener {

  public static ItemTag.Boolean GIVE_CREDITS = new Boolean("GiveCredits", false);
  public static ItemTag.Integer CREDIT_AMOUNT = new Integer("CreditAmount", 0);

  @EventHandler
  public void onAsyncHookLogin(AsyncHookLoginEvent event) {
    Credits.load(event.getUser());
  }

  @EventHandler
  public void onAsyncHookLogin(AsyncHookLogoutEvent event) {
    Credits.unload(event.getUser());
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onItem(PlayerPickupItemEvent event) {
    ItemStack stack = event.getItem().getItemStack();

    if (!GIVE_CREDITS.has(stack)) {
      return;
    }

    int amount = CREDIT_AMOUNT.get(stack);

    event.getItem().remove();
    event.setCancelled(true);

    Credits.reward(event.getPlayer(), amount, Messages.UI_REWARD_ITEM, false);
    event.setCancelled(true);
  }
}
