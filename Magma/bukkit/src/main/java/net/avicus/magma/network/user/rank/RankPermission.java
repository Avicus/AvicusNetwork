package net.avicus.magma.network.user.rank;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.avicus.magma.Magma;
import org.bukkit.entity.Player;

@ToString
@Getter
@EqualsAndHashCode
public class RankPermission {

  private final boolean allow;
  private String node;

  public RankPermission(String node) {
    if (node.startsWith("-")) {
      this.node = node.substring(1);
      this.allow = false;
    } else {
      this.node = node;
      this.allow = true;
    }
  }

  public void attach(Player player) {
    player.addAttachment(Magma.get(), this.node, this.allow);
    player.recalculatePermissions();
  }
}
