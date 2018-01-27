package net.avicus.atlas.countdown;

import com.google.common.base.Preconditions;
import java.util.Optional;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.Competitor;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.states.StatesModule;
import net.avicus.atlas.util.Events;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizedFormat;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.compendium.locale.text.LocalizedText;
import net.avicus.compendium.sound.SoundEvent;
import net.avicus.compendium.sound.SoundLocation;
import net.avicus.compendium.sound.SoundType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.github.paperspigot.Title;
import org.joda.time.Duration;

/**
 * Countdown that is used to start a match.
 * This may be triggered from an {@link AutoStartingCountdown}
 */
public class StartingCountdown extends MatchCountdown {

  /**
   * Constructor. <p> <p>NOTE: If no duration is supplied the value will be pulled from the match's
   * {@link net.avicus.atlas.module.map.CountdownConfig}</p>
   *
   * @param match match the countdown is being ran inside of
   * @param duration duration of the countdown
   */
  public StartingCountdown(Match match, Optional<Duration> duration) {
    super(match, duration.isPresent() ? duration.get()
        : match.getMap().getCountdownConfig().getDuration(StartingCountdown.class));
    Preconditions.checkArgument(match.getRequiredModule(StatesModule.class).isStarting(),
        "match isn't in a starting phase");
  }

  /**
   * Constructor.
   * *
   *
   * @param match match the countdown is being ran inside of
   */
  public StartingCountdown(Match match) {
    this(match, Optional.empty());
  }

  @Override
  public Localizable getName() {
    return Messages.GENERIC_COUNTDOWN_STARTING_NAME.with();
  }

  @Override
  protected void onTick(Duration elapsedTime, Duration remainingTime) {
    int elapsed = (int) elapsedTime.getStandardSeconds();
    int remaining = (int) remainingTime.getStandardSeconds();

    // "Match starting in X seconds!"
    Localizable text = createText(remaining);

    // Boss Bar
    float portion = (float) remaining / ((float) elapsed + (float) remaining);
    this.updateBossBar(text, portion);

    // Broadcast
    if (remaining % 10 == 0 || remaining <= 5) {
      this.match.broadcast(text);
    }

    // Title + Ding at 3, 2, 1
    if (remaining <= 3) {
      Localizable time = new LocalizedNumber(remaining, TextStyle.ofColor(ChatColor.RED));
      for (Competitor competitor : this.match.getRequiredModule(GroupsModule.class)
          .getCompetitors()) {
        for (Player player : competitor.getPlayers()) {
          Title title = new Title("", time.translate(player.getLocale()).toLegacyText(), 4, 10, 4);
          player.sendTitle(title);
          SoundEvent call = Events
              .call(new SoundEvent(player, SoundType.PIANO, SoundLocation.MATCH_DING));
          call.getSound().play(player, 1F);
        }
      }
    }
  }

  /**
   * Generate the text that is displayed to users.
   *
   * @param remaining time remaining
   * @return text for display
   */
  private Localizable createText(int remaining) {
    Localizable time = new LocalizedNumber(remaining, TextStyle.ofColor(ChatColor.RED));

    LocalizedFormat formatter = Messages.MATCH_STARTING_PLURAL;
    if (remaining == 1) {
      formatter = Messages.MATCH_STARTING;
    }

    return formatter.with(TextStyle.ofColor(ChatColor.GREEN), time);
  }

  @Override
  protected void onEnd() {
    // Hide boss bar
    this.clearBossBars();

    // Broadcast
    LocalizedText message = Messages.MATCH_STARTED.with(TextStyle.ofColor(ChatColor.GREEN));
    this.match.broadcast(message);

    // Go to next state
    this.match.getRequiredModule(StatesModule.class).next();

    // "Play" Title and Ding
    for (Competitor competitor : this.match.getRequiredModule(GroupsModule.class)
        .getCompetitors()) {
      for (Player player : competitor.getPlayers()) {
        Title title = new Title("",
            Messages.UI_PLAY.with(ChatColor.GREEN).translate(player.getLocale()).toLegacyText(), 4,
            10, 4);
        player.sendTitle(title);
        SoundEvent call = Events
            .call(new SoundEvent(player, SoundType.PIANO, SoundLocation.MATCH_START));
        call.getSound().play(player, 1.05F);
      }
    }
  }

  @Override
  protected void onCancel() {
    this.clearBossBars();
  }
}