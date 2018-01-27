package net.avicus.hook.gadgets.types.arrowtrails;

import com.google.common.collect.ArrayListMultimap;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import net.avicus.compendium.settings.PlayerSettings;
import net.avicus.compendium.settings.Setting;
import net.avicus.compendium.settings.types.SettingTypes;
import net.avicus.hook.utils.Events;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.module.gadgets.GadgetManager;
import net.avicus.magma.module.gadgets.Gadgets;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ArrowTrailManager implements GadgetManager<ArrowTrailGadget, ArrowTrailContext> {

  public static final ArrowTrailManager INSTANCE = new ArrowTrailManager();
  private static final Setting<Boolean> SHOW_TRAILS_SETTING = new Setting<>(
      "show-arrow-trails",
      SettingTypes.BOOLEAN,
      true,
      Messages.SETTINGS_SHOW_ARROW_TRAILS,
      Messages.SETTINGS_SHOW_ARROW_TRAILS_SUMMARY
  );

  private final ArrayListMultimap<UUID, ArrowTrailContext> trails;
  private final ArrowTrailTask task;
  private final Gadgets gadgets;

  private ArrowTrailManager() {
    this.trails = ArrayListMultimap.create();
    this.task = new ArrowTrailTask(this);
    this.gadgets = getGadgets();
  }

  public void play(ArrowTrailType trail, Location location) {
    List<Player> viewers = Bukkit.getOnlinePlayers()
        .stream()
        .filter(this::canSeeTrails)
        .collect(Collectors.toList());

    if (viewers.isEmpty()) {
      return;
    }

    trail.play(location, viewers);
  }

  public boolean canSeeTrails(Player viewer) {
    return PlayerSettings.get(viewer, SHOW_TRAILS_SETTING);
  }

  public Optional<ArrowTrailContext> getTrail(UUID user) {
    for (ArrowTrailContext context : this.trails.get(user)) {
      if (context.isEnabled()) {
        return Optional.of(context);
      }
    }
    return Optional.empty();
  }

  @Override
  public String getType() {
    return "arrow-trail";
  }

  @Override
  public void init() {
    PlayerSettings.register(SHOW_TRAILS_SETTING);
    this.task.start();
    Events.register(this.task);
  }

  @Override
  public void onAsyncLoad(User user, ArrowTrailContext context) {
    this.trails.put(user.getUniqueId(), context);
  }

  @Override
  public void onAsyncUnload(User user, ArrowTrailContext context) {
    this.trails.remove(user.getUniqueId(), context);
  }

  @Override
  public void onUse(Player player, ArrowTrailContext context) {
    if (context.isEnabled()) {
      // Disable this arrow trail
      context.setEnabled(false);
    } else {
      // Disable all enabled arrow trails
      this.trails.get(player.getUniqueId())
          .stream()
          .filter(ArrowTrailContext::isEnabled)
          .forEach(gadget -> {
            gadget.setEnabled(false);
            gadgets.updateBackpackGadget(gadget);
          });

      // Enable this arrow trail
      context.setEnabled(true);
    }

    gadgets.updateBackpackGadget(context);
  }

  @Override
  public ArrowTrailGadget deserializeGadget(JsonObject json) {
    ArrowTrailType type = ArrowTrailType.valueOf(json.get("type").getAsString().toUpperCase());
    return new ArrowTrailGadget(type);
  }
}
