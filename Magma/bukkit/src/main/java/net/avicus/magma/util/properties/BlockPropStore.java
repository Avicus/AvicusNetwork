package net.avicus.magma.util.properties;

import static net.avicus.compendium.Time.secToMs;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * This is from NCP
 */
public class BlockPropStore {

  /**
   * Breaking time for indestructible materials.
   */
  public static final long indestructible = Long.MAX_VALUE;
  /**
   * Default tool properties (inappropriate tool).
   */
  public static final Props.ToolProps noTool = new Props.ToolProps(Props.ToolType.NONE,
      Props.MaterialBase.NONE);
  /**
   * The Constant woodSword.
   */
  public static final Props.ToolProps woodSword = new Props.ToolProps(Props.ToolType.SWORD,
      Props.MaterialBase.WOOD);
  /**
   * The Constant woodSpade.
   */
  public static final Props.ToolProps woodSpade = new Props.ToolProps(Props.ToolType.SPADE,
      Props.MaterialBase.WOOD);
  /**
   * The Constant woodPickaxe.
   */
  public static final Props.ToolProps woodPickaxe = new Props.ToolProps(Props.ToolType.PICKAXE,
      Props.MaterialBase.WOOD);
  /**
   * The Constant woodAxe.
   */
  public static final Props.ToolProps woodAxe = new Props.ToolProps(Props.ToolType.AXE,
      Props.MaterialBase.WOOD);
  /**
   * The Constant stonePickaxe.
   */
  public static final Props.ToolProps stonePickaxe = new Props.ToolProps(Props.ToolType.PICKAXE,
      Props.MaterialBase.STONE);
  /**
   * The Constant ironPickaxe.
   */
  public static final Props.ToolProps ironPickaxe = new Props.ToolProps(Props.ToolType.PICKAXE,
      Props.MaterialBase.IRON);
  /**
   * The Constant diamondPickaxe.
   */
  public static final Props.ToolProps diamondPickaxe = new Props.ToolProps(Props.ToolType.PICKAXE,
      Props.MaterialBase.DIAMOND);
  /**
   * Times for instant breaking.
   */
  public static final long[] instantTimes = secToMs(0);
  /**
   * The Constant leafTimes.
   */
  public static final long[] leafTimes = secToMs(0.3);
  /**
   * The Constant gravelTimes.
   */
  public static final long[] gravelTimes = secToMs(0.9, 0.45, 0.25, 0.15, 0.15, 0.1);
  /**
   * The Constant woodTimes.
   */
  public static final long[] woodTimes = secToMs(3, 1.5, 0.75, 0.5, 0.4, 0.25);
  /**
   * Instantly breakable.
   */
  public static final Props.BlockProps instantType = new Props.BlockProps(noTool, 0, instantTimes);
  /**
   * The Constant gravelType.
   */
  public static final Props.BlockProps gravelType = new Props.BlockProps(woodSpade, 0.6f,
      gravelTimes);
  /**
   * Stone type blocks.
   */
  public static final Props.BlockProps stoneType = new Props.BlockProps(woodPickaxe, 1.5f);
  /**
   * The Constant woodType.
   */
  public static final Props.BlockProps woodType = new Props.BlockProps(woodAxe, 2, woodTimes);
  /**
   * The Constant brickType.
   */
  public static final Props.BlockProps brickType = new Props.BlockProps(woodPickaxe, 2);
  /**
   * The Constant coalType.
   */
  public static final Props.BlockProps coalType = new Props.BlockProps(woodPickaxe, 3);
  /**
   * The Constant goldBlockType.
   */
  public static final Props.BlockProps goldBlockType = new Props.BlockProps(woodPickaxe, 3,
      secToMs(15, 7.5, 3.75, 0.7, 0.55, 1.2));
  /**
   * The Constant ironBlockType.
   */
  public static final Props.BlockProps ironBlockType = new Props.BlockProps(woodPickaxe, 5,
      secToMs(25, 12.5, 2.0, 1.25, 0.95, 2.0));
  /**
   * The Constant diamondBlockType.
   */
  public static final Props.BlockProps diamondBlockType = new Props.BlockProps(woodPickaxe, 5,
      secToMs(25, 12.5, 6.0, 1.25, 0.95, 2.0));
  /**
   * The Constant hugeMushroomType.
   */
  public static final Props.BlockProps hugeMushroomType = new Props.BlockProps(woodAxe, 0.2f,
      secToMs(0.3, 0.15, 0.1, 0.05, 0.05, 0.05));
  /**
   * The Constant leafType.
   */
  public static final Props.BlockProps leafType = new Props.BlockProps(noTool, 0.2f, leafTimes);
  /**
   * The Constant sandType.
   */
  public static final Props.BlockProps sandType = new Props.BlockProps(woodSpade, 0.5f,
      secToMs(0.75, 0.4, 0.2, 0.15, 0.1, 0.1));
  /**
   * The Constant leverType.
   */
  public static final Props.BlockProps leverType = new Props.BlockProps(noTool, 0.5f,
      secToMs(0.75));
  /**
   * The Constant sandStoneType.
   */
  public static final Props.BlockProps sandStoneType = new Props.BlockProps(woodPickaxe, 0.8f);
  /**
   * The Constant chestType.
   */
  public static final Props.BlockProps chestType = new Props.BlockProps(woodAxe, 2.5f,
      secToMs(3.75, 1.9, 0.95, 0.65, 0.5, 0.35));
  /**
   * The Constant woodDoorType.
   */
  public static final Props.BlockProps woodDoorType = new Props.BlockProps(woodAxe, 3.0f,
      secToMs(4.5, 2.25, 1.15, 0.75, 0.6, 0.4));
  /**
   * The Constant dispenserType.
   */
  public static final Props.BlockProps dispenserType = new Props.BlockProps(woodPickaxe, 3.5f);
  /**
   * The Constant ironDoorType.
   */
  public static final Props.BlockProps ironDoorType = new Props.BlockProps(woodPickaxe, 5);
  /**
   * The Constant maxBlocks.
   */
  protected static final int maxBlocks = 4096;
  /**
   * Properties by block id, might be extended to 4096 later for custom blocks.
   */
  protected static final Props.BlockProps[] blocks = new Props.BlockProps[maxBlocks];
  /**
   * The Constant instantMat.
   */
  protected static final Material[] instantMat = new Material[]{
      // Named in wiki.
      Material.CROPS,
      Material.TRIPWIRE_HOOK, Material.TRIPWIRE,
      Material.TORCH,
      Material.TNT,
      Material.SUGAR_CANE_BLOCK,
      Material.SAPLING,
      Material.RED_ROSE, Material.YELLOW_FLOWER,
      Material.REDSTONE_WIRE,
      Material.REDSTONE_TORCH_ON, Material.REDSTONE_TORCH_OFF,
      Material.DIODE_BLOCK_ON, Material.DIODE_BLOCK_OFF,
      Material.PUMPKIN_STEM,
      Material.NETHER_WARTS,
      Material.BROWN_MUSHROOM, Material.RED_MUSHROOM,
      Material.MELON_STEM,
      Material.WATER_LILY,
      Material.LONG_GRASS,
      Material.FIRE,
      Material.DEAD_BUSH,
      //
      Material.CROPS,

      // 1.4
      Material.COMMAND,
      Material.FLOWER_POT,
      Material.CARROT,
      Material.POTATO,
  };
  /**
   * The Constant indestructibleTimes.
   */
  private static final long[] indestructibleTimes = new long[]{indestructible, indestructible,
      indestructible, indestructible, indestructible, indestructible};
  /**
   * The Constant indestructibleType.
   */
  public static final Props.BlockProps indestructibleType = new Props.BlockProps(noTool, -1f,
      indestructibleTimes);
  /**
   * The glass times.
   */
  public static long[] glassTimes = secToMs(0.45);
  /**
   * The Constant glassType.
   */
  public static final Props.BlockProps glassType = new Props.BlockProps(noTool, 0.3f, glassTimes,
      2f);
  /**
   * The rails times.
   */
  public static long[] railsTimes = secToMs(1.05, 0.55, 0.3, 0.2, 0.15, 0.1);
  /**
   * Map for the tool properties.
   */
  protected static Map<Integer, Props.ToolProps> tools = new LinkedHashMap<Integer, Props.ToolProps>(
      50, 0.5f);
  /**
   * Penalty factor for block break duration if under water.
   */
  protected static float breakPenaltyInWater = 4f;
  /**
   * Penalty factor for block break duration if not on ground.
   */
  protected static float breakPenaltyOffGround = 4f;
  /**
   * Returned if unknown.
   */
  private static Props.BlockProps defaultBlockProps = instantType;

