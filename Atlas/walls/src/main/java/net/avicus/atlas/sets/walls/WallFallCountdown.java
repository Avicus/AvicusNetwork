package net.avicus.atlas.sets.walls;

import java.util.List;
import lombok.ToString;
import net.avicus.atlas.countdown.MatchCountdown;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.shop.PlayerEarnPointEvent;
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

/**
 * A countdown which ends with a {@link Wall wall} falling.
 */
@ToString
public final class WallFallCountdown extends MatchCountdown {

  /**
   * The wall.
   */
  private final List<Wall> walls;

  WallFallCountdown(final Match match, final List<Wall> walls, final Duration duration) {
    super(match, duration);
    this.walls = walls;
  }

  @Override
  public Localizable getName() {
    return new UnlocalizedText("Wall Fall");
  }

  @Override
  protected void onTick(Duration elapsedTime, Duration remainingTime) {
    final int remainingSeconds = (int) remainingTime.getStandardSeconds();
    final Localizable message = Messages.GENERIC_WALLS_FALL_WILL.with(ChatColor.GRAY,
        new UnlocalizedText(StringUtil.secondsToClock(remainingSeconds),
            this.determineTimeColor(elapsedTime)));
    this.updateBossBar(message, elapsedTime);

    if (this.shouldBroadcast(remainingSeconds)) {
      this.match.importantBroadcast(message);
      this.match.getPlayers().forEach(
          (player) -> Events.call(new SoundEvent(player, SoundType.PIANO, SoundLocation.MATCH_DING))
              .getSound().play(player, 1F));
    }
  }

  @Override
  protected void onEnd() {
    this.clearBossBars();
    this.walls.forEach(wall -> wall.transitionTo(Wall.State.DESTROY));
    Events.call(new WallsFallEvent());
    this.match.getPlayers().forEach((player) -> {
      Events
          .call(new SoundEvent(player, SoundType.DOOR_BREAK, SoundLocation.WALL_FALL)).getSound()
          .play(player, 1F);
      Events.call(new PlayerEarnPointEvent(player, "wall-fall"));
    });
    this.match.importantBroadcast(Messages.GENERIC_WALLS_FALL_FELL.with(ChatColor.GRAY));
  }

  @Override
  protected void onCancel() {
    this.clearBossBars();
  }
}
