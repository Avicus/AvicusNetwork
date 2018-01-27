package net.avicus.atlas.sets.walls;

import javax.annotation.Nullable;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.compendium.inventory.SingleMaterialMatcher;
import net.avicus.magma.util.region.BoundedRegion;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

/**
 * A wall.
 * <p>
 * <p>https://www.youtube.com/watch?v=t8cELTdtw6U</p>
 */
@ToString(exclude = "match")
public final class Wall {

  /**
   * The default wall material data.
   */
  private static final MaterialData BEDROCK_MATERIAL_DATA = new MaterialData(Material.BEDROCK);
  /**
   * The material data for a fallen wall.
   */
  private static final MaterialData AIR_MATERIAL_DATA = new MaterialData(Material.AIR);
  /**
   * The match.
   */
  final Match match;
  /**
   * The wall region.
   */
  private final BoundedRegion region;
  /**
   * If there is a physical wall in the {@link #match} world already.
   */
  private final boolean physical;
  /**
   * The source material matcher.
   * <p>
   * <p>When {@link #physical} is false, the source material is used to construct the wall.</p>
   */
  @Nullable
  private final SingleMaterialMatcher sourceMaterial;
  /**
   * The target material matcher.
   */
  @Nullable
  private final SingleMaterialMatcher targetMaterial;

  /**
   * Construct a wall objective.
   *
   * @param match the match the objective is in
   * @param region the wall region
   * @param physical if there is a physical wall in the {@link #match} world already
   * @param sourceMaterial the material to transition to if no physical wall exists
   * @param targetMaterial the material to transition to when the wall falls
   */
  Wall(final Match match,
      final BoundedRegion region,
      final boolean physical,
      @Nullable final SingleMaterialMatcher sourceMaterial,
      @Nullable final SingleMaterialMatcher targetMaterial
  ) {
    this.match = match;
    this.region = region;
    this.physical = physical;
    this.sourceMaterial = sourceMaterial;
    this.targetMaterial = targetMaterial;
  }

  /**
   * Transition the wall to a new state.
   *
   * @param state the new state
   */
  void transitionTo(final State state) {
    this.match.getWorld().fastBlockChange(this.region, state.material(this));
  }

  public void initialize() {
    // Transition to initial material if there is no pre-existing wall
    if (!this.physical) {
      this.transitionTo(State.CONSTRUCT);
    }
  }

  enum State {
    CONSTRUCT {
      @Override
      MaterialData material(final Wall wall) {
        @Nullable final SingleMaterialMatcher material = wall.sourceMaterial;
        return material == null ? BEDROCK_MATERIAL_DATA : material.toMaterialData();
      }
    },
    DESTROY {
      @Override
      MaterialData material(final Wall wall) {
        @Nullable final SingleMaterialMatcher material = wall.targetMaterial;
        return material == null ? AIR_MATERIAL_DATA : material.toMaterialData();
      }
    };

    abstract MaterialData material(final Wall wall);
  }
}
