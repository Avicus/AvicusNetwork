package net.avicus.atlas.sets.competitve.objectives.phases;

import java.util.List;
import java.util.Optional;
import net.avicus.atlas.countdown.MatchCountdown;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.locales.LocalesModule;
import net.avicus.atlas.sets.competitve.objectives.destroyable.DestroyableObjective;
import net.avicus.atlas.util.Events;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.StringUtil;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizedTextFormat;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.compendium.plugin.CompendiumPlugin;
import net.avicus.compendium.sound.SoundEvent;
import net.avicus.compendium.sound.SoundLocation;
import net.avicus.compendium.sound.SoundType;
import org.bukkit.ChatColor;
import org.joda.time.Duration;

/**
 * A countdown that is used to apply a phase.
 */
public class PhaseApplyCountdown extends MatchCountdown {

  /**
   * The phase that this countdown is attempting to apply.
   */
  private final DestroyablePhase phase;

  private final List<DestroyableObjective> objectives;

  /**
   * Constructor.
   *
   * @param match match the countdown is being run inside of
   * @param duration duration of the countdown
   * @param phase phase that this countdown is attempting to apply
   */
  public PhaseApplyCountdown(Match match, Duration duration, DestroyablePhase phase,
      List<DestroyableObjective> objectives) {
    super(match, duration);
    this.phase = phase;
    this.objectives = objectives;
  }

  @Override
  public Localizable getName() {
    return Messages.GENERIC_COUNTDOWN_PHASE_APPLY_NAME.with();
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

    return new LocalizedTextFormat(match.getRequiredModule(LocalesModule.class).safeGetBundle(),
        this.phase.getCountdownMessage().toText(), this.phase.getName().toText(), time);
  }

  @Override
  protected void onEnd() {
    this.clearBossBars();
    Optional<PhaseApplyCountdown> count = phase.attemptApply(this.objectives);
    if (count.isPresent()) {
      CompendiumPlugin.getInstance().getCountdownManager().start(count.get());
    }
  }

  @Override
  protected void onCancel() {
    this.clearBossBars();
  }
}
