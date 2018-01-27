package net.avicus.atlas.countdown;

import net.avicus.atlas.match.Match;
import net.avicus.compendium.countdown.Countdown;
import org.bukkit.ChatColor;
import org.joda.time.Duration;

/**
 * A countdown that exists inside of a {@link Match}.
 */
public abstract class MatchCountdown extends Countdown {

  /**
   * Match the countdown is being ran inside of.
   */
  protected final Match match;

  /**
   * Constructor.
   *
   * @param match match the countdown is being ran inside of
   * @param duration duration of the countdown
   */
  protected MatchCountdown(Match match, Duration duration) {
    super(duration);
    this.match = match;
  }

  /**
   * Colorizes text based on time remaining.
   *
   * @param elapsed time elapsed so far
   * @return colorized text
   */
  protected ChatColor determineTimeColor(Duration elapsed) {
    double percent =
        100 - 100 * (double) elapsed.getStandardSeconds() / this.duration.getStandardSeconds();
    ChatColor color = ChatColor.GREEN;
    if (percent <= 33) {
      color = ChatColor.RED;
    } else if (percent < 66) {
      color = ChatColor.YELLOW;
    }

    return color;
  }

  /**
   * Determine if chat messages should be sent to players.
   *
   * @param secs time remaining
   * @return if chat messages should be sent to players
   */
  protected boolean shouldBroadcast(int secs) {
    return secs % 600 == 0 || // 10 minutes
        (secs <= 600 && secs % 120 == 0) || // 2 minutes for last 10 minutes
        (secs <= 120 && secs % 30 == 0) || // 30 seconds for last 2 minutes
        (secs <= 30 && secs % 5 == 0) || // 5 seconds for last 30 seconds
        (secs <= 5); // Every second for last 5 seconds
  }
}
