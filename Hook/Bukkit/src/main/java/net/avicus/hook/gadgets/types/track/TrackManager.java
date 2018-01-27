package net.avicus.hook.gadgets.types.track;

import com.google.gson.JsonObject;
import net.avicus.hook.utils.HookTask;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.module.gadgets.GadgetManager;
import net.avicus.magma.module.gadgets.Gadgets;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TrackManager implements GadgetManager<TrackGadget, TrackContext> {

  public static final TrackManager INSTANCE = new TrackManager();
  private final Gadgets gadgets;
  private long lastPlay;  // time when a track was last played

  private TrackManager() {
    this.gadgets = getGadgets();
  }

  @Override
  public String getType() {
    return "track";
  }

  @Override
  public void init() {

  }

  @Override
  public void onAsyncLoad(User user, TrackContext context) {

  }

  @Override
  public void onAsyncUnload(User user, TrackContext context) {

  }

  @Override
  public void onUse(Player player, TrackContext context) {
    if (System.currentTimeMillis() - this.lastPlay < 60 * 1000) {
      player.sendMessage(Messages.ERROR_TRACK_IN_PROGRESS.with(ChatColor.RED));
      return;
    }

    context.play(player);
    this.lastPlay = System.currentTimeMillis();

    if (!context.hasUsages()) {
      HookTask.of(() -> gadgets.deleteBackpackGadget(context)).nowAsync();
    } else {
      HookTask.of(() -> gadgets.updateBackpackGadget(context)).nowAsync();
    }
  }

  @Override
  public TrackGadget deserializeGadget(JsonObject json) {
    TrackType type = TrackType.valueOf(json.get("type").getAsString());
    int maxUsages = json.get("max-usages").getAsInt();
    return new TrackGadget(type, maxUsages);
  }
}
