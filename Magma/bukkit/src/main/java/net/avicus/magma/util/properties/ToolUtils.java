package net.avicus.magma.util.properties;

import static net.avicus.magma.util.properties.BlockPropStore.dispenserType;
import static net.avicus.magma.util.properties.BlockPropStore.getBlockProps;
import static net.avicus.magma.util.properties.BlockPropStore.getToolProps;
import static net.avicus.magma.util.properties.BlockPropStore.ironDoorType;
import static net.avicus.magma.util.properties.BlockPropStore.noTool;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ToolUtils {

  public static boolean isValidTool(final int blockId, final Props.BlockProps blockProps,
      final Props.ToolProps toolProps, final int efficiency) {
    boolean isValidTool = blockProps.tool.toolType == toolProps.toolType;

    if (!isValidTool && efficiency > 0) {
      // Efficiency makes the tool.
      // (wood, sand, gravel, ice)
      if (blockId == Material.SNOW.getId()) {
        return toolProps.toolType == Props.ToolType.SPADE;
      }
      if (blockId == Material.WOOL.getId()) {
        return true;
      }
      if (blockId == Material.WOODEN_DOOR.getId()) {
        return true;
      }
      if (blockProps.hardness <= 2
          && (blockProps.tool.toolType == Props.ToolType.AXE
          || blockProps.tool.toolType == Props.ToolType.SPADE
          || (blockProps.hardness < 0.8 && (blockId != Material.NETHERRACK.getId()
          && blockId != Material.SNOW.getId() && blockId != Material.SNOW_BLOCK.getId()
          && blockId != Material.STONE_PLATE.getId())))) {
        // Also roughly.
        return true;
      }
    }
    return isValidTool;
  }

  /**
   * Convenience method.
   *
   * @param BlockType the block type
   * @param player the player
   * @return the breaking duration
   */
  public static long getBreakingDuration(final Material BlockType, final Player player) {
    return getBreakingDuration(BlockType.getId(), player);
  }

  /**
   * Convenience method.
   *
   * @param blockId the block id
   * @param player the player
   * @return the breaking duration
   */
  public static long getBreakingDuration(final int blockId, final Player player) {
    return getBreakingDuration(blockId,
        player.getItemInHand(),
        player);
  }

  /**
   * @param blockId the block id
   * @param itemInHand May be null.
   * @param player the player
   * @return the breaking duration
   */
  public static long getBreakingDuration(final int blockId, final ItemStack itemInHand,
      final Player player) {
    // Haste (faster digging).
    double haste = Double.NEGATIVE_INFINITY;
    for (PotionEffect effect : player.getActivePotionEffects()) {
      if (effect.getType().equals(PotionEffectType.FAST_DIGGING)) {
        haste = effect.getAmplifier();
      }
    }
    return getBreakingDuration(blockId, itemInHand, Double.isInfinite(haste) ? 0 : 1 + (int) haste);
  }

  /**
   * @param material the block type
   * @param itemInHand May be null.
   * @param player the player
   * @return the breaking duration
   */
  public static long getBreakingDuration(final Material material, final ItemStack itemInHand,
      final Player player) {
    return getBreakingDuration(material.getId(), itemInHand, player);
  }

  /**
   * Get the normal breaking duration, including enchantments, and tool
   * properties.
   *
   * @param blockId the block id
   * @param itemInHand the item in hand
   * @param haste the haste
   * @return the breaking duration
   */
  public static long getBreakingDuration(final int blockId, final ItemStack itemInHand,
      final int haste) {
    if (isAir(itemInHand)) {
      return getBreakingDuration(blockId, getBlockProps(blockId), noTool, 0);
    } else {
      int efficiency = 0;
      if (itemInHand.containsEnchantment(Enchantment.DIG_SPEED)) {
        efficiency = itemInHand.getEnchantmentLevel(Enchantment.DIG_SPEED);
      }
      return getBreakingDuration(blockId, getBlockProps(blockId),
          getToolProps(itemInHand.getTypeId()), efficiency, haste);
    }
  }

  public static final boolean isAir(Material type) {
    return type == null || type == Material.AIR;
  }

  public static final boolean isAir(ItemStack stack) {
    return stack == null || isAir(stack.getType());
  }


  /**
   * Gets the breaking duration.
   *
   * @param blockId the block id
   * @param blockProps the block props
   * @param toolProps the tool props
   * @param efficiency the efficiency
   * @param haste Amplifier of haste potion effect (assume > 0 for effect there at all, so 1 is
   * haste I, 2 is haste II).
   * @return the breaking duration
   */
  public static long getBreakingDuration(final int blockId, final Props.BlockProps blockProps,
      final Props.ToolProps toolProps, int efficiency, int haste) {
    final long dur = getBreakingDuration(blockId, blockProps, toolProps, efficiency);
    return haste > 0 ? (long) (Math.pow(0.8, haste) * dur) : dur;
  }

  public static long getBreakingDuration(final int blockId, final Props.BlockProps blockProps,
      final Props.ToolProps toolProps, int efficiency) {
    boolean isValidTool = ToolUtils.isValidTool(blockId, blockProps, toolProps, efficiency);

    long duration;

    if (isValidTool) {
      // appropriate tool
      duration = blockProps.breakingTimes[toolProps.materialBase.index];
      if (efficiency > 0) {
        duration = (long) (duration / blockProps.efficiencyMod);
      }
    } else {
      // Inappropriate tool.
      duration = blockProps.breakingTimes[0];
      // Swords are always appropriate.
      if (toolProps.toolType == Props.ToolType.SWORD) {
        duration = (long) ((float) duration / 1.5f);
      }
    }

    // Specialties:
    if (toolProps.toolType == Props.ToolType.SHEARS) {
      // (Note: shears are not in the block props, anywhere)
      // Treat these extra (partly experimental):
      if (blockId == Material.WEB.getId()) {
        duration = 400;
        isValidTool = true;
      } else if (blockId == Material.WOOL.getId()) {
        duration = 240;
        isValidTool = true;
      } else if (blockId == Material.VINE.getId()) {
        duration = 300;
        isValidTool = true;
      }
    }
    // (sword vs web already counted)
    else if (blockId == Material.VINE.getId() && toolProps.toolType == Props.ToolType.AXE) {
      isValidTool = true;
      if (toolProps.materialBase == Props.MaterialBase.WOOD
          || toolProps.materialBase == Props.MaterialBase.STONE) {
        duration = 100;
      } else {
        duration = 0;
      }
    }

    if (isValidTool || blockProps.tool.toolType == Props.ToolType.NONE) {
      float mult = 1f;
      duration = (long) (mult * duration);

      // Efficiency level.
      if (efficiency > 0) {
        // Workarounds ...
        if (blockId == Material.WOODEN_DOOR.getId() && toolProps.toolType != Props.ToolType.AXE) {
          // Heck [Cleanup pending]...
          switch (efficiency) {
            case 1:
              return (long) (mult * 1500);
            case 2:
              return (long) (mult * 750);
            case 3:
              return (long) (mult * 450);
            case 4:
              return (long) (mult * 250);
            case 5:
              return (long) (mult * 150);
          }
        }
        // This seems roughly correct.
        for (int i = 0; i < efficiency; i++) {
          duration /= 1.33; // Matches well with obsidian.
        }
        // Formula from MC wiki.
        // TODO: Formula from mc wiki does not match well (too fast for obsidian).
        //				duration /= (1.0 + 0.5 * efficiency);

        // More Workarounds:
        // TODO: Consider checking a generic workaround (based on duration, assuming some dig packets lost, proportional to duration etc.).
        if (toolProps.materialBase == Props.MaterialBase.WOOD) {
          if (toolProps.toolType == Props.ToolType.PICKAXE && (blockProps == ironDoorType
              || blockProps == dispenserType)) {
            // Special correction.
            // TODO: Uncomfortable: hide this in the blocks by some flags / other type of workarounds !
            if (blockProps == dispenserType) {
              duration = (long) (duration / 1.5 - (efficiency - 1) * 60);
            } else if (blockProps == ironDoorType) {
              duration = (long) (duration / 1.5 - (efficiency - 1) * 100);
            }
          } else if (blockId == Material.LOG.getId()) {
            duration -= efficiency >= 4 ? 250 : 400;
          } else if (blockProps.tool.toolType == toolProps.toolType) {
            duration -= 250;
          } else {
            duration -= efficiency * 30;
          }

        } else if (toolProps.materialBase == Props.MaterialBase.STONE) {
          if (blockId == Material.LOG.getId()) {
            duration -= 100;
          }
        }
      }
    }
    // Post/legacy workarounds for efficiency tools ("improper").
    if (efficiency > 0 && !isValidTool) {
      if (!isValidTool && blockId == Material.MELON_BLOCK.getId()) {
        // Fall back to pre-1.8 behavior.
        // 450, 200 , 100 , 50 , 0
        duration = Math.min(duration, 450 / (long) Math.pow(2, efficiency - 1));
      }
    }
    return Math.max(0, duration);
  }
}
