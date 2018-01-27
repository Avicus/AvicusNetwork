package net.avicus.atlas.module.objectives.locatable;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.objectives.GlobalObjective;
import net.avicus.atlas.module.objectives.Objective;
import net.avicus.magma.util.distance.DistanceCalculationMetric;
import net.avicus.magma.util.distance.LocatableObject;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

@ToString
public abstract class LocatableObjective extends LocatableObject<Competitor, Player> implements
    GlobalObjective, Objective {

  private final GroupsModule module;
  @Getter
  private final DistanceMetrics metrics;

  public LocatableObjective(DistanceMetrics metrics, Match match) {
    super(Collections.singleton(metrics.getPreCompleteMetric()));
    this.module = match.getRequiredModule(GroupsModule.class);
    this.metrics = metrics;
  }

  @Override
  protected boolean canUpdateDistance(Player base) {
    return canComplete(base);
  }

  @Override
  public Function<Player, Competitor> conversionFunc() {
    return (p) -> module.getCompetitorOf(p).orElse(null);
  }

  @Override
  public boolean canViewAlways(Player base) {
    return module.getGroup(base).isSpectator();
  }

  @Override
  public ChatColor distanceColor(Competitor ref, Player viewer) {
    return isCompleted(ref) ? ChatColor.GREEN : super.distanceColor(ref, viewer);
  }

  @Override
  public boolean isDistanceRelevant(Competitor ref) {
    return !ref.getGroup().isObserving() && canComplete(ref);
  }

  @Nullable
  @Override
  public DistanceCalculationMetric getDistanceCalculationMetric(Competitor ref) {
    if (isCompleted(ref)) {
      return this.metrics.getPostCompleteMetric();
    } else {
      return this.metrics.getPreCompleteMetric();
    }
  }

  @Override
  public boolean shouldShowDistance(@Nullable Competitor ref, Player viewer) {
    if ((ref == null && isCompleted()) || isCompleted(ref)) {
      return getMetrics().getPostCompleteMetric() != null;
    }

    return this.canComplete(viewer) || module.isSpectator(viewer);
  }

  @Nullable
  public DistanceCalculationMetric.Type getDistanceCalculationMetricType(Player player) {
    Optional<Competitor> comp = this.module.getCompetitorOf(player);
    if (comp.isPresent()) {
      return super.getDistanceCalculationMetricType(comp.get());
    } else {
      return null;
    }
  }
}
