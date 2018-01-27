package net.avicus.hook.gadgets.types.morph;

import com.google.common.collect.ArrayListMultimap;
import com.google.gson.JsonObject;
import java.util.UUID;
import lombok.ToString;
import net.avicus.hook.utils.Events;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.module.gadgets.GadgetManager;
import net.avicus.magma.module.gadgets.Gadgets;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

@ToString
public class MorphManager implements GadgetManager<MorphGadget, MorphContext>, Listener {

  public static final MorphManager INSTANCE = new MorphManager();

  private final ArrayListMultimap<UUID, MorphContext> morphs;

  private final Gadgets gadgets;

  private MorphManager() {
    this.morphs = ArrayListMultimap.create();
    this.gadgets = getGadgets();
  }

  @Override
  public void init() {
    Events.register(this);
  }

  @Override
  public String getType() {
    return "morph";
  }

  @Override
  public void onAsyncLoad(User user, MorphContext context) {
    this.morphs.put(user.getUniqueId(), context);
  }

  @Override
  public void onAsyncUnload(User user, MorphContext context) {
    context.setEnabled(false);
    gadgets.updateBackpackGadget(context);
    this.morphs.remove(user.getUniqueId(), context);
  }

  @Override
  public void onUse(Player player, MorphContext context) {
    if (context.isEnabled()) {
      // Disable this morph
      context.setEnabled(false);
      context.unDisguise(player);

      player.sendMessage(
          Messages.UI_MORPH_OFF.with(ChatColor.RED, context.getGadget().getEntity().prettyName()));
      player.playSound(player.getLocation(), Sound.EXPLODE, 1.0F, 1.0F);
    } else {
      // Disable all enabled morphs
      this.morphs.get(player.getUniqueId())
          .stream()
          .filter(MorphContext::isEnabled)
          .forEach(morph -> {
            morph.setEnabled(false);
            morph.unDisguise(player);
            gadgets.updateBackpackGadget(morph);
          });

      // Enable this morph
      context.setEnabled(true);
      context.disguise(player);

      player.sendMessage(Messages.UI_MORPH_ACTIVE
          .with(ChatColor.GREEN, context.getGadget().getEntity().prettyName()));
      player.playSound(player.getLocation(), Sound.ZOMBIE_REMEDY, 1.0F, 1.0F);
    }

    gadgets.updateBackpackGadget(context);
  }

  @Override
  public MorphGadget deserializeGadget(JsonObject json) {
    MorphEntity entity = MorphEntity.valueOf(json.get("entity").getAsString().toUpperCase());
    return new MorphGadget(entity);
  }
}
