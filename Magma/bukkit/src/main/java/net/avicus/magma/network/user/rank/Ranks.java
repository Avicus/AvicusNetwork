package net.avicus.magma.network.user.rank;

import com.google.common.collect.ArrayListMultimap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.avicus.magma.Magma;
import net.avicus.magma.database.model.impl.Rank;
import net.avicus.magma.database.model.impl.RankMember;
import net.avicus.magma.database.model.impl.User;
import org.bukkit.scheduler.BukkitRunnable;

public class Ranks {

  private static final Map<String, BukkitRank> permOnly = new HashMap<>();
  private static final Map<Integer, BukkitRank> cached = new HashMap<>();
  private static final ArrayListMultimap<Integer, BukkitRank> ranks = ArrayListMultimap.create();
  private static final ArrayListMultimap<Integer, BukkitRank> offlineRanks = ArrayListMultimap
      .create();

  public static void init() {
    new RankReloadTask().start();
    Magma.get().getServer().getPluginManager().registerEvents(new RankListener(), Magma.get());
  }

  public static Optional<BukkitRank> getCached(int rankId) {
    return Optional.ofNullable(cached.get(rankId));
  }

  public static Optional<BukkitRank> getPermOnly(String name) {
    return Optional.of(permOnly.get(name));
  }

  public static List<BukkitRank> get(User user) {
    return get(user, true);
  }

  public static List<BukkitRank> get(User user, boolean online) {
    List<BukkitRank> res = new ArrayList<>();
    if (online) {
      res = ranks.get(user.getId());
    } else {
      if (offlineRanks.containsKey(user)) {
        res = offlineRanks.get(user.getId());
      } else {
        List<RankMember> memberships = user.memberships(Magma.get().database());
        for (RankMember membership : memberships) {
          Optional<BukkitRank> rank = Ranks.getCached(membership.getRankId());
          if (rank.isPresent()) {
            res.add(rank.get());
          }
        }
        offlineRanks.putAll(user.getId(), res);

        // Clear cache every 10 mins.
        new BukkitRunnable() {
          @Override
          public void run() {
            offlineRanks.removeAll(user);
          }
        }.runTaskLater(Magma.get(), 20 * 60 * 10);
      }
    }
    res = new ArrayList<>(res);
    Collections.sort(res,
        (p1, p2) -> Integer.compare(p2.getRank().getPriority(), p1.getRank().getPriority()));
    return res;
  }

  public static Optional<BukkitRank> getHighest(User user) {
    return getHighest(user, true);
  }

  public static Optional<BukkitRank> getHighest(User user, boolean online) {
    if (!get(user, online).isEmpty()) {
      List<BukkitRank> ranks = get(user);
      Collections.reverse(ranks);
      BukkitRank res = null;
      for (BukkitRank rank : ranks) {
        res = rank;
        if (rank.getPermissions().contains("magma.rank.highestdisplay")) {
          break;
        }
      }
      return Optional.ofNullable(res);
    }

    return Optional.empty();
  }

  public static void register(Rank rank, BukkitRank bukkit) {
    cached.put(rank.getId(), bukkit);
  }

  public static void registerPermsOnly(Rank rank, BukkitRank bukkit) {
    permOnly.put(rank.getName().substring(1), bukkit);
  }

  public static void add(User user, BukkitRank bukkit) {
    ranks.put(user.getId(), bukkit);
    offlineRanks.remove(user.getId(), bukkit);
  }

  public static void clear(User user) {
    // Clear old cache, add updated to cache.
    offlineRanks.removeAll(user.getId());
    offlineRanks.putAll(user.getId(), ranks.get(user.getId()));

    ranks.removeAll(user.getId());
  }
}
