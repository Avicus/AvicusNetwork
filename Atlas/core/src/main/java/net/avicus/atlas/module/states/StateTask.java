package net.avicus.atlas.module.states;

import net.avicus.atlas.event.match.MatchTickEvent;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.util.AtlasTask;
import net.avicus.atlas.util.Events;

public class StateTask extends AtlasTask {

  private final Match match;
  private final StatesModule manager;

  public StateTask(Match match, StatesModule manager) {
    super();
    this.match = match;
    this.manager = manager;
  }

  public void start() {
    repeat(0, 20);
  }

  @Override
  public void run() {
    MatchTickEvent tick = new MatchTickEvent(this.match, this.manager.getState());
    Events.call(tick);
  }
}
