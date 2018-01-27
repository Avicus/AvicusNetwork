package net.avicus.atlas.match.registry;

public interface RegisterableObject<T> {

  String getId();

  T getObject();
}
