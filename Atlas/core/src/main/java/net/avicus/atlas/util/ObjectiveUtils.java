package net.avicus.atlas.util;

import com.google.common.collect.ArrayListMultimap;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.AtlasConfig;
import net.avicus.atlas.component.visual.SidebarComponent;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.objectives.Objective;
import net.avicus.atlas.module.objectives.ObjectivesModule;
import net.avicus.atlas.module.objectives.locatable.LocatableObjective;
import net.avicus.compendium.alternator.TimedAlternator;
import net.avicus.compendium.locale.text.Localizable;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ObjectiveUtils {

  public static final String CHECK_MARK = "✔";
  public static final String TOUCH = "ᚕ";
  public static final String X_MARK = "✕";
  public static final String SQUARE_EMPTY = "⬜";
  public static final String SQUARE_SHADED = "▒";
  public static final String SQUARE_FILLED = "⬛";
  public static final String FLAG_EMPTY = "⚐";
  public static final String FLAG_FILLED = "⚑";
  public static final String ARROW_FILLED_TOP = "➣";
  public static final String ARROW_FILLED_BOTTOM = "➢";
  public static final String SEPARATOR = " " + ChatColor.GRAY + "-" + ChatColor.RESET + " ";
  public static final String TEAM_SEPERATOR = ChatColor.RESET + ":  ";

  public static final TimedAlternator<String> ARROW_ALTERNATOR = new TimedAlternator<>(
      500,
      ARROW_FILLED_TOP,
      ARROW_FILLED_BOTTOM);

  public static String getDisplay(Match match, Competitor competitor, Player viewer,
      Objective objective, ObjectiveRenderer renderer, boolean showName) {
    String res = renderer.getDisplay(match, competitor, viewer, objective, showName);
    if (res.isEmpty() && !renderer.equals(SidebarComponent.DEFAULT_RENDERER)) {
      res = SidebarComponent.DEFAULT_RENDERER
          .getDisplay(match, competitor, viewer, objective, showName);
    }

    return res;
  }

  public static String getDistanceDisplay(@Nullable Competitor competitor, Player viewer,
      Objective objective, boolean condense) {
    if (!AtlasConfig.isScrimmage()) {
      return condense ? "  " : "";
    }

    GroupsModule groups = Atlas.getMatch().getRequiredModule(GroupsModule.class);
    if ((objective.canComplete(viewer) || groups.isObserving(viewer))
        && objective instanceof LocatableObjective) {
      LocatableObjective locatable = (LocatableObjective) objective;
      if (competitor == null) {
        if (((LocatableObjective) objective).isCompleted()
            && ((LocatableObjective) objective).getMetrics().getPostCompleteMetric()
            == null) {
          return condense ? "  " : "";
        }
        // Shared objective (playing), show comp proximity
        Competitor own = groups.getCompetitorOf(viewer).orElse(null);
        if (own != null) {
          return locatable.stringifyDistance(own, viewer, true);
        }
        // Shared objective (spec), brackets with Competitor color.
        Competitor closest = locatable.closest();
        if (closest == null || !locatable.shouldShowDistance(closest, viewer)) {
          return condense ? "  " : "";
        }

        String left = closest.getChatColor() + (condense ? "[" : " [");
        String right = closest.getChatColor() + "]";
        String dis = locatable.stringifyDistance(closest, viewer, false);
        if (!dis.isEmpty()) {
          return left + dis + right;
        }
      } else {
        if (((LocatableObjective) objective).isCompleted(competitor)
            && ((LocatableObjective) objective).getMetrics().getPostCompleteMetric()
            == null) {
          return condense ? "  " : "";
        }
        String dis = locatable.stringifyDistance(competitor, viewer, true);
        return dis.isEmpty() ? condense ? "  " : "" : dis;
      }
    }
    return condense ? "  " : "";
  }


  public static List<String> objectivesByTeam(Match match, Player player, ObjectivesModule module,
      GroupsModule groupsModule, ObjectiveRenderer renderer) {
    Locale locale = player.getLocale();

    List<Objective> shared = new ArrayList<>();
    ArrayListMultimap<Competitor, Objective> specific = ArrayListMultimap.create();

    for (Objective objective : module.getObjectives()) {
      if (!objective.show()) {
        continue;
      }

      Competitor completer = null;
      for (Competitor test : groupsModule.getCompetitors()) {
        if (objective.canComplete(test)) {
          if (completer != null) {
            shared.add(objective);
            completer = null;
            break;
          }
          completer = test;
        }
      }

      if (completer != null) {
        specific.put(completer, objective);
      }
    }

    List<String> lines = shared
        .stream()
        .filter(Objective::show)
        .map(objective -> getDisplay(match, null, player, objective, renderer, true))
        .collect(Collectors.toList());

    if (shared.size() > 0 && specific.size() > 0) {
      lines.add(" ");
    }

    // Shared + # of competitors (* 2 for spaces and then removing the trailing beginning and end spaces) and the number of objectives to be added.
    boolean condense =
        (lines.size() + ((specific.keySet().size() * 2) - 2) + specific.values().size()) >= 16;
    // Shared + # of competitors (* 3 for spaces + 1 objective line and then removing the trailing beginning and end spaces).
    boolean superCondense = (lines.size() + ((specific.keySet().size() * 3) - 2)) >= 16;

    for (Competitor completer : specific.keySet()) {
      List<Objective> objectives = specific.get(completer);

      if (objectives.size() == 0) {
        continue;
      }

      Localizable name = completer.getColoredName();
      if (completer.hasPlayer(player)) {
        name.style().bold();
      }

      String fullLine = "";
      if (superCondense) {
        fullLine = name.translate(locale).toLegacyText();
        fullLine += TEAM_SEPERATOR;
      } else {
        lines.add(name.translate(locale).toLegacyText());
      }

      if (condense && !superCondense) {
        fullLine += " ";
      }

      for (Objective objective : objectives) {
        String line;
        if (condense) {
          fullLine += getDisplay(match, completer, player, objective, renderer, false);
        } else {
          line = " " + getDisplay(match, completer, player, objective, renderer, true);
          lines.add(line);
        }
      }

      if (!fullLine.isEmpty()) {
        lines.add(fullLine);
      }

      lines.add("");
    }

    if (lines.size() > 16) {
      lines = lines.stream().filter((s -> !s.equals(""))).collect(Collectors.toList());
    }

    // Remove space between shared and competitors
    if (lines.size() > 16) {
      lines = lines.stream().filter((s -> !s.equals(" "))).collect(Collectors.toList());
    }

    return lines;
  }

}
