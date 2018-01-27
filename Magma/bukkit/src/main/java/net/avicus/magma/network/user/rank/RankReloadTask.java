package net.avicus.magma.network.user.rank;

import net.avicus.magma.Magma;
import net.avicus.magma.database.model.impl.Rank;
import org.bukkit.scheduler.BukkitRunnable;

public class RankReloadTask extends BukkitRunnable {

  public void start() {
    this.runTaskTimerAsynchronously(Magma.get(), 0, 20 * 5);
  }

  @Override
  public void run() {
    for (Rank rank : Magma.get().database().getRanks().all()) {
      BukkitRank bukkit = new BukkitRank(rank);
      bukkit.init();
      if (rank.getName().startsWith("@")) {
        Ranks.registerPermsOnly(rank, bukkit);
      } else {
        Ranks.register(rank, bukkit);
      }
    }
  }
}
