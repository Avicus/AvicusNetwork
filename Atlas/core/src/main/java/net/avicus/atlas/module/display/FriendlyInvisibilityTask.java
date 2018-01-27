package net.avicus.atlas.module.display;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.avicus.atlas.event.group.PlayerChangedGroupEvent;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.util.AtlasTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class FriendlyInvisibilityTask extends AtlasTask implements Listener {

  public final DisplayModule display;
  private final GroupsModule groups;
  // Mapping of player with random integer for team identification.
  private HashMap<Player, String> potionInvisibles = new HashMap<>();
  private HashMap<Player, Set<Player>> playersCantSee = new HashMap<>();

  public FriendlyInvisibilityTask(Match match, DisplayModule display) {
    super();
    this.groups = match.getRequiredModule(GroupsModule.class);
    this.display = display;
  }

  @Override
  public void run() {
    execute();
  }

  public FriendlyInvisibilityTask start() {
    this.repeat(0, 20);
    return this;
  }

  public void execute() {
    for (Player viewer : Bukkit.getOnlinePlayers()) {
      if (groups.getGroup(viewer).isObserving()) {
        continue;
      }

      if (!this.playersCantSee.containsKey(viewer)) {
        this.playersCantSee.put(viewer, new HashSet<>());
      }

      Set<Player> shouldHide = this.playersCantSee.get(viewer);
      for (Player player : Bukkit.getOnlinePlayers()) {
        if (groups.getGroup(player).isObserving()) {
          continue;
        }

        if (shouldSee(viewer, player)) {
          shouldHide.remove(player);
        } else {
          shouldHide.add(player);
        }
      }
    }

    for (Map.Entry<Player, Set<Player>> entry : this.playersCantSee.entrySet()) {
      Player viewer = entry.getKey();

      Set<Player> hidden = entry.getValue();

      Iterator<Player> iterator = hidden.iterator();

      while (iterator.hasNext()) {
        Player hide = iterator.next();

        if (!hide.isOnline() || groups.getGroup(hide).isObserving()) {
          // This means a player has since become an observer/left.
          iterator.remove();
          continue;
        }

        constructTeam(viewer.getScoreboard(), hide);
      }
    }

    Set<Player> allHidden = getAllHidden();

    // Reset all players who have since become visible.
    for (Player viewer : Bukkit.getOnlinePlayers()) {
      for (Player target : Bukkit.getOnlinePlayers()) {
        if (viewer.equals(target)) {
          continue;
        }

        resetIfHadInvis(viewer, target, allHidden);
      }
    }
  }

  private Set<Player> getAllHidden() {
    Set<Player> allHidden = new HashSet<>();

    for (Player player : Bukkit.getOnlinePlayers()) {
      if (groups.getGroup(player).isObserving()) {
        continue;
      }

      if (!player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
        continue;
      }

      allHidden.add(player);
    }
    return allHidden;
  }

  private Team constructTeam(Scoreboard board, Player toAdd) {
    if (!this.potionInvisibles.containsKey(toAdd)) {
      this.potionInvisibles.put(toAdd, UUID.randomUUID().toString().substring(0, 8));
    }

    String teamName = "inv-" + this.potionInvisibles.get(toAdd);

    Team team = board.getTeam(teamName);

    if (team == null) {
      team = board.registerNewTeam(teamName);
      team.setPrefix(this.groups.getGroup(toAdd).getChatColor() + "");
      team.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
      team.setCanSeeFriendlyInvisibles(true);
      team.addEntry(toAdd.getName());
    }
    return team;
  }

  private void resetIfHadInvis(Player player, Player target, Set<Player> hidden) {
    if (hidden.contains(target)) {
      return;
    }

    Team team = player.getScoreboard().getTeam("inv-" + this.potionInvisibles.get(target));
    if (team != null) {
      for (String entry : team.getEntries()) {
        Player match = Bukkit.getPlayer(entry);

        if (entry == null) {
          continue;
        }

        Optional<Competitor> competitor = groups.getCompetitorOf(match);

        if (!competitor.isPresent()) {
          continue;
        }

        Team found = player.getScoreboard().getTeam(competitor.get().getId());

        if (found == null) {
          continue;
        }

        found.addEntry(entry);
      }

      team.unregister();
    }
  }

  private boolean shouldSee(Player player, Player target) {
    Group targetGroup = this.groups.getGroup(target);

    return targetGroup.isMember(player) || !target.hasPotionEffect(PotionEffectType.INVISIBILITY);
  }

  @EventHandler
  public void onChangeGroup(PlayerChangedGroupEvent event) {
    this.playersCantSee.remove(event.getPlayer());
  }

  @EventHandler
  public void onLeave(PlayerQuitEvent event) {
    this.playersCantSee.remove(event.getPlayer());
  }
}
