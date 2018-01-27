package net.avicus.hook.gadgets.types.map;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import lombok.Getter;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.match.Match;
import net.avicus.hook.gadgets.types.map.setnext.SetNextGadget;
import net.avicus.hook.gadgets.types.map.startvote.StartVoteGadget;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.game.MinecraftMap;
import net.avicus.magma.module.gadgets.EmptyGadgetContext;
import net.avicus.magma.module.gadgets.GadgetManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AtlasGadgetManager implements
    GadgetManager<AtlasGadget, EmptyGadgetContext<AtlasGadget>> {

  public static final AtlasGadgetManager INSTANCE = new AtlasGadgetManager();

  @Getter
  private final List<MinecraftMap> possibleMaps;

  private AtlasGadgetManager() {
    this.possibleMaps = new ArrayList<>();
  }

  public void addMap(MinecraftMap map) {
    this.possibleMaps.add(map);
  }

  public void addMaps(List<? extends MinecraftMap> map) {
    this.possibleMaps.addAll(map);
  }

  public void removeMap(MinecraftMap map) {
    this.possibleMaps.remove(map);
  }

  public void removeMaps(List<? extends MinecraftMap> map) {
    this.possibleMaps.removeAll(map);
  }

  @Override
  public String getType() {
    return "atlas";
  }

  @Override
  public void onUse(Player player, EmptyGadgetContext<AtlasGadget> context) {
    if (this.possibleMaps.isEmpty()) {
      player.sendMessage(Messages.ERROR_NO_ATLAS.with(ChatColor.RED));
      return;
    }
    @Nullable final Match match = Atlas.getMatch();
    if (match == null) {
      player.sendMessage(net.avicus.atlas.util.Messages.ERROR_MATCH_MISSING.with(ChatColor.RED));
      return;
    }
    if ((context.getGadget()).onUse(player, match, context)) {
      getGadgets().deleteBackpackGadget(context);
    }
  }

  @Override
  public void init() {
    // Nothing fancy to do
  }

  @Override
  public void onAsyncLoad(User user, EmptyGadgetContext context) {
    // Nothing fancy to do
  }

  @Override
  public void onAsyncUnload(User user, EmptyGadgetContext context) {
    // Nothing fancy to do
  }

  @Override
  public AtlasGadget deserializeGadget(JsonObject json) {
    String type = json.get("type").getAsString();
    switch (type) {
      case "set-next":
        return new SetNextGadget();
      case "start-vote":
        return new StartVoteGadget();
      default:
        throw new RuntimeException("Unknown atlas gadget type " + type);
    }
  }
}