  /**
   * Gets the tool props.
   *
   * @param stack the stack
   * @return the tool props
   */
  public static Props.ToolProps getToolProps(final ItemStack stack) {
    if (stack == null) {
      return noTool;
    } else {
      return getToolProps(stack.getTypeId());
    }
  }

  /**
   * Gets the tool props.
   *
   * @param mat the mat
   * @return the tool props
   */
  public static Props.ToolProps getToolProps(final Material mat) {
    if (mat == null) {
      return noTool;
    } else {
      return getToolProps(mat.getId());
    }
  }

  /**
   * Gets the tool props.
   *
   * @param id the id
   * @return the tool props
   */
  public static Props.ToolProps getToolProps(final Integer id) {
    final Props.ToolProps props = tools.get(id);
    if (props == null) {
      return noTool;
    } else {
      return props;
    }
  }

  /**
   * Gets the block props.
   *
   * @param stack the stack
   * @return the block props
   */
  public static Props.BlockProps getBlockProps(final ItemStack stack) {
    if (stack == null) {
      return defaultBlockProps;
    } else {
      return getBlockProps(stack.getType());
    }
  }

  /**
   * Gets the block props.
   *
   * @param mat the mat
   * @return the block props
   */
  public static Props.BlockProps getBlockProps(final Material mat) {
    if (mat == null) {
      return defaultBlockProps;
    } else {
      return getBlockProps(mat.getId());
    }
  }

