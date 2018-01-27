package net.avicus.atlas.sets.competitve.objectives.destroyable.leakable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.GroupVariable;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.groups.teams.Team;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.module.objectives.TouchableDistanceMetrics;
import net.avicus.atlas.sets.competitve.objectives.destroyable.DestroyableEventInfo;
import net.avicus.atlas.sets.competitve.objectives.destroyable.DestroyableObjective;
import net.avicus.atlas.sets.competitve.objectives.phases.DestroyablePhase;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.inventory.MultiMaterialMatcher;
import net.avicus.compendium.inventory.SingleMaterialMatcher;
import net.avicus.compendium.locale.text.LocalizedFormat;
import net.avicus.magma.util.region.BoundedRegion;
import net.avicus.magma.util.region.modifiers.JoinRegion;
import net.avicus.magma.util.region.shapes.BlockRegion;
import net.avicus.magma.util.region.shapes.CuboidRegion;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

@Getter
@ToString
public class LeakableObjective extends DestroyableObjective {

  private final LocalizedXmlString name;
  private final Optional<Team> owner;
  private final BoundedRegion leakArea;

  private final SingleMaterialMatcher completedState = new SingleMaterialMatcher(Material.AIR);

  private JoinRegion<BlockRegion> liquidRegion;
  private JoinRegion<CuboidRegion> liquidRegionPadded;

  private Material liquid;
  private List<Material> allowedLiquidTransformations = new ArrayList<>();

  @Setter
  private Optional<DestroyableEventInfo> lastBreak = Optional.empty();
  @Setter
  private boolean completed = false;

  public LeakableObjective(Match match, TouchableDistanceMetrics metrics, LocalizedXmlString name,
      Optional<Team> owner, BoundedRegion region, Optional<Integer> points,
      Optional<Integer> pointsPerBlock, MultiMaterialMatcher materials, boolean destroyable,
      boolean repairable, boolean enforceAntiRepair, boolean fireworks, Optional<Check> breakCheck,
      Optional<Check> repairCheck, double neededCompletion, boolean anyRepair,
      Optional<DestroyablePhase> phase, int leakDistance) {
    super(match, metrics, region, points, pointsPerBlock, materials, destroyable, repairable,
        enforceAntiRepair, fireworks, breakCheck, repairCheck, Optional.empty(), neededCompletion,
        anyRepair, phase);
    this.name = name;
    this.owner = owner;

    Vector leakMin = new Vector(region.getMin().getX() - 4, 0, region.getMin().getZ() - 4);
    Vector leakMax = new Vector(region.getMax().getX() + 4, region.getMin().getY() - leakDistance,
        region.getMax().getZ() + 4);

    this.leakArea = new CuboidRegion(leakMin, leakMax);
  }

  @Override
  public void initialize() {
    super.initialize();

    List<BlockRegion> liquids = new ArrayList<>();

    this.getRegion().iterator().forEachRemaining(vector -> {
      Block block = this.getMatch().getWorld()
          .getBlockAt(vector.toLocation(this.getMatch().getWorld()));
      if (block.getType().equals(Material.LAVA) || block.getType()
          .equals(Material.STATIONARY_LAVA)) {
        liquids.add(new BlockRegion(vector));

        this.liquid = block.getType();
      }
    });

    this.liquidRegion = new JoinRegion<>(liquids);
    if (this.liquid.equals(Material.LAVA) || this.liquid.equals(Material.STATIONARY_LAVA)) {
      this.allowedLiquidTransformations.add(Material.LAVA);
      this.allowedLiquidTransformations.add(Material.STATIONARY_LAVA);
    } else {
      this.allowedLiquidTransformations.add(Material.WATER);
      this.allowedLiquidTransformations.add(Material.STATIONARY_WATER);
    }
  }

  public boolean isOwner(Group group) {
    return this.owner.isPresent() && this.owner.get().equals(group);
  }

  @Override
  public boolean canComplete(Competitor competitor) {
    if (this.getBreakCheck().isPresent() && competitor.getGroup() instanceof Team) {
      Team team = (Team) competitor.getGroup();
      CheckContext context = new CheckContext(this.getMatch());
      context.add(new GroupVariable(team));
      if (this.getBreakCheck().get().test(context).fails()) {
        return false;
      }
    }
    return !this.owner.isPresent() || !isOwner(competitor.getGroup());
  }

  @Override
  public boolean isCompleted(Competitor competitor) {
    return isCompleted() && lastBreak.isPresent() && competitor
        .hasPlayer(lastBreak.get().getActor());
  }

  @Override
  public boolean isIncremental() {
    return false;
  }

  @Override
  public LocalizedFormat getTouchMessage() {
    return Messages.GENERIC_OBJECTIVE_TOUCHED;
  }
}
