package net.avicus.hook.gadgets.types.trail;

import com.google.common.collect.ArrayListMultimap;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.avicus.compendium.settings.PlayerSettings;
import net.avicus.compendium.settings.Setting;
import net.avicus.compendium.settings.types.SettingTypes;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.module.gadgets.GadgetManager;
import net.avicus.magma.module.gadgets.Gadgets;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TrailManager implements GadgetManager<TrailGadget, TrailContext> {

  public static final TrailManager INSTANCE = new TrailManager();
  private static final Setting<Boolean> SHOW_TRAILS_SETTING = new Setting<>(
      "show-trails",
      SettingTypes.BOOLEAN,
      true,
      Messages.SETTINGS_SHOW_TRAILS,
      Messages.SETTINGS_SHOW_TRAILS_SUMMARY
  );

  private final ArrayListMultimap<UUID, TrailContext> trails;
  private final TrailTask task;
  private final Gadgets gadgets;

  private TrailManager() {
    this.trails = ArrayListMultimap.create();
    this.task = new TrailTask(this);
    this.gadgets = getGadgets();
  }

  public void play(TrailContext trail, Player player, Location location) {
    List<Player> viewers = Bukkit.getOnlinePlayers()
        .stream()
        .filter(viewer -> canSeeTrails(viewer, player))
        .collect(Collectors.toList());

    if (viewers.isEmpty()) {
      return;
    }

    trail.getGadget().play(location, viewers);
  }

  public boolean canSeeTrails(Player viewer, Player target) {
    boolean visible = viewer.canSee(target);
    return visible && PlayerSettings.get(viewer, SHOW_TRAILS_SETTING);
  }

  public Set<UUID> usersWithTrails() {
    return this.trails.keySet();
  }

  public Optional<TrailContext> getTrail(UUID user) {
    for (TrailContext context : this.trails.get(user)) {
      if (context.isEnabled()) {
        return Optional.of(context);
      }
    }
    return Optional.empty();
  }

  @Override
  public String getType() {
    return "trail";
  }

  @Override
  public void init() {
    PlayerSettings.register(SHOW_TRAILS_SETTING);
    new TrailTask(this).start();
  }

  @Override
  public void onAsyncLoad(User user, TrailContext context) {
    this.trails.put(user.getUniqueId(), context);
  }

  @Override
  public void onAsyncUnload(User user, TrailContext context) {
    this.trails.remove(user.getUniqueId(), context);
  }

  @Override
  public void onUse(Player player, TrailContext context) {
    if (context.isEnabled()) {
      // Disable this ding
      context.setEnabled(false);
    } else {
      // Disable all enabled dings
      this.trails.get(player.getUniqueId())
          .stream()
          .filter(TrailContext::isEnabled)
          .forEach(gadget -> {
            gadget.setEnabled(false);
            gadgets.updateBackpackGadget(gadget);
          });

      // Enable this dings
      context.setEnabled(true);
    }

    gadgets.updateBackpackGadget(context);
  }

  @Override
  public TrailGadget deserializeGadget(JsonObject json) {
    TrailType type = TrailType.valueOf(json.get("type").getAsString().toUpperCase());
    return new TrailGadget(type);
  }
}
