package net.avicus.hook.gadgets.types.sound;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.JsonObject;
import java.util.Optional;
import java.util.UUID;
import net.avicus.compendium.sound.SoundEvent;
import net.avicus.compendium.sound.SoundLocation;
import net.avicus.compendium.sound.SoundType;
import net.avicus.grave.event.PlayerDeathByPlayerEvent;
import net.avicus.hook.credits.PlayerModifyCreditEvent;
import net.avicus.hook.utils.Events;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.module.gadgets.GadgetManager;
import net.avicus.magma.module.gadgets.Gadgets;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SoundManager implements GadgetManager<SoundGadget, SoundContext>, Listener {

  public static final SoundManager INSTANCE = new SoundManager();

  private final Table<UUID, SoundLocation, SoundContext> sounds;

  private final Gadgets gadgets;

  private SoundManager() {
    this.sounds = HashBasedTable.create();
    this.gadgets = getGadgets();
  }

  @Override
  public String getType() {
    return "sound";
  }

  @Override
  public void init() {
    Events.register(this);
  }

  public Optional<SoundType> getSound(UUID player, SoundLocation location) {
    SoundContext context = this.sounds.get(player, location);
    if (context == null) {
      return Optional.empty();
    }
    return Optional.of(context.getGadget().getType());
  }

  @EventHandler
  public void onPlayerCreditModify(PlayerModifyCreditEvent event) {
    if (event.getAmount() > 0) {
      SoundType ding = getSound(event.getPlayer().getUniqueId(), SoundLocation.CREDIT_GAIN)
          .orElse(SoundType.LEVEL_UP);
      ding.play(event.getPlayer(), 1.0F);
    }
  }

  @EventHandler
  public void killSound(PlayerDeathByPlayerEvent event) {
    SoundEvent call = Events
        .call(new SoundEvent(event.getCause(), SoundType.NONE, SoundLocation.KILL));
    call.getSound().play(event.getCause(), 1F);
  }

  @EventHandler
  public void onSound(SoundEvent event) {
    SoundType type = getSound(event.getPlayer().getUniqueId(), event.getLocation())
        .orElse(event.getSound());
    event.setSound(type);
  }

  @Override
  public void onAsyncLoad(User user, SoundContext context) {
    if (context.isEnabled()) {
      this.sounds.put(user.getUniqueId(), context.getGadget().getLocation(), context);
    }
  }

  @Override
  public void onAsyncUnload(User user, SoundContext context) {
    this.sounds.remove(user.getUniqueId(), context);
  }

  @Override
  public void onUse(Player player, SoundContext context) {
    if (context.isEnabled()) {
      // Disable this sound
      context.setEnabled(false);
      this.sounds.remove(player.getUniqueId(), context.getGadget().getLocation());
    } else {
      // Disable all enabled sounds of same location
      SoundContext found = this.sounds
          .remove(player.getUniqueId(), context.getGadget().getLocation());
      if (found != null) {
        found.setEnabled(false);
      }

      // Enable this sound
      context.setEnabled(true);
      this.sounds.put(player.getUniqueId(), context.getGadget().getLocation(), context);

      // Play it for them
      context.play(player);
    }

    gadgets.updateBackpackGadget(context);
  }

  @Override
  public SoundGadget deserializeGadget(JsonObject json) {
    SoundType type = SoundType.valueOf(json.get("type").getAsString().toUpperCase());
    // Legacy
    SoundLocation location = SoundLocation.CREDIT_GAIN;
    if (json.has("location")) {
      location = SoundLocation.valueOf(json.get("location").getAsString().toUpperCase());
    }
    return new SoundGadget(location, type);
  }
}
