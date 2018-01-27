package net.avicus.atlas.module.groups;

import java.util.Collection;
import net.avicus.atlas.command.JoinCommands;
import net.avicus.atlas.match.registry.RegisterableObject;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.util.color.TeamColor;
import net.avicus.magma.util.distance.PlayerStore;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

public interface Group extends RegisterableObject<Group>, PlayerStore {

  String getId();

  LocalizedXmlString getName();

  void setName(LocalizedXmlString name);

  LocalizedXmlString getOriginalName();

  TeamColor getTeamColor();

  ChatColor getChatColor();

  DyeColor getDyeColor();

  Color getFireworkColor();

  void add(Player player);

  void remove(Player player);

  boolean isObserving();

  void setObserving(boolean observing);

  boolean isFriendlyFireEnabled();

  boolean isFull(boolean withOverfill);

  default boolean isFull(Player player) {
    return isFull(player.hasPermission(JoinCommands.FULL_PERMISSION));
  }

  Collection<? extends GroupMember> getMembers();

  boolean isMember(Player player);

  default boolean isSpectator() {
    return this instanceof Spectators;
  }

  int size();

  int getMinPlayers();

  int getMaxPlayers();

  void setMaxPlayers(int max, int overfill);

  int getMaxOverfill();

  default double filledPortion() {
    return (double) size() / (double) getMaxPlayers();
  }
}
