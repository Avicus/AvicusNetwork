package net.avicus.atlas.module.groups.ffa;

import java.util.Collections;
import java.util.List;
import lombok.ToString;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.groups.GroupMember;
import net.avicus.atlas.util.color.TeamColor;
import org.bukkit.entity.Player;

@ToString
public class FFAMember extends GroupMember {

  private final TeamColor color;

  public FFAMember(String id, Group group, Player player, TeamColor color) {
    super(id, group, player);
    this.color = color;
  }

  @Override
  public TeamColor getTeamColor() {
    return this.color;
  }

  @Override
  public List<Player> getPlayers() {
    return Collections.singletonList(getPlayer());
  }
}
