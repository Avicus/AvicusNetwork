package net.avicus.atlas.module.groups;

import java.util.List;
import java.util.UUID;
import lombok.ToString;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.util.color.TeamColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@ToString(exclude = "group")
public class GroupMember implements Competitor {

  private final String id;
  private final Group group;
  private final UUID uuid;
  private final LocalizedXmlString name;

  public GroupMember(String id, Group group, Player player) {
    this.id = id;
    this.group = group;
    this.uuid = player.getUniqueId();
    this.name = new LocalizedXmlString(player.getName());
  }

  public Player getPlayer() {
    return Bukkit.getPlayer(this.uuid);
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public LocalizedXmlString getName() {
    return this.name;
  }

  @Override
  public Group getGroup() {
    return this.group;
  }

  @Override
  public boolean hasPlayer(Player player) {
    return getPlayers().contains(player);
  }

  @Override
  public List<Player> getPlayers() {
    return this.group.getPlayers();
  }

  @Override
  public TeamColor getTeamColor() {
    return this.group.getTeamColor();
  }

  public boolean isPlayer(Player player) {
    return hasPlayer(player);
  }
}
