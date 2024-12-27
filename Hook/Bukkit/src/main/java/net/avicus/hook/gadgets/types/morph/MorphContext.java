package net.avicus.hook.gadgets.types.morph;

import com.google.gson.JsonObject;
import java.util.Arrays;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.LivingWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.RabbitWatcher;
import net.avicus.hook.gadgets.types.morph.MorphEntity.PlayerMorphData;
import net.avicus.hook.gadgets.types.morph.MorphEntity.RabbitMorphData;
import net.avicus.hook.utils.HookTask;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.module.gadgets.AbstractGadgetContext;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@ToString
public class MorphContext extends AbstractGadgetContext<MorphGadget> {

  @Getter
  @Setter
  private boolean enabled;

  public MorphContext(MorphGadget gadget, boolean enabled) {
    super(gadget);
    this.enabled = enabled;
  }

  public void disguise(Player player) {
    // Gotta do these things sync.
    EntityType entity = getGadget().getEntity().getEntity();
    HookTask.of(() -> {
      if (entity.equals(EntityType.PLAYER)) {
        PlayerMorphData data = (PlayerMorphData) getGadget().getEntity().getData();
        PlayerDisguise pd = new PlayerDisguise(ChatColor.BLUE + player.getName(),
            data.getPlayerName());
        DisguiseAPI.disguiseToAll(player, pd);
      } else {
        MobDisguise disguise = new MobDisguise(
            DisguiseType.getType(getGadget().getEntity().getEntity()));
        DisguiseAPI.disguiseToAll(player, disguise);
        ((LivingWatcher) disguise.getWatcher()).setCustomName(ChatColor.BLUE + player.getName());
        ((LivingWatcher) disguise.getWatcher()).setCustomNameVisible(true);
        if (entity.equals(EntityType.RABBIT)) {
          RabbitMorphData data = (RabbitMorphData) getGadget().getEntity().getData();
          ((RabbitWatcher) disguise.getWatcher()).setType(data.getType());
        }
      }
    }).now();
  }

  public void unDisguise(Player player) {
    // Gotta do these things sync.
    HookTask.of(() -> {
      DisguiseAPI.undisguiseToAll(player);
    }).now();
  }

  @Override
  public ItemStack icon(Player player) {
    ItemStack stack = super.icon(player);
    ItemMeta meta = stack.getItemMeta();

    meta.setLore(Arrays.asList(
        Messages.enabledOrDisabled(this.enabled).render(player).toLegacyText()
    ));

    stack.setItemMeta(meta);
    return stack;
  }

  @Override
  public JsonObject serialize() {
    JsonObject json = new JsonObject();
    json.addProperty("enabled", this.enabled);
    return json;
  }
}
