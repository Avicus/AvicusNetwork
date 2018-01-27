package net.avicus.atlas.countdown;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.map.rotation.RotationException;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.countdown.Countdown;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizedFormat;
import net.avicus.compendium.locale.text.LocalizedText;
import net.avicus.compendium.locale.text.UnlocalizedText;
import org.bukkit.ChatColor;
import org.joda.time.Duration;

/**
 * A countdown that is used to handle the transition from one match onto the next.
 */
public class CyclingCountdown extends Countdown {

  /**
   * Match the countdown was started in.
   */
  private final Match from;
  /**
   * Match the countdown will transition to on end.
   */
  private final Match to;

  /**
   * Constructor. <p> <p>NOTE: If no duration is supplied the value will be pulled from the upcoming
   * match's {@link net.avicus.atlas.module.map.CountdownConfig}</p>
   *
   * @param from match the countdown was started in
   * @param to match the countdown will transition to on end
   * @param duration duration of the countdown
   */
  public CyclingCountdown(Match from, Match to, Optional<Duration> duration) {
    super(duration.isPresent() ? duration.get()
        : (to == null ? from.getMap().getCountdownConfig().getDuration(CyclingCountdown.class)
            : to.getMap().getCountdownConfig().getDuration(CyclingCountdown.class)));
    this.from = from;
    this.to = to;
  }

  /**
   * Constructor.
   *
   * @param from match the countdown was started in
   * @param to match the countdown will transition to on end
   */
  public CyclingCountdown(Match from, Match to) {
    this(from, to, Optional.empty());
  }

  @Override
  public Localizable getName() {
    return Messages.GENERIC_COUNTDOWN_CYCLING_NAME.with();
  }

  @Override
  protected void onTick(Duration elapsedTime, Duration remainingTime) {
    int remaining = (int) remainingTime.getStandardSeconds();
    Localizable text = createText(remaining);

    updateBossBar(text, elapsedTime);

    if (remaining % 10 == 0 || remaining <= 5) {
      this.from.broadcast(text);
    }
  }

  /**
   * Generate the text that is displayed to users.
   *
   * @param remaining time remaining
   * @return text for display
   */
  private Localizable createText(int remaining) {
    UnlocalizedText name = new UnlocalizedText(this.to.getMap().getName(), ChatColor.AQUA);
    UnlocalizedText time = new UnlocalizedText(remaining + "", ChatColor.AQUA);

    LocalizedFormat format = Messages.MATCH_CYCLING_PLURAL;
    if (remaining == 1) {
      format = Messages.MATCH_CYCLING;
    }

    return format.with(ChatColor.DARK_AQUA, name, time);
  }

  @Override
  protected void onEnd() {
    try {
      if (Objects.equals(this.from, Atlas.getMatch())) {
        Atlas.get().getMatchManager().getRotation().cycle();
      }

      this.clearBossBars();

      UnlocalizedText name = new UnlocalizedText(this.to.getMap().getName(), ChatColor.AQUA);
      LocalizedText message = Messages.MATCH_CYCLED.with(ChatColor.DARK_AQUA, name);
      this.to.broadcast(message);
    } catch (IOException e) {
      e.printStackTrace();

      UnlocalizedText name = new UnlocalizedText(this.to.getMap().getName());
      LocalizedText message = Messages.ERROR_CYCLE_FAILED.with(ChatColor.RED, name);
      this.to.broadcast(message);
    } catch (RotationException e) {
      // shouldn't happen
    }
  }

  @Override
  protected void onCancel() {
    this.clearBossBars();
  }
}
