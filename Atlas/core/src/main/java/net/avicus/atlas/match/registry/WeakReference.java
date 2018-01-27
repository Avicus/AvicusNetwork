package net.avicus.atlas.match.registry;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Usually holds an object, but it may not exist in some cases, hence the use of Optional.
 */
public interface WeakReference<T> {

  Optional<T> getObject();

  default boolean isPresent() {
    return this.getObject().isPresent();
  }

  /**
   * If a value is present, invoke the specified consumer with the value,
   * otherwise do nothing.
   *
   * @param consumer block to be executed if a value is present
   * @throws NullPointerException if value is present and {@code consumer} is null
   */
  default void ifPresent(Consumer<? super T> consumer) {
    if (isPresent()) {
      consumer.accept(getObject().get());
    }
  }
}
