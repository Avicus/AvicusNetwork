package net.avicus.atlas.module.map;

import java.util.HashMap;
import net.avicus.atlas.countdown.CyclingCountdown;
import net.avicus.atlas.countdown.StartingCountdown;
import net.avicus.compendium.countdown.Countdown;
import org.joda.time.Duration;
import org.joda.time.Seconds;

public class CountdownConfig {

  public static final HashMap<Class<? extends Countdown>, Duration> DEFAULT_VALUES = new HashMap<>();

  static {
    DEFAULT_VALUES.put(StartingCountdown.class, Seconds.seconds(25).toStandardDuration());
    DEFAULT_VALUES.put(CyclingCountdown.class, Seconds.seconds(30).toStandardDuration());
  }

  private final HashMap<Class<? extends Countdown>, Duration> countdowns = new HashMap<>();

  public void addCountdown(Countdown countdown, Duration duration) {
    addCountdown(countdown.getClass(), duration);
  }

  public void addCountdown(Class<? extends Countdown> countdown, Duration duration) {
    if (this.countdowns.containsKey(countdown)) {
      this.countdowns.remove(countdown);
    }

    this.countdowns.put(countdown, duration);
  }

  public void removeCountdown(Countdown countdown) {
    this.countdowns.remove(countdown.getClass());
  }

  public Duration getDuration(Countdown countdown) {
    return getDuration(countdown.getClass());
  }

  public Duration getDuration(Class<? extends Countdown> countdown) {
    if (this.countdowns.containsKey(countdown)) {
      return this.countdowns.get(countdown);
    }

    return DEFAULT_VALUES.get(countdown);
  }
}
