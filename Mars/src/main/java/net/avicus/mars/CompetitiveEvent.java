package net.avicus.mars;

import net.avicus.magma.database.model.impl.User;

public interface CompetitiveEvent {

  /**
   * Check if a certain user can join the server while this event is in progress.
   */
  boolean canJoinServer(User user);
}
