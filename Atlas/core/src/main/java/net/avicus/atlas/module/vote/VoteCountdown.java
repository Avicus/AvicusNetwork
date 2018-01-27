package net.avicus.atlas.module.vote;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.countdown.CyclingCountdown;
import net.avicus.atlas.countdown.MatchCountdown;
import net.avicus.atlas.map.rotation.Rotation;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.util.Events;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.StringUtil;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.compendium.sound.SoundEvent;
import net.avicus.compendium.sound.SoundLocation;
import net.avicus.compendium.sound.SoundType;
import org.bukkit.ChatColor;
import org.joda.time.Duration;
import org.joda.time.Seconds;

public class VoteCountdown extends MatchCountdown {

  private final VoteModule module;

  VoteCountdown(Match match, Duration duration, VoteModule module) {
    super(match, duration);
    this.module = module;
  }

  @Override
  public Localizable getName() {
    return Messages.VOTE_TITLE.with();
  }

  @Override
  protected void onTick(Duration elapsedTime, Duration remainingTime) {
    final int remainingSeconds = (int) remainingTime.getStandardSeconds();
    final Localizable message = Messages.VOTE_COUNTDOWN_TIME.with(ChatColor.AQUA,
        new UnlocalizedText(StringUtil.secondsToClock((int) remainingTime.getStandardSeconds()),
            this.determineTimeColor(elapsedTime)));

    this.updateBossBar(message, elapsedTime);

    if (this.shouldBroadcast(remainingSeconds)) {
      this.match.broadcast(message);
      this.match.getPlayers().forEach((player) -> {
        Events.call(new SoundEvent(player, SoundType.PIANO, SoundLocation.MATCH_DING)).getSound()
            .play(player, 1F);
      });
    }
  }

  @Override
  protected void onEnd() {
    this.clearBossBars();

    final Collection<Match> maps = this.module.votes();
    final Map<Match, Integer> votes = Maps.newHashMap();
    for (Match map : maps) {
      votes.put(map, Collections.frequency(maps, map));
    }

    Map.Entry<Match, Integer> winner = null;
    for (Map.Entry<Match, Integer> entry : votes.entrySet()) {
      if (winner == null || entry.getValue().compareTo(winner.getValue()) > 0) {
        winner = entry;
      }
    }

    if (winner != null) {
      this.match.importantBroadcast(Messages.VOTE_WON.with(ChatColor.GREEN,
          new UnlocalizedText(winner.getKey().getMap().getName(), ChatColor.DARK_AQUA)));
      try {
        final Rotation rotation = Atlas.get().getMatchManager().getRotation();
        final Match match = winner.getKey();
        rotation.next(match, true);
        rotation.cycleMatch(new CyclingCountdown(this.match, match,
            Optional.of(Seconds.seconds(15).toStandardDuration())));
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      this.match.broadcast(Messages.VOTE_NONE.with(ChatColor.RED));
    }

    this.module.started = false;
  }

  @Override
  protected void onCancel() {
    this.clearBossBars();
  }
}
