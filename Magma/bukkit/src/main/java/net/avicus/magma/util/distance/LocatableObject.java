package net.avicus.magma.util.distance;

import com.google.common.base.Functions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import lombok.ToString;
import net.avicus.magma.util.StringUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.util.Vector;

/**
 * Represents an object that exists inside of a {@link org.bukkit.World} whose distance can be
 * calculated based on the location of other objects.
 *
 * @param <R> Container class used to group {@link B}'s together so that the shortest distance can
 * be calculated out of a set of {@link B}s.
 * @param <B> Base object type that this object's location will be calculated against.
 */
@ToString
public abstract class LocatableObject<R extends PlayerStore, B> {

  /**
   * Mapping of {@link R}s to the shortest distance from the object.
   **/
  private final Map<R, Integer> distanceByRef = new HashMap<R, Integer>();

  /**
   * Set of {@link DistanceCalculationMetric metrics} used to calculate distance.
   **/
  private final Set<DistanceCalculationMetric> metrics;

  /**
   * Constructor.
   *
   * @param metrics to use.
   */
  public LocatableObject(Set<DistanceCalculationMetric> metrics) {
    this.metrics = metrics;
  }

  /**
   * Convert the squared distance to a double for accuracy and proper display.
   */
  private static double distanceFromDistanceSquared(int squared) {
    return squared == Integer.MAX_VALUE ? Double.POSITIVE_INFINITY : Math.sqrt(squared);
  }

  /**
   * Get the locations from which distance can be measured relative to.
   * The shortest measurement will be used.
   */
  public abstract Iterable<Vector> getDistanceReferenceLocations(B base);

  /**
   * Get the {@link DistanceCalculationMetric metric} that should be used to calculate distance to
   * the given {@link R}.
   */
  public
  @Nullable
  DistanceCalculationMetric getDistanceCalculationMetric(R ref) {
    return Iterables.getLast(this.metrics, null);
  }

  /**
   * Get the {@link DistanceCalculationMetric.Type metric type} that should be used to calculate
   * distance to the given {@link R}.
   */
  public
  @Nullable
  DistanceCalculationMetric.Type getDistanceCalculationMetricType(R ref) {
    DistanceCalculationMetric metric = getDistanceCalculationMetric(ref);
    return metric == null ? null : metric.type;
  }

  public
  @Nullable
  R closest() {
    if (this.distanceByRef.isEmpty()) {
      return null;
    }

    return Ordering.natural().onResultOf(Functions.forMap(this.distanceByRef))
        .immutableSortedCopy(distanceByRef.keySet()).get(0);
  }

  /**
   * Is distance relevant at the present moment for the given ref?
   * That is, can it be measured and affect the calculation of closeness?
   */
  public boolean isDistanceRelevant(R ref) {
    return getDistanceCalculationMetric(ref) != null;
  }

  /**
   * Check if a given {@link B base object} can update the distance for the object.
   */
  protected abstract boolean canUpdateDistance(B base);

  /**
   * Check if a given {@link BlockState} can update the distance for the object.
   *
   * @param oldState previous state
   * @param newState new state
   */
  protected boolean canBlockUpdateDistance(BlockState oldState, BlockState newState) {
    return true;
  }

  /**
   * Get the distance from a ref.
   */
  public int getDistance(R ref) {
    return this.distanceByRef.getOrDefault(ref, Integer.MAX_VALUE);
  }

  /**
   * Get the minimum distance the given ref has been from the object at any time.
   * The given metric determines exactly how this is measured.
   */
  public double getMinimumDistance(R ref) {
    return distanceFromDistanceSquared(this.getDistance(ref));
  }

  /**
   * Reset the distance from the object for a given {@link R}.
   *
   * @return the old distance, if there was one.
   */
  public
  @Nullable
  Integer resetDistance(R ref) {
    Integer oldDistance = distanceByRef.remove(ref);
    return oldDistance;
  }

  /**
   * Reset the distance from the object for a given {@link R}.
   *
   * @return the old distance, if there was one.
   */
  public
  @Nullable
  Integer resetDistance(B base) {
    Integer oldDistance = distanceByRef.remove(conversionFunc().apply(base));
    return oldDistance;
  }

  /**
   * Reset all of the distances from the object.
   *
   * @return if any old distances were cleared.
   */
  public boolean resetDistance() {
    boolean res = !this.distanceByRef.isEmpty();
    this.distanceByRef.clear();
    return res;
  }

  /**
   * Get the distance {@link B} has from the object based on a supplied {@link Location}.
   */
  public int getDistanceFrom(B base, Location location) {
    if (Double.isInfinite(location.lengthSquared())) {
      return Integer.MAX_VALUE;
    }

    DistanceCalculationMetric metric = getDistanceCalculationMetric(conversionFunc().apply(base));
    if (metric == null) {
      return Integer.MAX_VALUE;
    }

    int minimumDistance = Integer.MAX_VALUE;
    for (Vector v : getDistanceReferenceLocations(base)) {
      // If either point is at infinity, the distance is infinite
      if (Double.isInfinite(v.lengthSquared())) {
        continue;
      }

      int disX = location.getBlockX() - v.getBlockX();
      int disY = location.getBlockY() - v.getBlockY();
      int disZ = location.getBlockZ() - v.getBlockZ();

      // Note: distances stay squared as long as possible
      int distance;
      if (metric.horizontal) {
        distance = disX * disX + disZ * disZ;
      } else {
        distance = disX * disX + disY * disY + disZ * disZ;
      }

      if (distance < minimumDistance) {
        minimumDistance = distance;
      }
    }

    return minimumDistance;
  }

  /**
   * Function used to convert {@link B}'s to their base {@link R}.
   */
  public abstract Function<B, R> conversionFunc();

  /**
   * Check if a given {@link B base object} can view the distance for the object regardless of
   * state.
   */
  public abstract boolean canViewAlways(B base);

  /**
   * Update the distance {@link B} has to the object.
   *
   * @return if the {@link R} associated with {@link B}'s minimum distance changed.
   */
  public boolean updateDistance(B base, Location location) {
    R ref = conversionFunc().apply(base);
    if (ref == null) {
      return false;
    }
    if (isDistanceRelevant(ref) && canUpdateDistance(base)) {
      int oldDistance = distanceByRef.getOrDefault(ref, Integer.MAX_VALUE);
      int newDistance = getDistanceFrom(base, location);
      if (newDistance < oldDistance) {
        distanceByRef.put(ref, newDistance);
        return true;
      }
    }
    return false;
  }

  /**
   * Determine if a viewer is allowed to see the distance {@link R} has to the object.
   */
  public boolean shouldShowDistance(@Nullable R ref, B viewer) {
    return canViewAlways(viewer);
  }

  /**
   * Get the color that should be used beside distance strings.
   */
  public ChatColor distanceColor(R ref, B viewer) {
    return ChatColor.GRAY;
  }

  /**
   * Convert the distance a {@link R} has to this object to a user-readable string.
   *
   * @param ref to get distance for
   * @param viewer who is seeing the string
   * @param sub if the string should be converted to subscript
   */
  public String stringifyDistance(@Nullable R ref, B viewer, boolean sub) {
    if (!shouldShowDistance(ref, viewer)) {
      return "";
    }

    String text;
    double distance = this.getMinimumDistance(ref);
    if (distance == Double.POSITIVE_INFINITY) {
      text = "\u221e"; // ∞
    } else {
      if (sub) {
        text = StringUtil.subScript(String.format("%.1f", distance));
      } else {
        text = String.format("%.1f", distance);
      }
    }

    return distanceColor(ref, viewer) + text;
  }
}
