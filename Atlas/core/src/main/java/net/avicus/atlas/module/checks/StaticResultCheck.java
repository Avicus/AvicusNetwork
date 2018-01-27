package net.avicus.atlas.module.checks;

/**
 * A check that will always return the same value.
 */
public class StaticResultCheck implements Check {

  /**
   * Result to always return
   */
  private final CheckResult result;

  /**
   * Constructor.
   *
   * @param result result to always return
   */
  public StaticResultCheck(CheckResult result) {
    this.result = result;
  }

  @Override
  public CheckResult test(CheckContext context) {
    return this.result;
  }
}
