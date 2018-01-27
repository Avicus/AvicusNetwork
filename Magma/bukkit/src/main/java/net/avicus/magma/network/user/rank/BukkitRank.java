package net.avicus.magma.network.user.rank;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.ToString;
import net.avicus.magma.Magma;
import net.avicus.magma.database.model.impl.Rank;
import net.avicus.magma.database.table.impl.RankTable;
import org.bukkit.entity.Player;

@ToString
public class BukkitRank {

  @Getter
  private final Rank rank;
  private final RankTable table;
  private final Rank defaultRank;
  private List<RankPermission> permissions;

  public BukkitRank(Rank rank) {
    this.rank = rank;
    this.permissions = null;

    this.table = Magma.get().database().getRanks();
    this.defaultRank = table.getOrCreate("@default");
  }

  public void init() {
    this.permissions = new ArrayList<>();

    List<String> perms = new ArrayList<>(this.table.getPermissions(this.rank));
    perms.addAll(this.table
        .getCategoryPermissions(this.rank, Magma.get().localServer().getServerCategoryId()));

    this.permissions.addAll(perms
        .stream()
        .map(RankPermission::new)
        .collect(Collectors.toList()));
  }

  public void attachPermissions(Player player) {
    for (RankPermission permission : this.permissions) {
      permission.attach(player);
    }
  }

  public List<String> getPermissions() {
    List<String> res = new ArrayList<>();
    for (RankPermission permission : this.permissions) {
      res.add((permission.isAllow() ? "" : "-") + permission.getNode());
    }
    return res;
  }
}
