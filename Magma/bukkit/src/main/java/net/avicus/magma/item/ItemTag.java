package net.avicus.magma.item;

import com.google.common.base.Preconditions;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemTag {

  public static abstract class Base<T> {

    protected final java.lang.String name;
    protected final T defaultValue;

    protected Base(java.lang.String name, T defaultValue) {
      this.name = name;
      this.defaultValue = defaultValue;
    }

    protected abstract boolean hasPrimitive(NBTTagCompound tag);

    protected abstract T getPrimitive(NBTTagCompound tag);

    protected abstract void setPrimitive(NBTTagCompound tag, T value);

    protected void clearPrimitive(NBTTagCompound tag) {
      tag.remove(name);
    }

    public boolean has(@Nullable NBTTagCompound tag) {
      return tag != null && hasPrimitive(tag);
    }

    public boolean has(@Nullable ItemMeta meta) {
      return has(NBTUtils.getCustomTag(meta));
    }

    public boolean has(@Nullable ItemStack stack) {
      return has(NBTUtils.getCustomTag(stack));
    }

    public T get(@Nullable NBTTagCompound tag) {
      if (tag != null && hasPrimitive(tag)) {
        return getPrimitive(tag);
      } else {
        return defaultValue;
      }
    }

    public T get(@Nullable ItemMeta meta) {
      return get(NBTUtils.getCustomTag(meta));
    }

    public T get(@Nullable ItemStack stack) {
      return get(NBTUtils.getCustomTag(stack));
    }

    public void set(NBTTagCompound tag, T value) {
      if (Objects.equals(value, defaultValue)) {
        clear(tag);
      } else {
        setPrimitive(tag, Preconditions.checkNotNull(value));
      }
    }

    public void set(ItemMeta meta, T value) {
      set(NBTUtils.getOrCreateCustomTag(meta), value);
    }

    public void set(ItemStack stack, T value) {
      ItemUtils.updateMeta(stack, meta -> set(meta, value));
    }

    public void clear(@Nullable NBTTagCompound tag) {
      if (tag != null) {
        clearPrimitive(tag);
      }
    }

    public void clear(@Nullable ItemMeta meta) {
      clear(NBTUtils.getCustomTag(meta));
      NBTUtils.prune(meta);
    }

    public void clear(@Nullable ItemStack stack) {
      ItemUtils.updateMetaIfPresent(stack, this::clear);
    }
  }

  public static class Boolean extends Base<java.lang.Boolean> {

    public Boolean(java.lang.String name, java.lang.Boolean defaultValue) {
      super(name, defaultValue);
    }

    @Override
    protected boolean hasPrimitive(NBTTagCompound tag) {
      return tag.hasKeyOfType(name, 1);
    }

    @Override
    protected java.lang.Boolean getPrimitive(NBTTagCompound tag) {
      return tag.getBoolean(name);
    }

    @Override
    protected void setPrimitive(NBTTagCompound tag, java.lang.Boolean value) {
      tag.setBoolean(name, value);
    }
  }

  public static class Integer extends Base<java.lang.Integer> {

    public Integer(java.lang.String name, java.lang.Integer defaultValue) {
      super(name, defaultValue);
    }

    @Override
    protected boolean hasPrimitive(NBTTagCompound tag) {
      return tag.hasKeyOfType(name, 3);
    }

    @Override
    protected java.lang.Integer getPrimitive(NBTTagCompound tag) {
      return tag.getInt(name);
    }

    @Override
    protected void setPrimitive(NBTTagCompound tag, java.lang.Integer value) {
      tag.setInt(name, value);
    }
  }

  public static class Double extends Base<java.lang.Double> {

    public Double(java.lang.String name, java.lang.Double defaultValue) {
      super(name, defaultValue);
    }

    @Override
    protected boolean hasPrimitive(NBTTagCompound tag) {
      return tag.hasKeyOfType(name, 99);
    }

    @Override
    protected java.lang.Double getPrimitive(NBTTagCompound tag) {
      return tag.getDouble(name);
    }

    @Override
    protected void setPrimitive(NBTTagCompound tag, java.lang.Double value) {
      tag.setDouble(name, value);
    }
  }

  public static class Float extends Base<java.lang.Float> {

    public Float(java.lang.String name, java.lang.Float defaultValue) {
      super(name, defaultValue);
    }

    @Override
    protected boolean hasPrimitive(NBTTagCompound tag) {
      return tag.hasKeyOfType(name, 5);
    }

    @Override
    protected java.lang.Float getPrimitive(NBTTagCompound tag) {
      return tag.getFloat(name);
    }

    @Override
    protected void setPrimitive(NBTTagCompound tag, java.lang.Float value) {
      tag.setFloat(name, value);
    }
  }

  public static class String extends Base<java.lang.String> {

    public String(java.lang.String name, java.lang.String defaultValue) {
      super(name, defaultValue);
    }

    @Override
    protected boolean hasPrimitive(NBTTagCompound tag) {
      return tag.hasKeyOfType(name, 8);
    }

    @Override
    protected java.lang.String getPrimitive(NBTTagCompound tag) {
      return tag.getString(name);
    }

    @Override
    protected void setPrimitive(NBTTagCompound tag, java.lang.String value) {
      tag.setString(name, value);
    }
  }
}
