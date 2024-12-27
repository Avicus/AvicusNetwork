package net.avicus.atlas.module.observer.menu.item;

import net.avicus.atlas.module.observer.menu.ObserverMenu;
import net.avicus.atlas.module.observer.menu.ObserverMenuItem;
import net.avicus.atlas.util.Translations;
import net.avicus.magma.item.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class NightVisionItem extends ObserverMenuItem {

  public NightVisionItem(final Player viewer, final ObserverMenu parent, final int index) {
    super(viewer, parent, index);
  }

  @Override
  public void onClick(final ClickType type) {
    final boolean status = this.viewer.hasPotionEffect(PotionEffectType.NIGHT_VISION);
    if (status) {
      this.viewer.removePotionEffect(PotionEffectType.NIGHT_VISION);
    }
    if (!status) {
      this.viewer
          .addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Short.MAX_VALUE, 2));
    }

    this.parent.update(false);
  }

  @Override
  public ItemStack getItemStack() {
    return ItemStackBuilder.start()
        .material(Material.POTION)
        .durability((short) 8230)
        .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS)
        .displayName(Translations.MODULE_OBSERVER_MENU_ITEM_NIGHTVISION_NAME.with(ChatColor.AQUA)
            .render(this.viewer))
        .lore(Translations.MODULE_OBSERVER_MENU_ITEM_NIGHTVISION_DESCRIPTION.with(ChatColor.GRAY)
            .render(this.viewer), MAX_LENGTH)
        .lore("")
        .lore(TWO_PART_FORMAT.with(Translations.MODULE_OBSERVER_MENU_CURRENT.with(ChatColor.GRAY),
            Translations.bool(this.viewer.hasPotionEffect(PotionEffectType.NIGHT_VISION)))
            .render(this.viewer))
        .build();
  }
}
