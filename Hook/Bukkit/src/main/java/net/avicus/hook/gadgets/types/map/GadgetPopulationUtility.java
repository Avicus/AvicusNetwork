package net.avicus.hook.gadgets.types.map;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.GameType;
import net.avicus.atlas.map.AtlasMap;
import net.avicus.atlas.map.rotation.Rotation;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchBuildException;
import net.avicus.atlas.module.ModuleBuildException;
import net.avicus.atlas.module.states.StatesModule;
import net.avicus.atlas.module.vote.VoteModule;
import net.avicus.compendium.WeightedRandomizer;
import net.avicus.hook.gadgets.types.map.startvote.StartVoteGadget;
import net.avicus.magma.Magma;
import net.avicus.magma.game.MinecraftMap;
import net.avicus.magma.module.gadgets.Gadgets;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GadgetPopulationUtility {

  private static final Random RANDOM = new Random();

  public static void populate() {
    Magma.get().getMm().get(Gadgets.class).getManager("atlas").ifPresent(m -> {
      AtlasGadgetManager manager = (AtlasGadgetManager) m;
      manager.addMaps(possibleMaps());

      StartVoteGadget.setOnUse((p) -> {
        Player player = p.getLeft();
        Rotation rotation = Atlas.get().getMatchManager().getRotation();
        Match match = p.getRight();
        @Nullable final VoteModule module = match.getModule(VoteModule.class).orElse(null);
        if (module == null || rotation.isVoteQueued()) {
          player.sendMessage(net.avicus.atlas.util.Messages.VOTE_DISABLED.with(ChatColor.RED));
          return false;
        }

        Map<Integer, Match> toVote = Maps.newHashMap();
        WeightedRandomizer.Builder builder = WeightedRandomizer.<MinecraftMap>builder();
        manager.getPossibleMaps().forEach(map -> builder.item(map, 1));

        WeightedRandomizer<MinecraftMap> randomizer = builder.build();

        int count = RANDOM.nextInt(8);

        for (int i = 0; i < count; i++) {
          AtlasMap map = (AtlasMap) randomizer.next();
          Match created = null;
          try {
            created = Atlas.get().getMatchManager().getFactory().create(map);
          } catch (MatchBuildException | ModuleBuildException e) {
            // Can't parse this, skip!
            continue;
          }
          toVote.put(i, created);
        }

        if (toVote.isEmpty()) {
          return false;
        }

        boolean onCycle =
            rotation.isStarting() || match.getRequiredModule(StatesModule.class).isPlaying();

        module.start(toVote, null, onCycle);
        return true;
      });
    });
  }

  private static List<AtlasMap> possibleMaps() {
    Set<GameType> serverTypes = Sets.newHashSet();
    List<AtlasMap> maps = new ArrayList<>();
    Atlas.get().getMatchManager().getRotation().getMatches()
        .forEach(m -> serverTypes.addAll(m.getMap().getGameTypes()));
    Atlas.get().getMapManager().getLibraries().forEach(l -> {
      l.getMaps().forEach(m -> {
        if (!m.getGameTypes().isEmpty() && serverTypes.containsAll(m.getGameTypes())) {
          maps.add(m);
        }
      });
    });
    return maps;
  }


}
