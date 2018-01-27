package net.avicus.atlas.module.checks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A check is an object that will dynamically return a {@link CheckResult} based on {@link
 * Variable}s from the supplied {@link CheckContext}.
 */
public interface Check {

  /**
   * Execute a batch of checks and getFirst a map of how many checks returned each results.
   *
   * @param list list of checks to be executed
   * @param context context to be checked against
   * @return a map of {@link CheckResult}s with the number of checks that returned this result
   */
  static Map<CheckResult, Integer> test(List<Check> list, CheckContext context) {
    Map<CheckResult, Integer> results = new HashMap<>(list.size());
    results.put(CheckResult.ALLOW, 0);
    results.put(CheckResult.DENY, 0);
    results.put(CheckResult.IGNORE, 0);

    for (Check check : list) {
      CheckResult result = check.test(context);
      results.put(result, results.get(result) + 1);
    }

    return results;
  }

  /**
   * Checks if the context contains data that will allow the check to pass.
   *
   * @param context context to be checked against
   * @return if the test passes
   */
  CheckResult test(CheckContext context);
}
