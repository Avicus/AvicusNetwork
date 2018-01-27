package net.avicus.hook.credits;

import net.avicus.compendium.menu.inventory.InventoryMenu;
import net.avicus.hook.credits.categories.ArrowTrailCategory;
import net.avicus.hook.credits.categories.AtlasCategory;
import net.avicus.hook.credits.categories.BadgeCategory;
import net.avicus.hook.credits.categories.DeviceCategory;
import net.avicus.hook.credits.categories.KeysCategory;
import net.avicus.hook.credits.categories.MorphCategory;
import net.avicus.hook.credits.categories.SoundCategory;
import net.avicus.hook.credits.categories.TrackCategory;
import net.avicus.hook.credits.categories.TrailCategory;
import net.avicus.hook.utils.Messages;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GadgetStore extends InventoryMenu {

  public GadgetStore(Player player) {
    super(player, createTitle(player), 5);

    add(new BadgeCategory.BadgeCategoryRoot(player, this, 10));
    add(new TrackCategory(player, this, 12));
    add(new SoundCategory.SoundCategoryRoot(player, this, 14));
    add(new TrailCategory(player, this, 16));

    add(new ArrowTrailCategory(player, this, 20));

    add(new KeysCategory(player, this, 28));
    add(new DeviceCategory(player, this, 30));
    add(new MorphCategory(player, this, 32));
    add(new AtlasCategory(player, this, 34));
  }

  private static String createTitle(Player player) {
    return Messages.UI_GADGET_STORE.with(ChatColor.DARK_GRAY).translate(player.getLocale())
        .toLegacyText();
  }
}
