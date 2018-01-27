package net.avicus.atlas.map.rotation;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.countdown.CyclingCountdown;
import net.avicus.atlas.countdown.StartingCountdown;
import net.avicus.atlas.map.AtlasMap;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.util.AtlasTask;
import net.avicus.compendium.countdown.Countdown;
import net.avicus.compendium.countdown.CountdownManager;
import net.avicus.compendium.countdown.RestartingCountdown;
import net.avicus.compendium.plugin.CompendiumPlugin;
import org.apache.commons.lang.mutable.MutableInt;
import org.joda.time.Duration;

@ToString
public class Rotation {

  @Getter
  private final List<Match> matches;
  @Getter
  private final MutableInt currentIndex;
  @Getter
  private boolean restartQueued = false;
  @Getter
  @Setter
  private boolean voteQueued = false;

  @Getter
  @Setter
  private boolean nextRequestQueued = false;
  @Getter
  @Setter
  private AtlasMap nextRequestMap = null;

  public Rotation(List<Match> matches) {
    this.matches = matches;
    this.currentIndex = new MutableInt(-1);
  }

  private CountdownManager getCountdownManager() {
    return CompendiumPlugin.getInstance().getCountdownManager();
  }

  public void cancelAllAndStart(Countdown countdown) {
    CountdownManager manager = this.getCountdownManager();
    manager.cancelAll();
    manager.start(countdown);
  }

  public boolean isStarting() {
    return this.getCountdownManager().isRunning(StartingCountdown.class);
  }

  public boolean isCycling() {
    return this.getCountdownManager().isRunning(CyclingCountdown.class);
  }

  public boolean isRestarting() {
    return this.getCountdownManager().isRunning(RestartingCountdown.class);
  }

  public void startMatch(StartingCountdown countdown) {
    this.cancelAllAndStart(countdown);
  }

  public void cycleMatch(CyclingCountdown countdown) {
    this.cancelAllAndStart(countdown);
  }

  public void queueRestart() {
    this.restartQueued = true;

    // No players online
    if (Atlas.get().getServer().getOnlinePlayers().isEmpty() || this.isCycling() || this
        .isStarting()) {
      this.startRestartCountdown(new RestartingCountdown(Duration.standardSeconds(30)));
    }
  }

  public void startRestartCountdown(RestartingCountdown countdown) {
    this.cancelAllAndStart(countdown);
  }

  public List<Match> getNextMatches() {
    if (!getNextMatch().isPresent()) {
      return new ArrayList<>();
    }
    int from = this.currentIndex.intValue() + 1;
    int to = this.matches.size();
    return this.matches.subList(from, to);
  }

  public void start() throws IOException {
    this.currentIndex.add(1);
    getMatch().load();
    getMatch().open();
  }

  public boolean remove(int index, boolean force) throws IllegalArgumentException {
    if (index <= this.currentIndex.intValue() || index > this.matches.size()) {
      throw new IllegalArgumentException("Invalid index: " + index);
    }

    if (!force) {
      if (isRestarting()) {
        return false;
      }
      if (isCycling() && index == this.currentIndex.intValue() + 1) {
        return false;
      }
    }

    // cancel countdowns if they are there
    this.getCountdownManager().cancelAll(countdown -> countdown instanceof RestartingCountdown
        || countdown instanceof CyclingCountdown);

    this.matches.remove(index);
    return true;
  }

  public boolean next(Match match, boolean force) throws IllegalArgumentException {
    return insert(this.currentIndex.intValue() + 1, match, force);
  }

  public boolean append(Match match, boolean force) {
    return insert(this.matches.size(), match, force);
  }

  /**
   * @param force True to ignore existing countdowns & cancel them.
   * @return If countdowns are in progress.
   * @throws RotationException If the slot is prior to the current slot.
   */
  public boolean insert(int index, Match match, boolean force) throws IllegalArgumentException {
    if (index <= this.currentIndex.intValue() || index > this.matches.size()) {
      throw new IllegalArgumentException("invalid index " + index);
    }

    if (!force) {
      if (isRestarting()) {
        return false;
      }
      if (isCycling() && index == this.currentIndex.intValue() + 1) {
        return false;
      }
    }

    // cancel countdowns if they are there
    this.getCountdownManager().cancelAll(countdown -> countdown instanceof RestartingCountdown
        || countdown instanceof CyclingCountdown);

    this.matches.add(index, match);
    return true;
  }

  public Match getMatch() {
    Optional<Match> match = getMatchAt(this.currentIndex.intValue());
    Preconditions.checkArgument(match.isPresent(),
        "can't call getMatch() when state isn't present, use isMatchPresent()");
    return match.get();
  }

  public boolean isMatchPresent() {
    Optional<Match> match = getMatchAt(this.currentIndex.intValue());
    return match.isPresent();
  }

  public Optional<Match> getNextMatch() {
    return getMatchAt(this.currentIndex.intValue() + 1);
  }

  public Optional<Match> getMatchAt(int index) {
    if (index >= this.matches.size()) {
      return Optional.empty();
    }
    return Optional.of(this.matches.get(index));
  }

  /**
   * Cycles to the next match.
   *
   * @throws IOException If the map folder couldn't be copied.
   * @throws RotationException If the end of the rotation is reached.
   */
  public void cycle() throws IOException, RotationException {
    Match from = getMatch();
    Optional<Match> to = getNextMatch();

    if (!to.isPresent()) {
      throw new RotationException("No match to cycle.");
    }

    from.close();

    setNextRequestMap(null);
    setNextRequestQueued(false);

    this.currentIndex.add(1);

    to.get().load();
    to.get().open();

    // unload after new match is setup
    new AtlasTask() {
      @Override
      public void run() {
        from.unloadWorld();
      }
    }.later(20 * 10);
  }
}
