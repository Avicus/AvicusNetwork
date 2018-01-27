package net.avicus.atlas.module.groups;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.util.Messages;
import net.avicus.atlas.util.color.TeamColor;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

public class Spectators implements Group {

  private final Map<UUID, GroupMember> members;
  private LocalizedXmlString name;

  public Spectators() {
    this.name = new LocalizedXmlString(Messages.UI_SPECTATORS);
    this.members = new HashMap<>();
  }

  @Override
  public String getId() {
    return "spectators";
  }

  @Override
  public Spectators getObject() {
    return this;
  }

  @Override
  public LocalizedXmlString getName() {
    // Todo!
    return this.name;
  }

  @Override
  public void setName(LocalizedXmlString name) {
    this.name = name;
  }

  @Override
  public LocalizedXmlString getOriginalName() {
    return this.name;
  }

  @Override
  public TeamColor getTeamColor() {
    return TeamColor.AQUA;
  }

  @Override
  public ChatColor getChatColor() {
    return getTeamColor().getChatColor();
  }

  @Override
  public DyeColor getDyeColor() {
    return getTeamColor().getDyeColor();
  }

  @Override
  public Color getFireworkColor() {
    return getTeamColor().getFireworkColor();
  }

  @Override
  public void add(Player player) {
    String random = UUID.randomUUID().toString().substring(0, 8);
    this.members.put(player.getUniqueId(), new GroupMember(random, this, player));
  }

  @Override
  public void remove(Player player) {
    this.members.remove(player.getUniqueId());
  }

  @Override
  public boolean isObserving() {
    return true;
  }

  @Override
  public void setObserving(boolean observing) {
    // Don't do anything.
  }

  @Override
  public boolean isFriendlyFireEnabled() {
    return false;
  }

  @Override
  public boolean isFull(boolean withOverfill) {
    return false;
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
    return this.members.values()
        .stream()
        .map(GroupMember::getPlayer)
        .collect(Collectors.toList());
  }

  @Override
  public int size() {
    return this.members.size();
  }

  @Override
  public int getMinPlayers() {
    return -1;
  }

  @Override
  public int getMaxPlayers() {
    return Integer.MAX_VALUE;
  }

  @Override
  public int getMaxOverfill() {
    return Integer.MAX_VALUE;
  }

  @Override
  public void setMaxPlayers(int max, int overfill) {
  }
}
