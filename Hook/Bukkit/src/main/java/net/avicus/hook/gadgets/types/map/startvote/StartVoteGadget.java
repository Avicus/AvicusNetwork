package net.avicus.hook.gadgets.types.map.startvote;

import com.google.gson.JsonObject;

import java.util.function.Function;
import lombok.Setter;
import net.avicus.atlas.match.Match;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.hook.gadgets.types.map.AtlasGadget;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.module.gadgets.EmptyGadgetContext;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class StartVoteGadget extends AtlasGadget {

  @Setter
  static Function<Pair<Player, Match>, Boolean> onUse;

  @Override
  public Localizable getName() {
    return Messages.UI_START_VOTE.with(ChatColor.DARK_PURPLE);
  }

  @Override
  public ItemStack icon(Player player) {
    ItemStack stack = new ItemStack(Material.MAP);
    ItemMeta meta = stack.getItemMeta();

    meta.setDisplayName(getName().render(player).toLegacyText());

    stack.setItemMeta(meta);
    return stack;
  }

  @Override
  public boolean onUse(Player player, Match match, EmptyGadgetContext<AtlasGadget> context) {
    player.closeInventory();

    return onUse.apply(Pair.of(player, match));
  }

  @Override
  public JsonObject serialize() {
    JsonObject json = new JsonObject();
    json.addProperty("type", "start-vote");
    return json;
  }
}
