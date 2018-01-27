package net.avicus.atlas.module.shop;

import java.util.List;
import lombok.Getter;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.registry.RegisterableObject;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.LocationVariable;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.module.shop.menu.ShopMenu;
import net.avicus.atlas.util.Messages;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * A container of items that can be purchased.
 */
@Getter
public class Shop implements Listener, RegisterableObject<Shop> {

  /**
   * ID of the shop.
   */
  private final String id;
  /**
   * Match that this shop exists inside of.
   */
  private final Match match;
  /**
   * Configuration used to determine how many points should be earned for performing actions.
   */
  private final PointEarnConfig config;
  /**
   * Items in the shop.
   */
  private final List<ShopItem> items;
  /**
   * Name of the shop used for the {@link ShopMenu}.
   */
  private final LocalizedXmlString name;
  /**
   * Check that runs before the shop can be opened by a player.
   */
  private final Check openCheck;
  /**
   * Listener used to reward points.
   */
  private final PointListener pointListener;

  /**
   * Constructor
   *
   * @param id ID of the shop.
   * @param match Match that this shop exists inside of.
   * @param config Configuration used to determine how many points should be earned for performing
   * actions.
   * @param items Items in the shop.
   * @param name Name of the shop used for the {@link ShopMenu}.
   * @param openCheck Check that runs before the shop can be opened by a player.
   * @param pointListener Listener used to reward points.
   */
  public Shop(String id, Match match, PointEarnConfig config,
      List<ShopItem> items, LocalizedXmlString name, Check openCheck,
      PointListener pointListener) {
    this.id = id;
    this.match = match;
    this.config = config;
    this.items = items;
    this.name = name;
    this.openCheck = openCheck;
    this.pointListener = pointListener;
    this.pointListener.setShop(this);
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
  public void playerInteract(final PlayerInteractEvent event) {
    if (event.getAction() != Action.RIGHT_CLICK_AIR
        && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    if (ShopMenu.matches(event.getItem(), this)) {
      CheckContext context = new CheckContext(this.match);
      context.add(new LocationVariable(event.getPlayer().getLocation()));
      context.add(new PlayerVariable(event.getPlayer()));

      if (this.openCheck.test(context).passes()) {
        ShopMenu.create(this, event.getPlayer()).open();
        event.setCancelled(true);
        return;
      }

      event.getPlayer().sendMessage(Messages.UI_SHOP_FAIL.with(ChatColor.RED));
    }
  }

  @Override
  public Shop getObject() {
    return this;
  }
}
