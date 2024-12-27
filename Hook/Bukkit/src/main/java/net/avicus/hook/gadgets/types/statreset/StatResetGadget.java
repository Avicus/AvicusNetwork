package net.avicus.hook.gadgets.types.statreset;

import com.google.gson.JsonObject;
import java.util.Arrays;

import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.magma.module.gadgets.EmptyGadgetContext;
import net.avicus.magma.module.gadgets.Gadget;
import net.avicus.magma.module.gadgets.GadgetManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class StatResetGadget implements Gadget<EmptyGadgetContext<StatResetGadget>> {

  @Override
  public GadgetManager getManager() {
    return StatResetManager.INSTANCE;
  }

  @Override
  public Localizable getName() {
    return new UnlocalizedText("Stats Reset");
  }

  @Override
  public ItemStack icon(Player player) {
    ItemStack stack = new ItemStack(Material.REDSTONE);
    ItemMeta meta = stack.getItemMeta();

    meta.setDisplayName(ChatColor.DARK_RED + "Stats Reset");
    meta.setLore(Arrays.asList(
        ChatColor.WHITE + "Using this gadget will reset all",
        ChatColor.WHITE + "of your kills, deaths and objectives."
    ));

    stack.setItemMeta(meta);
    return stack;
  }

  @Override
  public JsonObject serialize() {
    return new JsonObject();
  }

  @Override
  public EmptyGadgetContext<StatResetGadget> defaultContext() {
    return new EmptyGadgetContext<>(this);
  }

  @Override
  public EmptyGadgetContext<StatResetGadget> deserializeContext(JsonObject json) {
    return defaultContext();
  }

  @Override
  public boolean isAllowedInMatches() {
    return true;
  }
}
