package net.avicus.atlas.module.checks.types;

import lombok.ToString;
import net.avicus.atlas.match.registry.WeakReference;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.CheckResult;
import net.avicus.atlas.module.states.State;
import net.avicus.atlas.module.states.StatesModule;

/**
 * A state check checks the current match state.
 */
@ToString
public class StateCheck implements Check {

  private final WeakReference<State> state;

  public StateCheck(WeakReference<State> state) {
    this.state = state;
  }

  @Override
  public CheckResult test(CheckContext context) {
    if (!this.state.isPresent()) {
      return CheckResult.IGNORE;
    }

    State currentState = context.getMatch().getRequiredModule(StatesModule.class).getState();
    return CheckResult.valueOf(currentState.equals(this.state.getObject().get()));
  }
}
