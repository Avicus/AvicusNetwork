package net.avicus.atlas.module.objectives.entity;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicDouble;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.module.objectives.Objective;
import net.avicus.atlas.module.objectives.locatable.DistanceMetrics;
import net.avicus.atlas.module.objectives.locatable.LocatableObjective;
import net.avicus.magma.util.region.BoundedRegion;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@Getter
@ToString(exclude = "match")
public class EntityObjective extends LocatableObjective implements Objective {

  private final Match match;

  private final LocalizedXmlString name;
  private final boolean show;
  private final Optional<Competitor> owner;
  private final Optional<Check> damageCheck;
  private final BoundedRegion region;
  private final Optional<Integer> points;

  private final EntityType typeMatcher;

  private ConcurrentMap<Entity, AtomicDouble> trackedEntities;

  private HashMap<Competitor, AtomicInteger> damageDealt = Maps.newHashMap();
  private HashMap<Competitor, AtomicDouble> completions = Maps.newHashMap();

  private double completion;

  private AtomicDouble maxHealthSum;

  public EntityObjective(DistanceMetrics metrics,
      Match match,
      LocalizedXmlString name,
      boolean show,
      Optional<Competitor> owner,
      Optional<Check> damageCheck,
      BoundedRegion region,
      Optional<Integer> points,
      EntityType typeMatcher) {
    super(metrics, match);
    this.match = match;
    this.name = name;
    this.owner = owner;
    this.show = show;
    this.damageCheck = damageCheck;
    this.region = region;
    this.points = points;
    this.typeMatcher = typeMatcher;
  }

  @Override
  public void initialize() {
    this.trackedEntities = Maps.newConcurrentMap();
    this.maxHealthSum = new AtomicDouble();
    this.match.getWorld().getEntitiesByClass(this.typeMatcher.getEntityClass()).stream()
        .filter(this.region::contains)
        .forEach(e -> this.trackedEntities.put(e, new AtomicDouble()));
    this.trackedEntities.keySet().forEach(e -> {
      if (e instanceof EntityLiving) {
        this.maxHealthSum.addAndGet(((EntityLiving) e).getMaxHealth());
        this.trackedEntities.get(e).addAndGet(((EntityLiving) e).getHealth());
      } else {
        this.maxHealthSum.addAndGet(1);
        this.trackedEntities.get(e).addAndGet(1);
      }
    });
  }

  public void updateCompletion() {
    AtomicDouble currentHealth = new AtomicDouble();

    new HashMap<>(this.trackedEntities).keySet().forEach(e -> {
      if (e instanceof EntityLiving) {
        currentHealth.addAndGet(((EntityLiving) e).getHealth());
        this.trackedEntities.get(e).set(((EntityLiving) e).getHealth());
        if (e.isDead()) {
          this.trackedEntities.remove(e);
        }
      } else {
        if (e.isDead()) {
          this.trackedEntities.get(e).set(0);
          this.trackedEntities.remove(e);
        } else {
          currentHealth.addAndGet(1);
        }
      }
    });

    this.completion = currentHealth.get() / this.maxHealthSum.get();
  }

  @Override
  public boolean canComplete(Competitor competitor) {
    return !this.owner.isPresent() || !isOwner(competitor.getGroup());
  }

  public boolean isOwner(Group group) {
    return this.owner.isPresent() && this.owner.get().equals(group);
  }

  /**
   * Get the {@link Competitor} with the most completion.
   * Will return empty if no completion has occurred or there is a tie for most.
   */
  public Optional<Competitor> getHighestCompleter() {
    Optional<Competitor> most = Optional.empty();

    double highest = Double.MIN_VALUE;

    List<Competitor> ties = new ArrayList<>();

    for (Map.Entry<Competitor, AtomicDouble> entry : this.completions.entrySet()) {
      if (entry.getValue().get() == highest) {
        ties.add(entry.getKey());
      } else if (entry.getValue().get() > highest) {
        ties.clear();
        highest = entry.getValue().get();
        most = Optional.of(entry.getKey());
      }
    }

    if (ties.isEmpty()) {
      return most;
    } else {
      return Optional.empty();
    }
  }

  @Override
  public boolean isCompleted(Competitor competitor) {
    return getCompletion(competitor) >= 1.0;
  }

  @Override
  public double getCompletion(Competitor competitor) {
    return completions.getOrDefault(competitor, new AtomicDouble()).get();
  }

  @Override
  public boolean isIncremental() {
    return true;
  }

  @Override
  public boolean isCompleted() {
    return getCompletion() >= 1.0;
  }

  @Override
  public Iterable<Vector> getDistanceReferenceLocations(Player base) {
    return this.trackedEntities.keySet().stream().map(e -> e.getLocation().toVector())
        .collect(Collectors.toList());
  }

  @Override
  public boolean show() {
    return this.show;
  }
}
