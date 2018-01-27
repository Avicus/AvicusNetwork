package net.avicus.atlas.module.objectives;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import lombok.Getter;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.objectives.locatable.LocatableObjective;
import net.avicus.compendium.locale.text.LocalizableFormat;
import net.avicus.magma.util.distance.DistanceCalculationMetric;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public abstract class TouchableObjective extends LocatableObjective implements Objective {

  private final Match match;
  @Getter
  private final Set<Player> recentTouchers;
  @Getter
  private final Set<Competitor> touchers;
  @Getter
  private final TouchableDistanceMetrics metrics;

  public TouchableObjective(Match match, TouchableDistanceMetrics metrics) {
    super(metrics, match);
    this.match = match;
    this.metrics = metrics;
    this.recentTouchers = new HashSet<>();
    this.touchers = new HashSet<>();
  }

  public boolean hasTouchedRecently(Player player) {
    return this.recentTouchers.contains(player);
  }

  public void setTouchedRecently(Player player, boolean touched) {
    if (touched) {
      Competitor competitor = this.match.getRequiredModule(GroupsModule.class)
          .getCompetitorOf(player).orElse(null);
      if (competitor == null) {
        return;
      }
      this.setTouched(competitor, true);
      if (this.isTouchRelevant(player)) {
        this.recentTouchers.add(player);
      }
    } else {
      this.recentTouchers.remove(player);
    }
  }

  public boolean isTouched() {
    return !this.touchers.isEmpty();
  }

  public boolean hasTouched(Competitor competitor) {
    return this.touchers.contains(competitor);
  }

  public void setTouched(Competitor competitor, boolean touched) {
    if (touched) {
      this.touchers.add(competitor);
    } else {
      this.touchers.remove(competitor);
    }
  }

  public boolean canSeeTouched(Player player) {
    Optional<Competitor> competitor = this.match.getRequiredModule(GroupsModule.class)
        .getCompetitorOf(player);
    if (competitor.isPresent()) {
      for (Competitor toucher : this.touchers) {
        if (toucher.equals(competitor.get())) {
          return true;
        }
      }
      return false;
    }
    // No Competitor = spectator
    return true;
  }

  @Override
  public ChatColor distanceColor(Competitor ref, Player viewer) {
    if (isCompleted()) {
      return super.distanceColor(ref, viewer);
    }

    return hasTouched(ref) ? ChatColor.AQUA : super.distanceColor(ref, viewer);
  }

  public boolean isTouchRelevant(Player player) {
    Optional<Competitor> competitor = this.match.getRequiredModule(GroupsModule.class)
        .getCompetitorOf(player);
    if (competitor.isPresent()) {
      return !this.isCompleted(competitor.get()) && !this.hasTouchedRecently(player);
    }
    return false;
  }

  public abstract LocalizableFormat getTouchMessage();

  @Override
  public boolean shouldShowDistance(@Nullable Competitor ref, Player viewer) {
    if (isTouched()) {
      return getMetrics().getPostTouchMetric() != null;
    }

    return super.shouldShowDistance(ref, viewer);
  }

  @Nullable
  @Override
  public DistanceCalculationMetric getDistanceCalculationMetric(Competitor ref) {
    if (hasTouched(ref)) {
      return this.metrics.getPostTouchMetric();
    } else if (isCompleted(ref)) {
      return this.metrics.getPostCompleteMetric();
    } else {
      return this.metrics.getPreCompleteMetric();
    }
  }
}
