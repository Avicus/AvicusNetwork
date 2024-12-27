package net.avicus.atlas.sets.competitve.objectives;

import static net.avicus.atlas.util.ObjectiveUtils.ARROW_ALTERNATOR;
import static net.avicus.atlas.util.ObjectiveUtils.CHECK_MARK;
import static net.avicus.atlas.util.ObjectiveUtils.FLAG_EMPTY;
import static net.avicus.atlas.util.ObjectiveUtils.FLAG_FILLED;
import static net.avicus.atlas.util.ObjectiveUtils.SEPARATOR;
import static net.avicus.atlas.util.ObjectiveUtils.SQUARE_EMPTY;
import static net.avicus.atlas.util.ObjectiveUtils.SQUARE_FILLED;
import static net.avicus.atlas.util.ObjectiveUtils.SQUARE_SHADED;
import static net.avicus.atlas.util.ObjectiveUtils.TOUCH;
import static net.avicus.atlas.util.ObjectiveUtils.X_MARK;
import static net.avicus.atlas.util.ObjectiveUtils.getDistanceDisplay;

import java.util.Locale;
import java.util.Optional;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.objectives.Objective;
import net.avicus.atlas.sets.competitve.objectives.destroyable.leakable.LeakableObjective;
import net.avicus.atlas.sets.competitve.objectives.destroyable.monument.MonumentObjective;
import net.avicus.atlas.sets.competitve.objectives.flag.FlagObjective;
import net.avicus.atlas.sets.competitve.objectives.hill.HillObjective;
import net.avicus.atlas.sets.competitve.objectives.wool.WoolObjective;
import net.avicus.atlas.util.ObjectiveRenderer;
import net.avicus.compendium.StringUtil;
import net.avicus.compendium.utils.Strings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CompetitveRenderer extends ObjectiveRenderer {

  @Override
  public String getDisplay(Match match, Competitor competitor, Player viewer, Objective objective,
      boolean showName) {
    StringBuilder result = new StringBuilder();
    Locale locale = viewer.getLocale();

    if (objective instanceof HillObjective) {
      HillObjective hill = (HillObjective) objective;

      Optional<Competitor> capturing = hill.getCapturing();
      if (capturing.isPresent()) {
        result.append(capturing.get().getGroup().getChatColor());
      } else {
        result.append(ChatColor.WHITE);
      }

      if (hill.isCompleted() && hill.isPermanent()) {
        result.append(CHECK_MARK);
      } else {
        result.append(StringUtil.toPercent(hill.getCompletion()));
      }

      if (showName) {
        result.append(SEPARATOR);

        if (hill.getOwner().isPresent()) {
          result.append(hill.getOwner().get().getGroup().getChatColor());
        } else {
          result.append(ChatColor.WHITE);
        }

        result.append(hill.getName().render(viewer));
      } else {
        result.append("   ");
      }
    } else if (objective instanceof MonumentObjective) {
      MonumentObjective monument = (MonumentObjective) objective;

      if (monument.isCompleted()) {
        result.append(ChatColor.GREEN);
        result.append(CHECK_MARK);
      } else if (monument.isIncremental()) {
        result.append(Strings.toChatColor(monument.getCompletion()));
        result.append(StringUtil.toPercent(monument.getCompletion()));
      } else {
        result.append(ChatColor.RED);
        result.append(X_MARK);
      }

      result.append(getDistanceDisplay(competitor, viewer, objective, !showName));

      if (showName) {
        result.append(SEPARATOR);
        // No owner, has a highest completer
        Optional<Competitor> highest = monument.getHighestCompleter();
        if (!monument.getOwner().isPresent() && highest.isPresent()) {
          result.append(highest.get().getChatColor());
        }
        result.append(monument.getName().render(viewer));
      }
    } else if (objective instanceof LeakableObjective) {
      LeakableObjective leakable = (LeakableObjective) objective;

      if (leakable.isCompleted()) {
        result.append(ChatColor.GREEN);
        result.append(CHECK_MARK);
      } else if (leakable.isIncremental()) {
        result.append(Strings.toChatColor(leakable.getCompletion()));
        result.append(StringUtil.toPercent(leakable.getCompletion()));
      } else if (leakable.isTouched()) {
        result.append(ChatColor.AQUA);
        result.append(TOUCH);
      } else {
        result.append(ChatColor.RED);
        result.append(X_MARK);
      }

      result.append(getDistanceDisplay(competitor, viewer, objective, !showName));

      if (showName) {
        result.append(SEPARATOR);
        // No owner, has a highest completer
        Optional<Competitor> highest = leakable.getHighestCompleter();
        if (!leakable.getOwner().isPresent() && highest.isPresent()) {
          result.append(highest.get().getChatColor());
        }
        result.append(leakable.getName().render(viewer));
      }
    } else if (objective instanceof WoolObjective) {
      WoolObjective wool = (WoolObjective) objective;

      result.append(wool.getChatColor());

      if (wool.isCompleted()) {
        result.append(SQUARE_FILLED);
      } else if (wool.isTouched() && wool.canSeeTouched(viewer)) {
        result.append(SQUARE_SHADED);
      } else {
        result.append(SQUARE_EMPTY);
      }

      result.append(getDistanceDisplay(competitor, viewer, objective, !showName));

      if (showName) {
        result.append(SEPARATOR);
        result.append(wool.getChatColor());
        result.append(wool.getName().render(viewer));
      }
    } else if (objective instanceof FlagObjective) {
      FlagObjective flag = (FlagObjective) objective;

      if (!flag.getOwner().isPresent() && flag.getCarrier().isPresent()) {
        result.append(flag.getCarrier().get().getChatColor());
      } else {
        result.append(flag.getChatColor());
      }

      if (flag.isDropped()) {
        result.append(FLAG_EMPTY);
      }
      if (!flag.getCurrentPost().isPresent() || flag.getCarrier().isPresent()) {
        result.append(ARROW_ALTERNATOR.next());
      } else {
        result.append(FLAG_FILLED);
      }

      result.append(getDistanceDisplay(competitor, viewer, objective, !showName));

      if (showName) {
        result.append(SEPARATOR);
        result.append(flag.getChatColor());
        result.append(flag.getName().render(viewer));
      }
    }

    return result.toString();
  }
}
