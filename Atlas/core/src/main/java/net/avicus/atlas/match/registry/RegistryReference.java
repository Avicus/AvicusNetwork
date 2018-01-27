package net.avicus.atlas.match.registry;

import java.util.Optional;
import lombok.ToString;

/**
 * Refers to an object by its "id".
 */
@ToString(exclude = "registry")
public class RegistryReference<T> implements WeakReference<T> {

  private final MatchRegistry registry;
  private final Class<T> type;
  private final String id;

  private T result; // limits # of expensive calls to the registry

  public RegistryReference(MatchRegistry registry, Class<T> type, String id) {
    this.registry = registry;
    this.type = type;
    this.id = id;
  }

  public Optional<T> getObject() {
    if (this.result != null) {
      return Optional.of(this.result);
    }

    try {
      Optional<T> optional = this.registry.get(this.type, this.id, false);
      if (optional.isPresent()) {
        this.result = optional.get();
        return optional;
      } else {
        // warning, not found
        this.registry.getMatch().warn(new Exception("Reference to \"" + id + "\" was not found."));
        return Optional.empty();
      }
    } catch (Exception e) {
      // warning, wrong type found
      this.registry.getMatch().warn(e);
      return Optional.empty();
    }
  }
}
