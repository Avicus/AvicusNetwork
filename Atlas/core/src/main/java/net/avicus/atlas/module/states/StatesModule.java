package net.avicus.atlas.module.states;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.event.match.MatchCloseEvent;
import net.avicus.atlas.event.match.MatchOpenEvent;
import net.avicus.atlas.event.match.MatchStateChangeEvent;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.Module;
import net.avicus.atlas.util.Events;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.joda.time.Duration;
import org.joda.time.Instant;

@ToString(exclude = "match")
public class StatesModule implements Module {

  private final Match match;
  @Getter
  private final List<State> states;
  @Getter
  private int current;
  private StateTask task;

  public StatesModule(Match match, List<State> states) {
    this.match = match;
    this.states = states;
    this.current = 0;
    this.task = new StateTask(match, this);
  }

  public boolean isStarting() {
    if (!getNextState().isPresent()) {
      return false;
    }
    State curr = getState();
    State next = getNextState().get();
    return !curr.isPlaying() && next.isPlaying();
  }

  public boolean isPlaying() {
    return getState().isPlaying();
  }

  public boolean isCycling() {
    return !getNextState().isPresent();
  }

  public Duration getTotalDuration() {
    Duration duration = new Duration(0);
    for (State state : this.states) {
      duration = duration.plus(state.getDuration().orElse(Duration.ZERO));
    }
    return duration;
  }

  public Duration getTotalPlayingDuration() {
    Duration duration = new Duration(0);
    for (State state : this.states) {
      if (state.isPlaying()) {
        duration = duration.plus(state.getDuration().orElse(Duration.ZERO));
      }
    }
    return duration;
  }


  @EventHandler(priority = EventPriority.LOW) // LOW (after players are moved to spectator)
  public void onMatchOpen(MatchOpenEvent event) {
    callStateChange(Optional.empty(), Optional.of(getState()));
    this.task.start();
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onMatchClose(MatchCloseEvent event) {
    this.task.cancel0();
  }

  public void next() {
    setState(getNextState());
  }

  public boolean previous() {
    Optional<State> previous = getPreviousState();
    if (previous.isPresent()) {
      setState(getPreviousState());
      return true;
    }
    return false;
  }

  public State getState() {
    Optional<State> state = getStateAt(this.current);
    Preconditions.checkArgument(state.isPresent(),
        "can't call getState() when state isn't present, use isStatePresent()");
    return state.get();
  }

  public void setState(Optional<State> state) {
    int index = this.states.size();
    if (state.isPresent()) {
      index = this.states.indexOf(state.get());
    }

    Optional<State> previous = Optional.of(getState());
    callStateChange(previous, state);
    this.current = index;

    if (previous.isPresent()) {
      previous.get().end(Instant.now());
    }
    if (state.isPresent()) {
      state.get().start(Instant.now());
    }

    this.task.cancel0();
    if (isStatePresent()) {
      this.task = new StateTask(this.match, this);
      this.task.start();
    }
  }

  public boolean isStatePresent() {
    Optional<State> state = getStateAt(this.current);
    return state.isPresent();
  }

  public Optional<State> getPreviousState() {
    return getStateAt(this.current - 1);
  }

  public Optional<State> getNextState() {
    return getStateAt(this.current + 1);
  }

  public Optional<State> getStateAt(int index) {
    if (index >= this.states.size()) {
      return Optional.empty();
    }
    return Optional.of(this.states.get(index));
  }

  private MatchStateChangeEvent callStateChange(Optional<State> from, Optional<State> to) {
    return Events.call(new MatchStateChangeEvent(this.match, from, to));
  }
}
