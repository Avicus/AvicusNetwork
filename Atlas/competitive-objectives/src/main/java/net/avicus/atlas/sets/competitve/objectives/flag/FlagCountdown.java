package net.avicus.atlas.sets.competitve.objectives.flag;

import java.util.Optional;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.countdown.MatchCountdown;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.sets.competitve.objectives.flag.events.FlagRecoverEvent;
import net.avicus.atlas.sets.competitve.objectives.zones.flag.PostZone;
import net.avicus.atlas.util.Events;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizedFormat;
import net.avicus.compendium.locale.text.LocalizedNumber;
import org.joda.time.Duration;

public class FlagCountdown extends MatchCountdown {

  private final FlagObjective flag;
  private final PostZone spawnPost;

  public FlagCountdown(Match match, Duration duration, FlagObjective flag, PostZone spawnPost) {
    super(match, duration);
    this.flag = flag;
    this.spawnPost = spawnPost;
  }

  @Override
  public Localizable getName() {
    return Messages.GENERIC_COUNTDOWN_FLAG_RECOVER_NAME.with();
  }

  @Override
  protected void onTick(Duration elapsedTime, Duration remainingTime) {
    final int remainingSeconds = (int) remainingTime.getStandardSeconds();

    // Flashy flashy
    Atlas.get().getSideBar().syncUpdate();

    if (shouldBroadcast(remainingSeconds)) {
      LocalizedFormat format;
      if (this.flag.getCurrentLocation().isPresent()) {
        format = Messages.GENERIC_OBJECTIVE_RECOVER_PLURAL;
        if (remainingSeconds == 1) {
          format = Messages.GENERIC_OBJECTIVE_RECOVER;
        }
      } else {
        format = Messages.GENERIC_OBJECTIVE_RESPAWN_PLURAL;
        if (remainingSeconds == 1) {
          format = Messages.GENERIC_OBJECTIVE_RESPAWN;
        }
      }

      Localizable what = this.flag.getName().toText(this.flag.getChatColor());
      Localizable when = new LocalizedNumber(remainingSeconds,
          TextStyle.ofColor(this.flag.getChatColor()));

      Localizable broadcast = format.with(what, when);
      broadcast = Messages.UI_IMPORTANT.with(TextStyle.ofBold(), broadcast);
      this.flag.getMatch().broadcast(broadcast);
    }
  }

  @Override
  protected void onEnd() {
    if (this.flag.getCurrentLocation().isPresent()) {
      this.spawnPost.spawn(this.flag, true);
      Events.call(new FlagRecoverEvent(this.flag));
    } else {
      this.spawnPost.spawn(this.flag, true);
    }

    this.flag.setFlagCountdown(Optional.empty());
  }

  @Override
  protected void onCancel() {
    this.flag.setFlagCountdown(Optional.empty());
  }
}
