package net.avicus.atlas.module.groups.ffa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.ToString;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.util.color.TeamColor;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

@ToString
public class FFATeam implements Group {

  private final int min;
  private final Map<UUID, FFAMember> members;
  private final Map<UUID, TeamColor> colors;
  private final boolean colorize;
  private final boolean friendlyFire;
  private LocalizedXmlString name;
  private int max;
  private int maxOverfill;
  private boolean observing;

  public FFATeam(LocalizedXmlString name, int min, int max, int maxOverfill, boolean colorize,
      boolean friendlyFire) {
    this.name = name;
    this.min = min;
    this.max = max;
    this.maxOverfill = maxOverfill;
    this.colorize = colorize;
    this.friendlyFire = friendlyFire;
    this.members = new HashMap<>();
    this.colors = new HashMap<>();
  }

  public void add(Player player) {
    TeamColor color = nextColor();

    String random = UUID.randomUUID().toString().substring(0, 8);
    this.members.put(player.getUniqueId(), new FFAMember(random, this, player, color));
    this.colors.put(player.getUniqueId(), color);
  }

  private TeamColor nextColor() {
    TeamColor color = TeamColor.GRAY;

    if (this.colorize) {
      // Map team color to number of players with that color
      Map<TeamColor, Integer> colorCounts = new HashMap<>();

      // Populate with 0
      for (TeamColor test : TeamColor.values()) {
        colorCounts.put(test, 0);
      }

      for (TeamColor test : this.colors.values()) {
        int current = colorCounts.get(test);
        colorCounts.put(test, current + 1);
      }

      // Creates mutable list of all team colors
      List<TeamColor> sorted = new ArrayList<>(Arrays.asList(TeamColor.values()));
      sorted.remove(TeamColor.AQUA);

      // Randomize (otherwise its the same order every match)
      Collections.shuffle(sorted);

      // Sort from lowest count to highest
      sorted.sort((o1, o2) -> {
        Integer count1 = colorCounts.getOrDefault(o1, 0);
        Integer count2 = colorCounts.getOrDefault(o2, 0);

        return count1.compareTo(count2);
      });

      // Get the lowest
      color = sorted.get(0);
    }

    return color;
  }

  public void remove(Player player) {
    this.members.remove(player.getUniqueId());
    this.colors.remove(player.getUniqueId());
  }

  @Override
  public Collection<FFAMember> getMembers() {
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
        .map(FFAMember::getPlayer)
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
  public int getMaxOverfill() {
    return this.maxOverfill;
  }

  @Override
  public String getId() {
    return "ffa";
  }

  @Override
  public Group getObject() {
    return this;
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
    return this.name;
  }

  @Override
  public TeamColor getTeamColor() {
    return TeamColor.GRAY;
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
  public boolean isObserving() {
    return this.observing;
  }

  @Override
  public void setObserving(boolean observing) {
    this.observing = observing;
  }

  @Override
  public boolean isFriendlyFireEnabled() {
    return this.friendlyFire;
  }

  @Override
  public boolean isFull(boolean withOverfill) {
    if (withOverfill) {
      return this.members.size() >= this.maxOverfill;
    }
    return this.members.size() >= this.max;
  }

  @Override
  public boolean equals(Object compare) {
    return compare instanceof FFATeam && ((FFATeam) compare).getId().equals(this.getId());
  }
}
