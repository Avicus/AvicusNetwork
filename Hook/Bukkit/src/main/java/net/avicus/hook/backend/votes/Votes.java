package net.avicus.hook.backend.votes;

import net.avicus.hook.utils.Events;

public class Votes {

  public static void init() {
    Events.register(new VoteListener());
  }
}
