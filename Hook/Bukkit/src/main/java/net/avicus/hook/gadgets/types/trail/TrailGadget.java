package net.avicus.hook.gadgets.types.trail;

import com.google.gson.JsonObject;
import java.util.List;

import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.magma.module.gadgets.AbstractGadget;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TrailGadget extends AbstractGadget<TrailContext> {

  private final TrailType type;

  public TrailGadget(TrailType type) {
    super(TrailManager.INSTANCE);
    this.type = type;
  }

  public Localizable getName() {
    return new UnlocalizedText(this.type.getName(), ChatColor.WHITE);
  }

  public void play(Location location, List<Player> viewers) {
    this.type.getEffect().display(0.3f, 0.3f, 0.3f, 2, 5, location, viewers);
  }

  @Override
  public ItemStack icon(Player player) {
    ItemStack stack = new ItemStack(Material.MELON_SEEDS);
    ItemMeta meta = stack.getItemMeta();

    meta.setDisplayName(getName().render(player).toLegacyText());

    stack.setItemMeta(meta);
    return stack;
  }

  @Override
  public JsonObject serialize() {
    JsonObject json = new JsonObject();
    json.addProperty("type", this.type.name());
    return json;
  }

  @Override
  public TrailContext defaultContext() {
    return new TrailContext(this, false);
  }

  @Override
  public TrailContext deserializeContext(JsonObject json) {
    boolean enabled = json.get("enabled").getAsBoolean();
    return new TrailContext(this, enabled);
  }

  @Override
  public boolean isAllowedInMatches() {
    return true;
  }
}
