package net.avicus.atlas.sets.competitve.objectives.destroyable;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicDouble;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.Setter;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.LocationVariable;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.objectives.StagnatedCompletionObjective;
import net.avicus.atlas.module.objectives.TouchableDistanceMetrics;
import net.avicus.atlas.module.objectives.TouchableObjective;
import net.avicus.atlas.runtimeconfig.fields.ConfigurableField;
import net.avicus.atlas.runtimeconfig.fields.OptionalField;
import net.avicus.atlas.runtimeconfig.fields.RegisteredObjectField;
import net.avicus.atlas.runtimeconfig.fields.SimpleFields.BooleanField;
import net.avicus.atlas.runtimeconfig.fields.SimpleFields.IntField;
import net.avicus.atlas.sets.competitve.objectives.phases.DestroyablePhase;
import net.avicus.atlas.util.Players;
import net.avicus.compendium.inventory.MultiMaterialMatcher;
import net.avicus.compendium.inventory.SingleMaterialMatcher;
import net.avicus.magma.util.region.BoundedRegion;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.EntityEffect;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

@Getter
public abstract class DestroyableObjective extends TouchableObjective implements
    StagnatedCompletionObjective {

  private final Match match;

  private BoundedRegion region;
  private Optional<Integer> points;
  private Optional<Integer> pointsPerBlock;
  private final MultiMaterialMatcher materials;

  private boolean destroyable;
  private boolean repairable;
  private boolean enforceAntiRepair;
  private final boolean fireworks;

  private Optional<Check> breakCheck;
  private Optional<Check> repairCheck;

  private final SingleMaterialMatcher completedState;

  private final double neededCompletion;

  private boolean anyRepair;

  @Setter
  private Optional<DestroyablePhase> phase;

  private double completionDouble;
  private HashMap<Block, MaterialData> originals;
  private List<Block> remaining;

  private HashMap<Competitor, AtomicInteger> brokenBlocks = Maps.newHashMap();
  private HashMap<Competitor, AtomicDouble> completions = Maps.newHashMap();

  public DestroyableObjective(Match match, TouchableDistanceMetrics metrics, BoundedRegion region,
      Optional<Integer> points, Optional<Integer> pointsPerBlock, MultiMaterialMatcher materials,
      boolean destroyable, boolean repairable, boolean enforceAntiRepair, boolean fireworks,
      Optional<Check> breakCheck, Optional<Check> repairCheck,
      Optional<SingleMaterialMatcher> completedState, double neededCompletion, boolean anyRepair,
      Optional<DestroyablePhase> phase) {
    super(match, metrics);
    this.match = match;
    this.region = region;
    this.points = points;
    this.pointsPerBlock = pointsPerBlock;
    this.materials = materials;
    this.destroyable = destroyable;
    this.repairable = repairable;
    this.enforceAntiRepair = enforceAntiRepair;
    this.fireworks = fireworks;
    this.breakCheck = breakCheck;
    this.repairCheck = repairCheck;
    this.completedState = completedState.orElse(new SingleMaterialMatcher(Material.AIR));
    this.neededCompletion = neededCompletion;
    this.anyRepair = anyRepair;
    this.phase = phase;
  }

  public boolean canPlayerBreak(Player player, Block block) {
    if (!this.materials.matches(block.getState())) {
      return false;
    }

    if (this.breakCheck.isPresent()) {
      CheckContext context = new CheckContext(this.match);
      context.add(new PlayerVariable(player));
      context.add(new LocationVariable(block.getLocation()));
      return this.breakCheck.get().test(context).passes();
    }
    return true;
  }

  public void recordBreak(Player player) {
    Optional<Competitor> competitor = match.getRequiredModule(GroupsModule.class)
        .getCompetitorOf(player);
    competitor.ifPresent(c -> {
      this.brokenBlocks.putIfAbsent(c, new AtomicInteger());
      this.brokenBlocks.get(c).addAndGet(1);
    });
  }

  @Override
  public boolean isCompleted(Competitor competitor) {
    return this.completions.getOrDefault(competitor, new AtomicDouble()).get()
        >= this.neededCompletion;
  }

  public boolean canPlayerRepair(Player player, Block block) {
    if (!this.isRepairable()) {
      return false;
    }

    if (this.repairCheck.isPresent()) {
      CheckContext context = new CheckContext(this.match);
      context.add(new PlayerVariable(player));
      context.add(new LocationVariable(block.getLocation()));
      return this.repairCheck.get().test(context).passes();
    }
    return true;
  }

  public boolean isInside(Block block) {
    return this.originals.containsKey(block);
  }

  public boolean isRemaining(Block block) {
    return this.remaining.contains(block);
  }

  public MaterialData getOriginal(Block block) {
    return this.originals.get(block);
  }

  public void updateMaterial(MultiMaterialMatcher find, SingleMaterialMatcher replace) {
    this.materials.replaceMaterial(find, replace);
    for (Map.Entry<Block, MaterialData> original : this.getOriginals().entrySet()) {
      if (find.matches(original.getKey().getState())) {
        original.getKey().setType(replace.getMaterial());
        if (replace.isDataRelevant()) {
          original.getValue().setData(replace.getData().get());
        }
      }
    }
    for (Block remain : this.getRemaining()) {
      if (find.matches(remain.getState())) {
        remain.setType(replace.getMaterial());
        if (replace.isDataRelevant()) {
          remain.setData(replace.getData().get());
        }
      }
    }
  }

  public void spawnFirework(Block block, Competitor competitor) {
    if (!this.fireworks) {
      return;
    }

    Location location = block.getLocation().add(0.5, 0.5, 0.5);
    Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
    FireworkMeta meta = firework.getFireworkMeta();
    meta.setPower(0);

    FireworkEffect.Builder builder = FireworkEffect.builder();
    builder.with(FireworkEffect.Type.BURST);
    builder.withColor(competitor.getFireworkColor());
    builder.withTrail();

    meta.addEffect(builder.build());
    firework.setFireworkMeta(meta);

    firework.playEffect(EntityEffect.FIREWORK_EXPLODE);

    // 1.8-1.9 Support
    Players.playFireworkSound();
  }

  @Override
  public void initialize() {
    this.originals = new HashMap<>();
    this.remaining = new ArrayList<>();

    for (Vector point : this.region) {
      Block block = point.toLocation(this.match.getWorld()).getBlock();

      if (this.materials.matches(block.getState())) {
        this.originals.put(block, block.getState().getData());
        this.remaining.add(block);
      }
    }

    this.recalculateCompletion();
  }

  @Override
  public Iterable<Vector> getDistanceReferenceLocations(Player base) {
    return Collections.singleton(this.region.getCenter());
  }

  public void repair(Block block) {
    this.remaining.add(block);
  }

  public void recalculateCompletion() {
    Iterator<Block> iterator = this.remaining.iterator();
    while (iterator.hasNext()) {
      Block block = iterator.next();
      if (this.completedState.matches(block.getState())) {
        iterator.remove();
      }
    }

    this.completionDouble = 1.0 - (double) this.remaining.size() / (double) this.originals.size();

    this.brokenBlocks.forEach((c, b) -> {
      this.completions.putIfAbsent(c, new AtomicDouble());
      this.completions.get(c).set((double) b.get() / (double) this.originals.size());
    });

    if (!this.isIncremental()) {
      if (this.isCompleted()) {
        this.completionDouble = 1.0;
      } else if (this.isTouched()) {
        this.completionDouble = 0.5;
      } else {
        this.completionDouble = 0;
      }

      return;
    }
  }

  @Override
  public double getCompletion() {
    return this.getCompletionDouble();
  }

  /**
   * Get the {@link Competitor} with the most completion.
   * Will return empty if no completion has occurred or there is a tie for most.
   */
  @Override
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
  public double getCompletion(Competitor competitor) {
    if (!canComplete(competitor)) {
      throw new IllegalArgumentException("Competitor cannot complete objective.");
    }
    return this.completions.getOrDefault(competitor, new AtomicDouble()).get();
  }

  public boolean shouldEnforceRepairRules() {
    return this.isRepairable() || this.enforceAntiRepair;
  }

  @Override
  public ConfigurableField[] getFields() {
    return ArrayUtils.addAll(super.getFields(),
        new RegisteredObjectField<>("Region", () -> this.region, (v) -> this.region = v, BoundedRegion.class),
        new OptionalField<>("Completion Points", () -> this.points, (v) -> this.points = v, new IntField("points")),
        new OptionalField<>("Points Per Block", () -> this.pointsPerBlock, (v) -> this.pointsPerBlock = v, new IntField("pointsper")),
        new BooleanField("Destroyable With TNT", () -> this.destroyable, (v) -> this.destroyable = v),
        new BooleanField("Repairable", () -> this.repairable, (v) -> this.repairable = v),
        new BooleanField("Enforce Anti Repair", () -> this.enforceAntiRepair, (v) -> this.enforceAntiRepair = v),
        new BooleanField("Repairable With Anything", () -> this.anyRepair, (v) -> this.anyRepair = v),
        new OptionalField<>("Break Check", () -> this.breakCheck, (v) -> this.breakCheck = v, new RegisteredObjectField<>("check", Check.class)),
        new OptionalField<>("Repair Check", () -> this.repairCheck, (v) -> this.repairCheck = v, new RegisteredObjectField<>("check", Check.class))
    );
  }
}
