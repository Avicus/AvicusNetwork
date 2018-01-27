package net.avicus.magma.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.avicus.magma.util.StringUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemStackBuilder {

  @Nullable
  private Material material;
  private Short durability;
  private int quantity = 1;
  private ItemFlag[] flags;
  private ItemTag.Base[] tags;
  @Nullable
  private String displayName;
  private List<String> lore;
  private boolean locked = false;
  private boolean unShareable = false;

  public static ItemStackBuilder start() {
    return new ItemStackBuilder();
  }

  public ItemStackBuilder material(Material material) {
    this.material = material;
    return this;
  }

  public ItemStackBuilder durability(short durability) {
    this.durability = durability;
    return this;
  }

  public ItemStackBuilder quantity(int quantity) {
    this.quantity = quantity;
    return this;
  }

  public ItemStackBuilder displayName(BaseComponent displayName) {
    this.displayName = displayName.toLegacyText();
    return this;
  }

  public ItemStackBuilder displayName(String displayName) {
    this.displayName = displayName;
    return this;
  }

  public ItemStackBuilder lore(BaseComponent component) {
    return this.lore(component.toLegacyText());
  }

  public ItemStackBuilder lore(String string) {
    if (this.lore == null) {
      this.lore = new ArrayList<>();
    }
    this.lore.add(string);
    return this;
  }

  public ItemStackBuilder lore(BaseComponent component, int maxLength) {
    return this.lore(component.toLegacyText(), maxLength);
  }

  public ItemStackBuilder lore(String string, int maxLength) {
    if (this.lore == null) {
      this.lore = new ArrayList<>();
    }
    StringUtil.wrapLoreWithLastColors(this.lore, maxLength, string);
    return this;
  }

  public ItemStackBuilder flags(ItemFlag... flags) {
    this.flags = flags;
    return this;
  }

  public ItemStackBuilder locked() {
    this.locked = true;
    return this;
  }

  public ItemStackBuilder unShareable() {
    this.unShareable = true;
    return this;
  }

  public ItemStackBuilder tags(ItemTag.Base... tags) {
    this.tags = tags;
    return this;
  }

  public ItemStack build() {
    final ItemStack stack = new ItemStack(this.material);
    if (this.durability != null) {
      stack.setDurability(this.durability);
    }
    stack.setAmount(this.quantity);
    final ItemMeta meta = stack.getItemMeta();
    if (this.displayName != null) {
      meta.setDisplayName(ChatColor.RESET + this.displayName);
    }
    if (this.lore != null) {
      meta.setLore(this.lore);
    }
    if (this.flags != null) {
      meta.addItemFlags(this.flags);
    }
    if (this.tags != null) {
      Arrays.stream(this.tags).forEach(t -> t.set(meta, t.defaultValue));
    }
    if (this.locked) {
      LockingSharingListener.LOCKED.set(meta, true);
    }
    if (this.unShareable) {
      LockingSharingListener.UN_SHAREABLE.set(meta, true);
    }
    stack.setItemMeta(meta);
    return stack;
  }
}
