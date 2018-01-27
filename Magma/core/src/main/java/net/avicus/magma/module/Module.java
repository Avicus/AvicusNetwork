package net.avicus.magma.module;

/**
 * A module.
 */
public interface Module {

  /**
   * Invoked when enabling the module.
   */
  default void enable() {
  }

  /**
   * Invoked when disabling the module.
   */
  default void disable() {
  }
}
