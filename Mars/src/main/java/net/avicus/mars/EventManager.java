package net.avicus.mars;

import java.util.Optional;

public interface EventManager<T extends CompetitiveEvent> {

  Optional<T> getCurrentEvent();

  void start();
}
