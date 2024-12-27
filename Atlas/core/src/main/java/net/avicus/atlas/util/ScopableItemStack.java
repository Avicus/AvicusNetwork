package net.avicus.atlas.util;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.module.projectiles.CustomProjectile;
import net.avicus.atlas.module.projectiles.ProjectilesModule;
import net.avicus.atlas.util.color.TeamColorProvider;
import net.avicus.magma.item.ItemTag;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 * A layer on top of ItemStack that modifies its ItemMeta based on a provided player.
 */
@ToString(exclude = "match")
public class ScopableItemStack {

  public final static ItemTag.Boolean ALLOW_NAMES = new ItemTag.Boolean("atlas.show-name-in-dm",
      false);

  private final Match match;
  private final ItemStack itemStack;
  private final Optional<LocalizedXmlString> customName;
  private final Optional<List<LocalizedXmlString>> lore;
  private final Optional<TeamColorProvider> teamColor;
  private final Optional<CustomProjectile> projectile;

  public ScopableItemStack(Match match,
      ItemStack itemStack) {
    this.match = match;
    this.customName = Optional.empty();
    this.lore = Optional.empty();
    this.itemStack = itemStack;
    this.teamColor = Optional.empty();
    this.projectile = match.getRequiredModule(ProjectilesModule.class)
        .scanProjectileFormat(itemStack);
  }

  public ScopableItemStack(Match match,
      ScopableItemStack item) {
    this.match = match;
    this.customName = item.customName;
    this.lore = item.lore;
    this.itemStack = item.itemStack.clone();
    this.teamColor = item.teamColor;
    this.projectile = item.projectile;
  }

  public ScopableItemStack(Match match,
      ItemStack itemStack,
      Optional<LocalizedXmlString> customName,
      Optional<List<LocalizedXmlString>> lore,
      Optional<TeamColorProvider> teamColor,
      Optional<CustomProjectile> projectile) {
    this.match = match;
    this.customName = customName;
    this.lore = lore;
    this.itemStack = itemStack;
    this.teamColor = teamColor;
    this.projectile = projectile;
  }

  public ItemStack getBaseItemStack() {
    return this.itemStack;
  }

  public ItemStack getItemStack() {
    return getItemStack(Optional.empty());
  }

  public ItemStack getItemStack(Player player) {
    return getItemStack(Optional.of(player));
  }

  private ItemStack getItemStack(Optional<Player> player) {
    ItemStack item = this.itemStack.clone();
    ItemMeta meta = item.getItemMeta();

    if (this.customName.isPresent()) {
      ALLOW_NAMES.set(meta, true);
    }

    if (player.isPresent()) {
      this.customName.ifPresent(n -> meta.setDisplayName(n.render(player.get())));
      this.lore.ifPresent(l -> meta
          .setLore(l.stream().map(s -> s.render(player.get())).collect(Collectors.toList())));
    } else {
      this.customName.ifPresent(n -> meta.setDisplayName(n.renderDefault()));
      this.lore.ifPresent(l -> meta.setLore(
          l.stream().map(LocalizedXmlString::renderDefault).collect(Collectors.toList())));
    }

    boolean colorable = (item.getType() == Material.WOOL ||
        item.getType() == Material.STAINED_CLAY ||
        item.getType() == Material.STAINED_GLASS ||
        item.getType() == Material.STAINED_GLASS_PANE ||
        item.getType() == Material.CARPET);

    // Primary color
    if (this.teamColor.isPresent()) {
      Color primary = this.teamColor.get().getColor(player);
      if (meta instanceof LeatherArmorMeta) {
        LeatherArmorMeta leather = (LeatherArmorMeta) meta;
        leather.setColor(primary);
      } else if (colorable) {
        item.setDurability(this.teamColor.get().getDyeColor(player).getWoolData());
      }
    }

    item.setItemMeta(meta);
    return item;
  }

  public boolean equals(Player player, ItemStack other) {
    Optional<CustomProjectile> module = this.match.getRequiredModule(ProjectilesModule.class)
        .scanProjectileFormat(other);
    ScopableItemStack otherScopable = new ScopableItemStack(this.match, other, Optional.empty(),
        Optional.empty(), this.teamColor, module);
    ItemStack otherItem = otherScopable.getItemStack(player);

    ItemStack item = getItemStack(player);

    return otherItem.isSimilar(item);
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof ScopableItemStack) {
      ScopableItemStack test = (ScopableItemStack) other;
      return test.getItemStack().isSimilar(getItemStack());
    }
    return false;
  }
}