  /**
   * Gets the block props.
   *
   * @param blockId the block id
   * @return the block props
   */
  public static Props.BlockProps getBlockProps(final int blockId) {
    if (blockId < 0 || blockId >= blocks.length || blocks[blockId] == null) {
      return defaultBlockProps;
    } else {
      return blocks[blockId];
    }
  }

  /**
   * Inits the tools.
   */
  public static void initTools() {
    tools.clear();
    tools.put(268, new Props.ToolProps(Props.ToolType.SWORD, Props.MaterialBase.WOOD));
    tools.put(269, new Props.ToolProps(Props.ToolType.SPADE, Props.MaterialBase.WOOD));
    tools.put(270, new Props.ToolProps(Props.ToolType.PICKAXE, Props.MaterialBase.WOOD));
    tools.put(271, new Props.ToolProps(Props.ToolType.AXE, Props.MaterialBase.WOOD));

    tools.put(272, new Props.ToolProps(Props.ToolType.SWORD, Props.MaterialBase.STONE));
    tools.put(273, new Props.ToolProps(Props.ToolType.SPADE, Props.MaterialBase.STONE));
    tools.put(274, new Props.ToolProps(Props.ToolType.PICKAXE, Props.MaterialBase.STONE));
    tools.put(275, new Props.ToolProps(Props.ToolType.AXE, Props.MaterialBase.STONE));

    tools.put(256, new Props.ToolProps(Props.ToolType.SPADE, Props.MaterialBase.IRON));
    tools.put(257, new Props.ToolProps(Props.ToolType.PICKAXE, Props.MaterialBase.IRON));
    tools.put(258, new Props.ToolProps(Props.ToolType.AXE, Props.MaterialBase.IRON));
    tools.put(267, new Props.ToolProps(Props.ToolType.SWORD, Props.MaterialBase.IRON));

    tools.put(276, new Props.ToolProps(Props.ToolType.SWORD, Props.MaterialBase.DIAMOND));
    tools.put(277, new Props.ToolProps(Props.ToolType.SPADE, Props.MaterialBase.DIAMOND));
    tools.put(278, new Props.ToolProps(Props.ToolType.PICKAXE, Props.MaterialBase.DIAMOND));
    tools.put(279, new Props.ToolProps(Props.ToolType.AXE, Props.MaterialBase.DIAMOND));

    tools.put(283, new Props.ToolProps(Props.ToolType.SWORD, Props.MaterialBase.GOLD));
    tools.put(284, new Props.ToolProps(Props.ToolType.SPADE, Props.MaterialBase.GOLD));
    tools.put(285, new Props.ToolProps(Props.ToolType.PICKAXE, Props.MaterialBase.GOLD));
    tools.put(286, new Props.ToolProps(Props.ToolType.AXE, Props.MaterialBase.GOLD));

    tools.put(359, new Props.ToolProps(Props.ToolType.SHEARS, Props.MaterialBase.NONE));
  }

