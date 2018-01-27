package net.avicus.hook.gadgets.types.badge;

import com.google.common.collect.ArrayListMultimap;
import com.google.gson.JsonObject;
import java.util.Optional;
import java.util.UUID;
import lombok.ToString;
import net.avicus.hook.utils.Events;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.module.gadgets.GadgetManager;
import net.avicus.magma.module.gadgets.Gadgets;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@ToString
public class BadgeManager implements GadgetManager<BadgeGadget, BadgeContext>, Listener {

  public static final BadgeManager INSTANCE = new BadgeManager();

  private final ArrayListMultimap<UUID, BadgeContext> badges;

  private final Gadgets gadgets;

  private BadgeManager() {
    this.badges = ArrayListMultimap.create();
    this.gadgets = getGadgets();
  }

  private Optional<BadgeContext> getEnabledBadge(UUID user) {
    for (BadgeContext badge : this.badges.get(user)) {
      if (badge.isEnabled()) {
        return Optional.of(badge);
      }
    }
    return Optional.empty();
  }

  @EventHandler(ignoreCancelled = true)
  public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
    BadgeContext badge = getEnabledBadge(event.getPlayer().getUniqueId()).orElse(null);
    String symbol = badge == null ? "" : badge.getDisplay();

    String format = ChatColor.GRAY + "«" + ChatColor.WHITE + "%1$s" + symbol + ChatColor.GRAY + "»"
        + ChatColor.WHITE + " %2$s";
    event.setFormat(format);
  }

  @Override
  public void init() {
    Events.register(this);
  }

  @Override
  public String getType() {
    return "badge";
  }

  @Override
  public void onAsyncLoad(User user, BadgeContext context) {
    this.badges.put(user.getUniqueId(), context);
  }

  @Override
  public void onAsyncUnload(User user, BadgeContext context) {
    this.badges.remove(user.getUniqueId(), context);
  }

  @Override
  public void onUse(Player player, BadgeContext context) {
    if (context.isEnabled()) {
      // Disable this badge
      context.setEnabled(false);
    } else {
      // Disable all enabled badges
      this.badges.get(player.getUniqueId())
          .stream()
          .filter(BadgeContext::isEnabled)
          .forEach(badge -> {
            badge.setEnabled(false);
            gadgets.updateBackpackGadget(badge);
          });

      // Enable this badge
      context.setEnabled(true);
    }

    gadgets.updateBackpackGadget(context);
  }

  @Override
  public BadgeGadget deserializeGadget(JsonObject json) {
    BadgeSymbol symbol = BadgeSymbol.valueOf(json.get("symbol").getAsString().toUpperCase());
    ChatColor color = ChatColor.valueOf(json.get("color").getAsString().toUpperCase());
    return new BadgeGadget(symbol, color);
  }
}
