package net.avicus.atlas.module.observer.menu.item;

import net.avicus.atlas.module.observer.menu.ObserverMenu;
import net.avicus.atlas.module.observer.menu.ObserverMenuItem;
import net.avicus.atlas.util.Translations;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.magma.item.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public final class GameModeItem extends ObserverMenuItem {

  public GameModeItem(final Player viewer, final ObserverMenu parent, final int index) {
    super(viewer, parent, index);
  }

  @Override
  public void onClick(final ClickType type) {
    final GameMode mode = this.viewer.getGameMode();
    switch (mode) {
      case ADVENTURE:
        this.viewer.setGameMode(GameMode.SPECTATOR);
        break;
      case SPECTATOR:
        this.viewer.setGameMode(GameMode.ADVENTURE);
        this.viewer.setAllowFlight(true);
        this.viewer.setFlying(true);
        break;
    }

    this.parent.update(false);
  }

  @Override
  public ItemStack getItemStack() {
    final ItemStackBuilder builder = ItemStackBuilder.start()
        .material(Material.EYE_OF_ENDER)
        .flags(ItemFlag.HIDE_ATTRIBUTES)
        .displayName(Translations.MODULE_OBSERVER_MENU_ITEM_GAMEMODE_NAME.with(ChatColor.AQUA)
            .translate(this.viewer))
        .lore(Translations.MODULE_OBSERVER_MENU_ITEM_GAMEMODE_DESCRIPTION.with(ChatColor.GRAY)
            .translate(this.viewer), MAX_LENGTH)
        .lore("")
        .lore(TWO_PART_FORMAT.with(Translations.MODULE_OBSERVER_MENU_CURRENT.with(ChatColor.GRAY),
            new UnlocalizedText(this.viewer.getGameMode().name().toLowerCase()))
            .translate(this.viewer));
    if (this.viewer.getGameMode() == GameMode.SPECTATOR) {
      builder
          .lore("")
          .lore(Translations.MODULE_OBSERVER_MENU_COMMAND
              .with(ChatColor.YELLOW, new UnlocalizedText("/obs")).translate(this.viewer));
    }
    return builder.build();
  }
}
