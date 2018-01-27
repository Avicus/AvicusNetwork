package net.avicus.atlas.match.registry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.Getter;
import net.avicus.atlas.match.Match;

/**
 * Registers an ID to elements of the map.
 */
public class MatchRegistry {

  @Getter
  private final Match match;
  private final Map<String, RegisterableObject> objects;

  public MatchRegistry(Match match) {
    this.match = match;
    this.objects = new HashMap<>();
  }

  public void add(RegisterableObject component) throws MatchRegistryException {
    Optional<Object> existing = get(Object.class, component.getId(), false);
    if (existing.isPresent()) {
      throw new MatchRegistryException("Tried to register id \"" + component.getId() + "\" twice.");
    }
    this.objects.put(component.getId(), component);
  }

  public void add(List<? extends RegisterableObject> objects) throws MatchRegistryException {
    objects.forEach(this::add);
  }

  @SuppressWarnings("unchecked")
  public <T> Optional<T> get(Class<T> type, String id, boolean required)
      throws MatchRegistryException {
    @Nullable RegisterableObject found = this.objects.get(id);

    if (found == null) {
      if (required) {
        throw new MatchRegistryException(
            "Unable to find required " + type.getSimpleName() + " for id \"" + id + "\".");
      }
      return Optional.empty();
    }

    // check the type is correct (found.getObject() instanceof type)
    if (type.isAssignableFrom(found.getObject().getClass())) {
      return Optional.of((T) found.getObject());
    }

    throw new MatchRegistryException(
        "Match registry mismatch for id \"" + id + "\". Found " + found.getObject().getClass()
            .getSimpleName() + " but expected " + type.getSimpleName() + ".");
  }

  public <T> WeakReference<T> getReference(Class<T> type, String id) {
    Optional<T> optional = get(type, id, false);

    if (optional.isPresent()) {
      return new StaticReference<>(optional.get());
    }

    return new RegistryReference<>(this, type, id);
  }
}
