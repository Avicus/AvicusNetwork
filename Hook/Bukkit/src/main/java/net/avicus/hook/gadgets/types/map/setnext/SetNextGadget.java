package net.avicus.hook.gadgets.types.map.setnext;

import com.google.gson.JsonObject;
import net.avicus.atlas.match.Match;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.hook.gadgets.types.map.AtlasGadget;
import net.avicus.hook.utils.HookTask;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.module.gadgets.EmptyGadgetContext;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SetNextGadget extends AtlasGadget {

  @Override
  public Localizable getName() {
    return Messages.UI_SN.with(ChatColor.LIGHT_PURPLE);
  }

  @Override
  public ItemStack icon(Player player) {
    ItemStack stack = new ItemStack(Material.EMPTY_MAP);
    ItemMeta meta = stack.getItemMeta();

    meta.setDisplayName(getName().render(player).toLegacyText());

    stack.setItemMeta(meta);
    return stack;
  }

  @Override
  public boolean onUse(Player player, Match match, EmptyGadgetContext<AtlasGadget> context) {
    player.closeInventory();

    HookTask.of(() -> new MapSelectionMenu(player, "Select a Map", 5, this, context)
        .open()).now();

    // Removed by items on click.
    return false;
  }

  @Override
  public JsonObject serialize() {
    JsonObject json = new JsonObject();
    json.addProperty("type", "set-next");
    return json;
  }
}
