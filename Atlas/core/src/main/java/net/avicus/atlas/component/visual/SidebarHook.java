package net.avicus.atlas.component.visual;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.CompetitorRule;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.objectives.Objective;
import net.avicus.atlas.module.objectives.ObjectivesModule;
import net.avicus.atlas.module.objectives.lcs.LastCompetitorStanding;
import net.avicus.atlas.module.objectives.score.ScoreObjective;
import net.avicus.atlas.util.Messages;
import net.avicus.atlas.util.ObjectiveRenderer;
import net.avicus.atlas.util.ObjectiveUtils;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.magma.util.Sidebar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

@Getter
@Setter
public abstract class SidebarHook implements Listener {

  private SidebarComponent component;
  private Match match;

  public ObjectiveRenderer getRenderer() {
    return SidebarComponent.DEFAULT_RENDERER;
  }

  public List<String> getRows(Player player, GroupsModule groups, Sidebar sidebar,
      ObjectivesModule module) {
    if (groups.getCompetitorRule() == CompetitorRule.INDIVIDUAL) {
      return getScoreLCS(player, sidebar, module, groups);
    } else {
      return getTeamDisplay(player, sidebar, module, getRenderer());
    }
  }

  public List<String> getScoreLCS(Player viewer, Sidebar sidebar, ObjectivesModule module,
      GroupsModule groups) {
    List<String> lines = new ArrayList<>();
    for (Objective objective : module.getObjectives()) {
      if (!objective.show()) {
        continue;
      }

      if (objective instanceof ScoreObjective) {
        Map<Player, Integer> points = new HashMap<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
          Competitor competitor = groups.getCompetitorOf(player).orElse(null);
          if (competitor == null) {
            continue;
          }

          ScoreObjective scoreObjective = (ScoreObjective) objective;
          int score = scoreObjective.getPoints(competitor);
          points.put(player, score);
        }

        List<Player> ordered = new ArrayList<>(points.keySet());
        ordered.sort((o1, o2) -> points.get(o2).compareTo(points.get(o1)));

        for (Player player : ordered) {
          lines.add(points.get(player) + " " + player.getDisplayName());
        }

        break;
      } else if (objective instanceof LastCompetitorStanding) {
        LastCompetitorStanding lcs = (LastCompetitorStanding) objective;
        lines.add(0, ObjectiveUtils
            .getDisplay(this.getMatch(), null, viewer, lcs, SidebarComponent.DEFAULT_RENDERER,
                false));
      }
    }

    return lines;
  }

  public List<String> getTeamDisplay(Player player, Sidebar sidebar, ObjectivesModule module,
      ObjectiveRenderer renderer) {
    List<String> lines = ObjectiveUtils.objectivesByTeam(this.getMatch(), player, module,
        this.getMatch().getRequiredModule(GroupsModule.class), renderer);

    int num = lines.size() - 1;

    // remove last space
    if (num >= 0) {
      if (lines.get(num).equals("")) {
        lines.remove(num);
      }
    }

    return lines;
  }

  @Nullable
  public Localizable getTitleFinal(ObjectivesModule module) {
    Localizable title = null;
    if (module.getObjectives().size() == module.getScores().size()) {
      title = Messages.UI_POINTS.with();
    }

    return getTitle(module).orElse(title);
  }

  public Optional<Localizable> getTitle(ObjectivesModule module) {
    return Optional.empty();
  }
}
