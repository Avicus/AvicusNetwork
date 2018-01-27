package net.avicus.hook.gadgets.types.sound;

import com.google.gson.JsonObject;
import java.util.Locale;
import lombok.Getter;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.sound.SoundLocation;
import net.avicus.compendium.sound.SoundType;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.module.gadgets.AbstractGadget;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SoundGadget extends AbstractGadget<SoundContext> {

  @Getter
  private final SoundType type;
  @Getter
  private final SoundLocation location;

  public SoundGadget(SoundLocation location, SoundType type) {
    super(SoundManager.INSTANCE);
    this.type = type;
    this.location = location;
  }

  public void play(Player player) {
    this.type.play(player, 1.0F);
  }

  @Override
  public Localizable getName() {
    return Messages.UI_SOUND
        .with(ChatColor.WHITE, this.location.prettyName(), this.type.prettyName());
  }

  @Override
  public ItemStack icon(Locale locale) {
    ItemStack stack = new ItemStack(Material.NOTE_BLOCK);
    ItemMeta meta = stack.getItemMeta();

    meta.setDisplayName(getName().translate(locale).toLegacyText());

    stack.setItemMeta(meta);
    return stack;
  }

  @Override
  public JsonObject serialize() {
    JsonObject json = new JsonObject();
    json.addProperty("location", this.location.name());
    json.addProperty("type", this.type.name());
    return json;
  }

  @Override
  public SoundContext defaultContext() {
    return new SoundContext(this, false);
  }

  @Override
  public SoundContext deserializeContext(JsonObject json) {
    boolean enabled = json.get("enabled").getAsBoolean();
    return new SoundContext(this, enabled);
  }

  @Override
  public boolean isAllowedInMatches() {
    return true;
  }
}
