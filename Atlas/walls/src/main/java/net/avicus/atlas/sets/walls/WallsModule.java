package net.avicus.atlas.sets.walls;

import java.util.List;
import net.avicus.atlas.event.match.MatchStateChangeEvent;
import net.avicus.atlas.module.Module;
import net.avicus.compendium.countdown.CountdownManager;
import net.avicus.compendium.plugin.CompendiumPlugin;
import org.bukkit.event.EventHandler;
import org.joda.time.Duration;

public class WallsModule implements Module {

  private final List<Wall> walls;
  private final Duration fallTIme;

  public WallsModule(List<Wall> walls, Duration fallTIme) {
    this.walls = walls;
    this.fallTIme = fallTIme;
  }

  @Override
  public void open() {
    this.walls.forEach(Wall::initialize);
  }

  @EventHandler
  public void matchStateChange(final MatchStateChangeEvent event) {
    final CountdownManager cm = CompendiumPlugin.getInstance().getCountdownManager();

    if (event.isChangeToPlaying()) {
      cm.start(new WallFallCountdown(event.getMatch(), this.walls, this.fallTIme));
    }

    if (event.isChangeToNotPlaying()) {
      cm.cancelAll(c -> c instanceof WallFallCountdown);
    }
  }
}
