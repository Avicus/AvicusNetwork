package net.avicus.magma.util.properties;

import java.util.Arrays;

/**
 * This is from NCP
 */
public class Props {

  /**
   * The Enum ToolType.
   *
   * @author mc_dev
   */
  public static enum ToolType {

    /**
     * The none.
     */
    NONE,

    /**
     * The sword.
     */
    SWORD,

    /**
     * The shears.
     */
    SHEARS,

    /**
     * The spade.
     */
    SPADE,

    /**
     * The axe.
     */
    AXE,

    /**
     * The pickaxe.
     */
    PICKAXE,
    //		HOE,
  }

  /**
   * The Enum MaterialBase.
   *
   * @author mc_dev
   */
  public static enum MaterialBase {

    /**
     * The none.
     */
    NONE(0, 1f),

    /**
     * The wood.
     */
    WOOD(1, 2f),

    /**
     * The stone.
     */
    STONE(2, 4f),

    /**
     * The iron.
     */
    IRON(3, 6f),

    /**
     * The diamond.
     */
    DIAMOND(4, 8f),

    /**
     * The gold.
     */
    GOLD(5, 12f);
    /**
     * Index for array.
     */
    public final int index;

    /**
     * The break multiplier.
     */
    public final float breakMultiplier;

    /**
     * Instantiates a new material base.
     *
     * @param index the index
     * @param breakMultiplier the break multiplier
     */
    private MaterialBase(int index, float breakMultiplier) {
      this.index = index;
      this.breakMultiplier = breakMultiplier;
    }

    /**
     * Gets the by id.
     *
     * @param id the id
     * @return the by id
     */
    public static final MaterialBase getById(final int id) {
      for (final MaterialBase base : MaterialBase.values()) {
        if (base.index == id) {
          return base;
        }
      }
      throw new IllegalArgumentException("Bad id: " + id);
    }
  }

  /**
   * Properties of a tool.
   **/
  public static class ToolProps {

    /**
     * The tool type.
     */
    public final ToolType toolType;

    /**
     * The material base.
     */
    public final MaterialBase materialBase;

    /**
     * Instantiates a new tool props.
     *
     * @param toolType the tool type
     * @param materialBase the material base
     */
    public ToolProps(ToolType toolType, MaterialBase materialBase) {
      this.toolType = toolType;
      this.materialBase = materialBase;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
      return "ToolProps(" + toolType + "/" + materialBase + ")";
    }

    /**
     * Validate.
     */
    public void validate() {
      if (toolType == null) {
        throw new IllegalArgumentException("ToolType must not be null.");
      }
      if (materialBase == null) {
        throw new IllegalArgumentException("MaterialBase must not be null");
      }
    }
  }

  /**
   * Properties of a block.
   */
  public static class BlockProps {

    /**
     * The tool.
     */
    public final ToolProps tool;

    /**
     * The breaking times.
     */
    public final long[] breakingTimes;

    /**
     * The hardness.
     */
    public final float hardness;
    /**
     * Factor 2 = 2 times faster.
     */
    public final float efficiencyMod;

    /**
     * Instantiates a new block props.
     *
     * @param tool The tool type that allows access to breaking times other than MaterialBase.NONE.
     * @param hardness the hardness
     */
    public BlockProps(ToolProps tool, float hardness) {
      this(tool, hardness, 1);
    }

    /**
     * Instantiates a new block props.
     *
     * @param tool The tool type that allows access to breaking times other than MaterialBase.NONE.
     * @param hardness the hardness
     * @param efficiencyMod the efficiency mod
     */
    public BlockProps(ToolProps tool, float hardness, float efficiencyMod) {
      this.tool = tool;
      this.hardness = hardness;
      breakingTimes = new long[6];
      for (int i = 0; i < 6; i++) {
        final float multiplier;
        if (tool.materialBase == null) {
          multiplier = 1f;
        } else if (i < tool.materialBase.index) {
          multiplier = 1f;
        } else {
          multiplier = MaterialBase.getById(i).breakMultiplier * 3.33f;
        }
        breakingTimes[i] = (long) (1000f * 5f * hardness / multiplier);
      }
      this.efficiencyMod = efficiencyMod;
    }

    /**
     * Instantiates a new block props.
     *
     * @param tool The tool type that allows access to breaking times other than MaterialBase.NONE.
     * @param hardness the hardness
     * @param breakingTimes The breaking times (NONE, WOOD, STONE, IRON, DIAMOND, GOLD)
     */
    public BlockProps(ToolProps tool, float hardness, long[] breakingTimes) {
      this(tool, hardness, breakingTimes, 1f);
    }

    /**
     * Instantiates a new block props.
     *
     * @param tool The tool type that allows access to breaking times other than MaterialBase.NONE.
     * @param hardness the hardness
     * @param breakingTimes The breaking times (NONE, WOOD, STONE, IRON, DIAMOND, GOLD)
     * @param efficiencyMod the efficiency mod
     */
    public BlockProps(ToolProps tool, float hardness, long[] breakingTimes, float efficiencyMod) {
      this.tool = tool;
      this.breakingTimes = breakingTimes;
      this.hardness = hardness;
      this.efficiencyMod = efficiencyMod;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
      return "BlockProps(" + hardness + " / " + tool.toString() + " / " + Arrays
          .toString(breakingTimes) + ")";
    }

    /**
     * Validate.
     */
    public void validate() {
      if (breakingTimes == null) {
        throw new IllegalArgumentException("Breaking times must not be null.");
      }
      if (breakingTimes.length != 6) {
        throw new IllegalArgumentException(
            "Breaking times length must match the number of available tool types (6).");
      }
      if (tool == null) {
        throw new IllegalArgumentException("Tool must not be null.");
      }
      tool.validate();
    }
  }
}
