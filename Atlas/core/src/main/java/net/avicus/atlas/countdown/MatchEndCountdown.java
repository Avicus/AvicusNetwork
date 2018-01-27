package net.avicus.atlas.countdown;

import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.results.scenario.EndScenario;
import net.avicus.atlas.util.Events;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.StringUtil;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.compendium.sound.SoundEvent;
import net.avicus.compendium.sound.SoundLocation;
import net.avicus.compendium.sound.SoundType;
import org.bukkit.ChatColor;
import org.joda.time.Duration;

/**
 * A countdown that is used to end a match.
 */
public class MatchEndCountdown extends MatchCountdown {

  private final EndScenario scenario;

  /**
   * Constructor.
   *
   * @param match match the countdown is being run inside of
   * @param duration duration of the countdown
   * @param scenario scenario to execute on end
   */
  public MatchEndCountdown(Match match, Duration duration, EndScenario scenario) {
    super(match, duration);
    this.scenario = scenario;
  }

  @Override
  public Localizable getName() {
    return Messages.GENERIC_COUNTDOWN_END_NAME.with();
  }

  @Override
  protected void onTick(Duration elapsedTime, Duration remainingTime) {
    int sec = (int) remainingTime.getStandardSeconds();

    Localizable message = timeRemainingMessage(elapsedTime, remainingTime);

    // Boss bar
    updateBossBar(message, elapsedTime);

    // Periodic chat broadcast
    if (shouldBroadcast(sec)) {
      Localizable broadcast = Messages.UI_IMPORTANT.with(TextStyle.ofBold(), message);
      this.match.broadcast(broadcast);

      this.match.getPlayers().forEach((p) -> {
        SoundEvent call = Events.call(new SoundEvent(p, SoundType.PIANO, SoundLocation.MATCH_DING));
        call.getSound().play(p, 1F);
      });
    }
  }

  /**
   * Generate the text that is displayed to users.
   *
   * @param elapsedTime time elapsed
   * @param remainingTime time remaining
   * @return text for display
   */
  public Localizable timeRemainingMessage(Duration elapsedTime, Duration remainingTime) {
    ChatColor color = determineTimeColor(elapsedTime);
    UnlocalizedText time = new UnlocalizedText(
        StringUtil.secondsToClock((int) remainingTime.getStandardSeconds()), color);

    return Messages.MATCH_TIME_REMAINING.with(ChatColor.WHITE, time);
  }

  @Override
  protected void onEnd() {
    this.clearBossBars();
    this.scenario.execute(this.match, this.match.getRequiredModule(GroupsModule.class));
  }

  @Override
  protected void onCancel() {
    this.clearBossBars();
  }
}
