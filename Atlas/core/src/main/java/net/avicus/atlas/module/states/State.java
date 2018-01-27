package net.avicus.atlas.module.states;

import java.util.Optional;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.match.registry.RegisterableObject;
import org.joda.time.Duration;
import org.joda.time.Instant;


@ToString(exclude = "nextState")
public class State implements RegisterableObject<State> {

  @Getter
  private final String id;
  @Getter
  private final boolean playing;
  @Getter
  private final Optional<State> nextState;
  private Instant start;
  private Instant end;

  public State(String id, boolean playing, Optional<State> nextState) {
    this.id = id;
    this.playing = playing;
    this.nextState = nextState;
  }

  public void start(Instant when) {
    this.start = when;
    this.end = null;
  }

  public void end(Instant when) {
    this.end = when;
  }

  public Optional<Duration> getDuration() {
    if (this.start == null) {
      return Optional.empty();
    }
    Instant end = this.end != null ? this.end : Instant.now();
    return Optional.of(new Duration(this.start, end));
  }

  public Optional<Instant> getStart() {
    return Optional.ofNullable(this.start);
  }

  public Optional<Instant> getEnd() {
    return Optional.ofNullable(this.end);
  }

  public boolean isOngoing() {
    return this.start != null && this.end == null;
  }

  @Override
  public State getObject() {
    return this;
  }
}
