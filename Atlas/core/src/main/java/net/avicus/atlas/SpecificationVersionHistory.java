package net.avicus.atlas;

import net.avicus.magma.util.Version;

/**
 * A simple class to keep track of all specification updates.
 */
public class SpecificationVersionHistory {

  /**
   * Kits require IDs, KitCheck
   **/
  public static final Version KIT_IDS_REQUIRED = new Version(1, 0, 1);
  public static Version INITIAL_RELEASE = new Version(1, 0, 0);
  /**
   * Changes the default behaviour of destroyable repair checking to ignore all.
   **/
  public static Version REPAIR_CHECK_DEF_OFF = new Version(1, 0, 2);

  /**
   * Removes the ability to disable chat channels.
   **/
  public static Version UNTOUCHABLE_CHAT_CHANNELS = new Version(1, 0, 3);

  /**
   * Forces loadouts to use sub-tags for organization.
   **/
  public static Version LOADOUT_SUB_TAG = new Version(1, 0, 4);

  /**
   * Walls is no longer an objective.
   **/
  public static Version SEPARATE_WALLS = new Version(1, 0, 5);

  /**
   * Rectangles take a min and a max now.
   */
  public static Version NEW_RECTANGLES = new Version(1, 0, 6);

  /**
   * Current specification, all modules should reference this.
   **/
  public static Version CURRENT = NEW_RECTANGLES;
}
