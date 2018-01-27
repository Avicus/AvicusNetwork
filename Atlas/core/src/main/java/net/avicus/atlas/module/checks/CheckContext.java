package net.avicus.atlas.module.checks;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.match.Match;

/**
 * Container for multiple {@link Variable}s used for checks
 * <p>
 * Multiple variables of the same type are not allowed.
 */
@ToString(exclude = "match")
public class CheckContext {

  /**
   * Match that these variables exist in.
   */
  @Getter
  private final Match match;
  /**
   * All of the variables inside of this context
   */
  @Getter
  private final List<Variable> variables;

  /**
   * Constructor.
   *
   * @param match match that these variables exist in.
   */
  public CheckContext(Match match) {
    this.match = match;
    this.variables = new ArrayList<>();
  }

  /**
   * Remove a variable from the context
   *
   * @param variable to add
   */
  public void remove(Class<? extends Variable> variable) {
    this.variables.removeAll(
        this.variables.stream().filter(v -> v.getClass() == variable).collect(Collectors.toList()));
  }

  /**
   * Add a variable to the context
   *
   * @param variable to add
   */
  public void add(Variable variable) {
    this.variables.add(variable);
  }

  /**
   * Add a variable to the context while removing all others of the same type.
   *
   * @param variable to add
   */
  public void addDestructively(Variable variable) {
    remove(variable.getClass());
    this.variables.add(variable);
  }

  /**
   * Get a variable from the context.
   *
   * @param variableType class type of the variable
   * @param <T> type of variable
   * @return an optional of the variable (if it exists)
   */
  @SuppressWarnings("unchecked")
  public <T extends Variable> Optional<T> getFirst(Class<T> variableType) {
    for (Variable test : this.variables) {
      if (test.getClass() == variableType) {
        return Optional.of((T) test);
      }
    }
    return Optional.empty();
  }

  /**
   * Get a variable from the context.
   *
   * @param variableType class type of the variable
   * @param <T> type of variable
   * @return an optional of the variable (if it exists)
   */
  @SuppressWarnings("unchecked")
  public <T extends Variable> Optional<T> getLast(Class<T> variableType) {
    for (Variable test : Lists.reverse(this.variables)) {
      if (test.getClass() == variableType) {
        return Optional.of((T) test);
      }
    }
    return Optional.empty();
  }

  /**
   * Get all variables of type from the context.
   *
   * @param variableType class type of the variable
   * @param <T> type of variable
   * @return an optional of the variable (if it exists)
   */
  @SuppressWarnings("unchecked")
  public <T extends Variable> List<T> getAll(Class<T> variableType) {
    List<T> result = new ArrayList<T>();
    for (Variable test : this.variables) {
      if (test.getClass() == variableType) {
        result.add((T) test);
      }
    }
    return result;
  }

  public CheckContext duplicate() {
    CheckContext clone = new CheckContext(this.match);
    this.variables.forEach(clone::add);
    return clone;
  }
}
