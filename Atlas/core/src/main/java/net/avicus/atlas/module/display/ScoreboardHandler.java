package net.avicus.atlas.module.display;

import com.google.common.collect.ArrayListMultimap;
import java.util.List;
import net.avicus.atlas.event.competitor.PlayerChangeCompetitorEvent;
import net.avicus.atlas.event.group.PlayerChangedGroupEvent;
import net.avicus.atlas.event.match.MatchOpenEvent;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.groups.Spectators;
import net.avicus.atlas.util.VersionUtil;
import net.avicus.atlas.util.color.TeamColor;
import net.avicus.compendium.snap.SnapClass;
import net.avicus.compendium.snap.SnapMethod;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardHandler implements Listener {

  private final Match match;
  private Spectators spectators;
  private ArrayListMultimap<Scoreboard, Competitor> competitors;

  public ScoreboardHandler(Match match) {
    this.match = match;
  }

  @EventHandler
  public void onMatchOpen(MatchOpenEvent event) {
    this.spectators = event.getMatch().getRequiredModule(GroupsModule.class).getSpectators();
    this.competitors = ArrayListMultimap.create();
    Bukkit.getOnlinePlayers()
        .forEach(this::resetScoreboardTeams);
    Bukkit.getOnlinePlayers().forEach(this::addSpectatorPlayer);
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    resetScoreboardTeams(event.getPlayer());
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerChangeCompetitor(PlayerChangeCompetitorEvent event) {
    if (event.getCompetitorFrom().isPresent()) {
      Competitor competitor = event.getCompetitorFrom().get();
      removeCompetitorPlayer(competitor, event.getPlayer());
    }
    if (event.getCompetitorTo().isPresent()) {
      registerScoreboardCompetitor(event.getCompetitorTo().get());
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerChangeGroup(PlayerChangedGroupEvent event) {
    if (event.getGroupFrom().isPresent() && event.getGroupFrom().get().equals(this.spectators)) {
      removeSpectatorPlayer(event.getPlayer());
    }

    // Only handle spectators, competitor changes are handled in PlayerChangeCompetitorEvent
    if (event.getGroup().equals(this.spectators)) {
      addSpectatorPlayer(event.getPlayer());
    }
  }

  private void removeCompetitorPlayer(Competitor competitor, Player player) {
    for (Player target : Bukkit.getOnlinePlayers()) {
      removeCompetitorPlayer(target.getScoreboard(), competitor, player);
    }
  }

  private void removeCompetitorPlayer(Scoreboard scoreboard, Competitor competitor, Player player) {
    org.bukkit.scoreboard.Team team = scoreboard.getTeam(competitor.getId());
    team.removeEntry(player.getName());
  }

  private void removeSpectatorPlayer(Player player) {
    for (Player target : Bukkit.getOnlinePlayers()) {
      removeSpectatorPlayer(target.getScoreboard(), player);
    }
  }

  private void removeSpectatorPlayer(Scoreboard scoreboard, Player player) {
    org.bukkit.scoreboard.Team team = scoreboard.getTeam(this.spectators.getId());
    team.removeEntry(player.getName());
  }

  private void addSpectatorPlayer(Player player) {
    for (Player target : Bukkit.getOnlinePlayers()) {
      addSpectatorPlayer(target.getScoreboard(), player);
    }
  }

  private void addSpectatorPlayer(Scoreboard scoreboard, Player player) {
    org.bukkit.scoreboard.Team team = scoreboard.getTeam(this.spectators.getId());
    team.addEntry(player.getName());
  }

  private void registerCompetitors(Scoreboard scoreboard) {
    GroupsModule module = this.match.getRequiredModule(GroupsModule.class);
    List<Competitor> competitors = this.competitors.get(scoreboard);

    for (Competitor competitor : module.getCompetitors()) {
      if (competitors.contains(competitor)) {
        continue;
      }

      registerScoreboardCompetitor(scoreboard, competitor);
      competitors.add(competitor);
    }
  }

  public void resetScoreboardTeams(Player player) {
    Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    player.setScoreboard(scoreboard);

    registerScoreboardTeam(scoreboard, this.spectators.getId(), this.spectators.getTeamColor(),
        this.spectators.getPlayers(), false);
    registerCompetitors(scoreboard);
  }

  private void registerScoreboardCompetitor(Competitor competitor) {
    for (Player player : Bukkit.getOnlinePlayers()) {
      registerScoreboardCompetitor(player.getScoreboard(), competitor);
    }
  }

  private void registerScoreboardCompetitor(Scoreboard scoreboard, Competitor competitor) {
    registerScoreboardTeam(scoreboard, competitor.getId(), competitor.getTeamColor(),
        competitor.getPlayers(), competitor.getGroup().isFriendlyFireEnabled());
  }

  private void registerScoreboardTeam(Scoreboard scoreboard, String id, TeamColor color,
      List<Player> members, boolean friendlyFire) {
    // Unregister any existing team, create new one.
    org.bukkit.scoreboard.Team bukkit = scoreboard.getTeam(id);
    if (bukkit != null) {
      bukkit.unregister();
    }
    bukkit = scoreboard.registerNewTeam(id);

    bukkit.setPrefix(color.getChatColor().toString());

    for (Player member : members) {
      bukkit.addEntry(member.getName());
    }

    bukkit.setAllowFriendlyFire(friendlyFire);
    bukkit.setCanSeeFriendlyInvisibles(true);

    // 1.8-1.9 Support
    if (VersionUtil.isCombatUpdate()) {
      SnapClass option = new SnapClass("org.bukkit.scoreboard.Team$Option");
      SnapClass optionStatus = new SnapClass("org.bukkit.scoreboard.Team$OptionStatus");

      SnapMethod snapOption = new SnapClass("org.bukkit.scoreboard.Team")
          .getMethod("setOption", option.getClazz(), optionStatus.getClazz());

      Object collisionRule = option.getMethod("valueOf", String.class).getStatic("COLLISION_RULE");
      Object forOwnTeam = optionStatus.getMethod("valueOf", String.class).getStatic("FOR_OWN_TEAM");

      snapOption.get(bukkit, collisionRule, forOwnTeam);

      // 1.9 Version
      // bukkit.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.ALWAYS);
      // bukkit.setOption(Option.COLLISION_RULE, OptionStatus.FOR_OWN_TEAM);
    }
  }
}
