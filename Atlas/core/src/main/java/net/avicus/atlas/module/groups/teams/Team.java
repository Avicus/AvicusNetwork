package net.avicus.atlas.module.groups.teams;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.ToString;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.groups.GroupMember;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.util.color.TeamColor;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

@ToString
public class Team implements Group, Competitor {

  private final String id;
  private final LocalizedXmlString originalName;
  private final TeamColor color;
  private final int min;
  private final Map<UUID, GroupMember> members;
  private LocalizedXmlString name;
  private int max;
  private int maxOverfill;
  private boolean observing;

  public Team(String id, LocalizedXmlString name, TeamColor color, int min, int max,
      int maxOverfill) {
    this.id = id;
    this.name = name;
    this.originalName = name;
    this.color = color;
    this.min = min;
    this.max = max;
    this.maxOverfill = maxOverfill;
    this.members = new HashMap<>();
  }

  public void add(Player player) {
    String random = UUID.randomUUID().toString().substring(0, 8);
    this.members.put(player.getUniqueId(), new GroupMember(random, this, player));
  }

  public void remove(Player player) {
    this.members.remove(player.getUniqueId());
  }

  @Override
  public LocalizedXmlString getName() {
    return this.name;
  }

  @Override
  public void setName(LocalizedXmlString name) {
    this.name = name;
  }

  @Override
  public LocalizedXmlString getOriginalName() {
    return this.originalName;
  }

  @Override
  public Group getGroup() {
    return this;
  }

  @Override
  public TeamColor getTeamColor() {
    return this.color;
  }

  @Override
  public ChatColor getChatColor() {
    return this.color.getChatColor();
  }

  @Override
  public DyeColor getDyeColor() {
    return this.color.getDyeColor();
  }

  @Override
  public Color getFireworkColor() {
    return this.color.getFireworkColor();
  }

  @Override
  public boolean hasPlayer(Player player) {
    return this.members.containsKey(player.getUniqueId());
  }

  @Override
  public boolean isObserving() {
    return this.observing;
  }

  @Override
  public void setObserving(boolean observing) {
    this.observing = observing;
  }

  @Override
  public boolean isFriendlyFireEnabled() {
    return false;
  }

  @Override
  public boolean isFull(boolean withOverfill) {
    if (withOverfill) {
      return this.members.size() >= this.maxOverfill;
    }
    return this.members.size() >= this.max;
  }

  @Override
  public Collection<GroupMember> getMembers() {
    return this.members.values();
  }

  @Override
  public boolean isMember(Player player) {
    return this.members.containsKey(player.getUniqueId());
  }

  @Override
  public List<Player> getPlayers() {
    List<Player> list = this.members.values()
        .stream()
        .map(GroupMember::getPlayer)
        .collect(Collectors.toList());
    return list;
  }

  @Override
  public int size() {
    return this.members.size();
  }

  @Override
  public int getMinPlayers() {
    return this.min;
  }

  @Override
  public int getMaxPlayers() {
    return this.max;
  }

  @Override
  public void setMaxPlayers(int max, int overfill) {
    this.max = max;
    this.maxOverfill = overfill == -1 ? (max + (max / 2)) : overfill;
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public Team getObject() {
    return this;
  }

  @Override
  public boolean equals(Object compare) {
    return compare instanceof Team && ((Team) compare).getId().equals(this.getId());
  }

  @Override
  public int getMaxOverfill() {
    return this.maxOverfill;
  }
}
