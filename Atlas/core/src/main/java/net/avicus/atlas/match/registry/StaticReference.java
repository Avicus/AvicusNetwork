package net.avicus.atlas.match.registry;

import java.util.Optional;
import javax.annotation.Nonnull;
import lombok.ToString;

/**
 * Refers to an object directly.
 */
@ToString
public class StaticReference<T> implements WeakReference<T> {

  private final T object;

  public StaticReference(@Nonnull T object) {
    this.object = object;
  }

  @Override
  public Optional<T> getObject() {
    return Optional.of(this.object);
  }
}
