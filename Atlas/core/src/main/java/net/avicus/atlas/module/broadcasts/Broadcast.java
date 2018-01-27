package net.avicus.atlas.module.broadcasts;

import java.util.Optional;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.compendium.locale.text.Localizable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.joda.time.Duration;

/**
 * Represents a message displayed to users during the lifetime of this {@link Match}
 */
@ToString
public class Broadcast {

  /**
   * Localized form of the message to be displayed.
   */
  private final LocalizedXmlString message;
  /**
   * The format of the broadcast.
   */
  private final BroadcastFormat format;
  /**
   * Interval between the display of messages.
   */
  private final Duration interval;
  /**
   * Number of times the message should repeat. Empty for infinite.
   */
  @Getter
  private final Optional<Integer> repetitionCount;
  /**
   * Check to be ran before the message is displayed.
   */
  private final Optional<Check> check;

  /**
   * Construct a new broadcast.
   *
   * @param message message to be displayed
   * @param format format of the message
   * @param interval duration between broadcasts
   * @param repetitionCount amount of times the broadcast should be displayed
   * @param check check to be performed before broadcast
   */
  public Broadcast(LocalizedXmlString message, BroadcastFormat format, Duration interval,
      Optional<Integer> repetitionCount, Optional<Check> check) {
    this.message = message;
    this.format = format;
    this.interval = interval;
    this.repetitionCount = repetitionCount;
    this.check = check;
  }

  /**
   * Get the interval of this broadcast in ticks.
   */
  public int tickInterval() {
    return (int) (this.interval.getStandardSeconds() * 20);
  }

  /**
   * Tests any check that this tip has.
   *
   * @param match The match.
   * @return If this tip should be broadcasted.
   */
  public boolean test(Match match) {
    if (this.check.isPresent()) {
      CheckContext context = new CheckContext(match);
      if (this.check.get().test(context).fails()) {
        return false;
      }
    }

    return true;
  }

  /**
   * Broadcasts this tip to the server.
   */
  public void broadcast(Match match) {
    Localizable text = this.format.apply(this.message);
    for (Player player : match.getPlayers()) {
      player.sendMessage(text);
    }
    Bukkit.getConsoleSender().sendMessage(text);
  }

  /**
   * Creates a new tip task, which handles broadcast scheduling.
   *
   * @return The new task.
   */
  public BroadcastTask createTask(Match match) {
    return new BroadcastTask(match, this);
  }
}
