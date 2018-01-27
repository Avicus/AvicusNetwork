package net.avicus.atlas.module.stats.action.base;

import java.time.Instant;

public interface Action {

  Instant getWhen();

  double getScore();

  String getDebugMessage();
}