  /**
   * Inits the blocks.
   */
  public static void initBlocks() {
    // Reset tool props.
    Arrays.fill(blocks, null);

    // Set block break properties.
    // Instantly breakable.
    for (final Material mat : instantMat) {
      blocks[mat.getId()] = instantType;
    }
    // TODO: Bed is special.
    // Leaf type
    for (Material mat : new Material[]{
        Material.LEAVES, Material.BED_BLOCK}) {
      blocks[mat.getId()] = leafType;
    }
    // Huge mushroom type (...)
    for (Material mat : new Material[]{
        Material.HUGE_MUSHROOM_1, Material.HUGE_MUSHROOM_2,
        Material.VINE, Material.COCOA}) {
      blocks[mat.getId()] = hugeMushroomType;
    }

    blocks[Material.SNOW.getId()] = new Props.BlockProps(getToolProps(Material.WOOD_SPADE), 0.1f,
        secToMs(0.5, 0.1, 0.05, 0.05, 0.05, 0.05));
    blocks[Material.SNOW_BLOCK.getId()] = new Props.BlockProps(getToolProps(Material.WOOD_SPADE),
        0.1f, secToMs(1, 0.15, 0.1, 0.05, 0.05, 0.05));
    for (Material mat : new Material[]{
        Material.REDSTONE_LAMP_ON, Material.REDSTONE_LAMP_OFF,
        Material.GLOWSTONE, Material.GLASS,
    }) {
      blocks[mat.getId()] = glassType;
    }
    blocks[Material.THIN_GLASS.getId()] = glassType;
    blocks[Material.NETHERRACK.getId()] = new Props.BlockProps(woodPickaxe, 0.4f,
        secToMs(2, 0.3, 0.15, 0.1, 0.1, 0.05));
    blocks[Material.LADDER.getId()] = new Props.BlockProps(noTool, 0.4f, secToMs(0.6), 2.5f);
    blocks[Material.CACTUS.getId()] = new Props.BlockProps(noTool, 0.4f, secToMs(0.6));
    blocks[Material.WOOD_PLATE.getId()] = new Props.BlockProps(woodAxe, 0.5f,
        secToMs(0.75, 0.4, 0.2, 0.15, 0.1, 0.1));
    blocks[Material.STONE_PLATE.getId()] = new Props.BlockProps(woodPickaxe, 0.5f,
        secToMs(2.5, 0.4, 0.2, 0.15, 0.1, 0.07));
    blocks[Material.SAND.getId()] = sandType;
    blocks[Material.SOUL_SAND.getId()] = sandType;
    for (Material mat : new Material[]{Material.LEVER, Material.PISTON_BASE,
        Material.PISTON_EXTENSION, Material.PISTON_STICKY_BASE,
        Material.STONE_BUTTON, Material.PISTON_MOVING_PIECE}) {
      blocks[mat.getId()] = leverType;
    }
    //		blocks[Material.ICE.getId()] = new BlockProps(woodPickaxe, 0.5f, secToMs(2.5, 0.4, 0.2, 0.15, 0.1, 0.1));
    blocks[Material.ICE.getId()] = new Props.BlockProps(woodPickaxe, 0.5f,
        secToMs(0.7, 0.35, 0.18, 0.12, 0.09, 0.06));
    blocks[Material.DIRT.getId()] = sandType;
    blocks[Material.CAKE_BLOCK.getId()] = leverType;
    blocks[Material.BREWING_STAND.getId()] = new Props.BlockProps(woodPickaxe, 0.5f,
        secToMs(2.5, 0.4, 0.2, 0.15, 0.1, 0.1));
    blocks[Material.SPONGE.getId()] = new Props.BlockProps(noTool, 0.6f, secToMs(0.9));
    for (Material mat : new Material[]{
        Material.MYCEL, Material.GRAVEL, Material.GRASS, Material.SOIL,
        Material.CLAY,
    }) {
      blocks[mat.getId()] = gravelType;
    }
    for (Material mat : new Material[]{
        Material.RAILS, Material.POWERED_RAIL, Material.DETECTOR_RAIL,
    }) {
      blocks[mat.getId()] = new Props.BlockProps(woodPickaxe, 0.7f, railsTimes);
    }
    blocks[Material.MONSTER_EGGS.getId()] = new Props.BlockProps(noTool, 0.75f, secToMs(1.15));
    blocks[Material.WOOL.getId()] = new Props.BlockProps(noTool, 0.8f, secToMs(1.2), 3f);
    blocks[Material.SANDSTONE.getId()] = sandStoneType;
    blocks[Material.SANDSTONE_STAIRS.getId()] = sandStoneType;
    for (Material mat : new Material[]{
        Material.STONE, Material.SMOOTH_BRICK, Material.SMOOTH_STAIRS,
    }) {
      blocks[mat.getId()] = stoneType;
    }
    blocks[Material.NOTE_BLOCK.getId()] = new Props.BlockProps(woodAxe, 0.8f,
        secToMs(1.2, 0.6, 0.3, 0.2, 0.15, 0.1));
    final Props.BlockProps pumpkinType = new Props.BlockProps(woodAxe, 1,
        secToMs(1.5, 0.75, 0.4, 0.25, 0.2, 0.15));
    blocks[Material.WALL_SIGN.getId()] = pumpkinType;
    blocks[Material.SIGN_POST.getId()] = pumpkinType;
    blocks[Material.PUMPKIN.getId()] = pumpkinType;
    blocks[Material.JACK_O_LANTERN.getId()] = pumpkinType;
    blocks[Material.MELON_BLOCK.getId()] = new Props.BlockProps(noTool, 1, secToMs(1.45), 3);
    blocks[Material.BOOKSHELF.getId()] = new Props.BlockProps(woodAxe, 1.5f,
        secToMs(2.25, 1.15, 0.6, 0.4, 0.3, 0.2));
    for (Material mat : new Material[]{
        Material.WOOD_STAIRS, Material.WOOD, Material.WOOD_STEP, Material.LOG,
        Material.FENCE, Material.FENCE_GATE, Material.JUKEBOX,
        Material.JUNGLE_WOOD_STAIRS, Material.SPRUCE_WOOD_STAIRS,
        Material.BIRCH_WOOD_STAIRS,
        Material.WOOD_DOUBLE_STEP, // ?
        // double slabs ?
    }) {
      blocks[mat.getId()] = woodType;
    }
    for (Material mat : new Material[]{
        Material.COBBLESTONE_STAIRS, Material.COBBLESTONE,
        Material.NETHER_BRICK, Material.NETHER_BRICK_STAIRS, Material.NETHER_FENCE,
        Material.CAULDRON, Material.BRICK, Material.BRICK_STAIRS,
        Material.MOSSY_COBBLESTONE, Material.BRICK, Material.BRICK_STAIRS,
        Material.STEP, Material.DOUBLE_STEP, // ?

    }) {
      blocks[mat.getId()] = brickType;
    }
    blocks[Material.WORKBENCH.getId()] = chestType;
    blocks[Material.CHEST.getId()] = chestType;
    blocks[Material.WOODEN_DOOR.getId()] = woodDoorType;
    blocks[Material.TRAP_DOOR.getId()] = woodDoorType;
    for (Material mat : new Material[]{
        Material.ENDER_STONE, Material.COAL_ORE,

    }) {
      blocks[mat.getId()] = coalType;
    }
    blocks[Material.DRAGON_EGG.getId()] = new Props.BlockProps(noTool, 3f,
        secToMs(4.5)); // Former: coalType.
    final long[] ironTimes = secToMs(15, 15, 1.15, 0.75, 0.6, 15);
    final Props.BlockProps ironType = new Props.BlockProps(stonePickaxe, 3, ironTimes);
    for (Material mat : new Material[]{
        Material.LAPIS_ORE, Material.LAPIS_BLOCK, Material.IRON_ORE,
    }) {
      blocks[mat.getId()] = ironType;
    }
    final long[] diamondTimes = secToMs(15, 15, 15, 0.75, 0.6, 15);
    final Props.BlockProps diamondType = new Props.BlockProps(ironPickaxe, 3, diamondTimes);
    for (Material mat : new Material[]{
        Material.REDSTONE_ORE, Material.GLOWING_REDSTONE_ORE,
        Material.EMERALD_ORE, Material.GOLD_ORE, Material.DIAMOND_ORE,
    }) {
      blocks[mat.getId()] = diamondType;
    }
    blocks[Material.GOLD_BLOCK.getId()] = goldBlockType;
    blocks[Material.FURNACE.getId()] = dispenserType;
    blocks[Material.BURNING_FURNACE.getId()] = dispenserType;
    blocks[Material.DISPENSER.getId()] = dispenserType;
    blocks[Material.WEB.getId()] = new Props.BlockProps(woodSword, 4,
        secToMs(20, 0.4, 0.4, 0.4, 0.4, 0.4));

    for (Material mat : new Material[]{
        Material.MOB_SPAWNER, Material.IRON_DOOR_BLOCK,
        Material.IRON_FENCE, Material.ENCHANTMENT_TABLE,
        Material.EMERALD_BLOCK,
    }) {
      blocks[mat.getId()] = ironDoorType;
    }
    blocks[Material.IRON_BLOCK.getId()] = ironBlockType;
    blocks[Material.DIAMOND_BLOCK.getId()] = diamondBlockType;
    blocks[Material.ENDER_CHEST.getId()] = new Props.BlockProps(woodPickaxe, 22.5f);
    blocks[Material.OBSIDIAN.getId()] = new Props.BlockProps(diamondPickaxe, 50,
        secToMs(250, 125, 62.5, 41.6, 9.4, 20.8));

    // More 1.4 (not insta).
    // TODO: Either move all to an extra setup class, or integrate above.
    blocks[Material.BEACON.getId()] = new Props.BlockProps(noTool, 25f, secToMs(4.45)); // TODO
    blocks[Material.COBBLE_WALL.getId()] = brickType;
    blocks[Material.WOOD_BUTTON.getId()] = leverType;
    blocks[Material.SKULL.getId()] = new Props.BlockProps(noTool, 8.5f, secToMs(1.45)); // TODO
    blocks[Material.ANVIL.getId()] = new Props.BlockProps(woodPickaxe, 5f); // TODO

    // Indestructible.
    for (Material mat : new Material[]{
        Material.AIR, Material.ENDER_PORTAL, Material.ENDER_PORTAL_FRAME,
        Material.PORTAL, Material.LAVA, Material.WATER, Material.BEDROCK,
        Material.STATIONARY_LAVA, Material.STATIONARY_WATER,
    }) {
      blocks[mat.getId()] = indestructibleType;
    }
    blocks[95] = indestructibleType; // Locked chest (prevent crash with 1.7).
  }
}
