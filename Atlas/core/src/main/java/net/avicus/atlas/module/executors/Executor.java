package net.avicus.atlas.module.executors;

import lombok.ToString;
import net.avicus.atlas.match.registry.RegisterableObject;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;

/**
 * A wrapper object that contains a block of code that can be executed inside of a {@link
 * net.avicus.atlas.match.Match}
 */
@ToString
public abstract class Executor implements RegisterableObject<Executor> {

  private final String id;
  private final Check check;

  /**
   * Constructor
   *
   * @param id of the executor
   * @param check to be ran before execution
   */
  public Executor(String id, Check check) {
    this.id = id;
    this.check = check;
  }

  /**
   * Execute this executor if it's check passes.
   *
   * @param context that holds variables to check against
   */
  public void executeChecked(CheckContext context) {
    if (this.check.test(context).passes()) {
      this.execute(context);
    }
  }

  /**
   * Execute this executor.
   *
   * @param context that holds variables to check against
   */
  abstract public void execute(CheckContext context);

  @Override
  public String getId() {
    return id;
  }

  @Override
  public Executor getObject() {
    return this;
  }
}
