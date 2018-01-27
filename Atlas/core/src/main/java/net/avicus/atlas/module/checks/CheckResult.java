package net.avicus.atlas.module.checks;

/**
 * The possible results that a check can return.
 */
public enum CheckResult {
  /**
   * If the check passes
   */
  ALLOW,
  /**
   * If the check does not match
   */
  DENY,
  /**
   * The check has neither passed nor failed. (ie. it is not relevant to the situation)
   */
  IGNORE;

  /**
   * Get a check result based on a supplied boolean.
   *
   * @param val boolean to convert
   * @return value based on boolean
   */
  public static CheckResult valueOf(boolean val) {
    return val ? ALLOW : DENY;
  }

  /**
   * If the check passes.
   * Will also return true if the result is ignored.
   *
   * @return if the check passes, or is ignored
   */
  public boolean passes() {
    return this != DENY;
  }

  /**
   * If the check fails.
   *
   * @return if the check fails
   */
  public boolean fails() {
    return this == DENY;
  }
}
