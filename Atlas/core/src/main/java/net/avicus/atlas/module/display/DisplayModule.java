package net.avicus.atlas.module.display;

import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.event.match.MatchCloseEvent;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.Module;
import net.avicus.atlas.util.Events;
import net.avicus.atlas.util.VersionUtil;
import org.bukkit.event.EventHandler;

@ToString
public class DisplayModule implements Module {

  @Getter
  private final ScoreboardHandler scoreboard;
  private final FriendlyInvisibilityTask friendlyInvisTask;

  public DisplayModule(Match match) {
    this.scoreboard = new ScoreboardHandler(match);
    this.friendlyInvisTask = new FriendlyInvisibilityTask(match, this);
  }

  @Override
  public void open() {
    Events.register(this.scoreboard);
    if (!VersionUtil.isCombatUpdate()) {
      this.friendlyInvisTask.start();
      Events.register(this.friendlyInvisTask);
    }
  }

  @EventHandler
  public void onMatchClose(MatchCloseEvent event) {
    if (!VersionUtil.isCombatUpdate()) {
      this.friendlyInvisTask.cancel0();
      Events.unregister(this.friendlyInvisTask);
    }
  }
}
