package net.avicus.atlas.module.shop;

import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;

/**
 * Configuration options about how many points should be earned for performing actions in matches.
 */
public class PointEarnConfig {

  /**
   * Global list of actions that can be configured. External modules should use this to add special
   * point actions.
   */
  public static final List<String> CONFIGURABLES = Lists.newArrayList();

  static {
    for (String s : new String[]{"player-kill", "join-match", "point-earn"}) {
      CONFIGURABLES.add(s);
    }
  }

  /**
   * Configuration options matching action ID keys to point values.
   */
  private final HashMap<String, Integer> config;

  /**
   * Constructor
   *
   * @param config Configuration options matching action ID keys to point values.
   */
  public PointEarnConfig(HashMap<String, Integer> config) {
    this.config = config;
  }

  /**
   * Get the amount of points for performing an action.
   * If the action is not configured, 0 will be returned.
   *
   * @param s ID of the action being performed.
   * @return the amount of points for performing an action.
   */
  public int getPoints(String s) {
    return this.config.getOrDefault(s, 0);
  }
}
