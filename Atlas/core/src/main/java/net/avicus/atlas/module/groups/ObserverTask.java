package net.avicus.atlas.module.groups;

import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.tutorial.TutorialModule;
import net.avicus.atlas.util.AtlasTask;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.settings.PlayerSettings;
import net.avicus.compendium.settings.Setting;
import net.avicus.compendium.settings.types.SettingTypes;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ObserverTask extends AtlasTask {

  public static final Setting<Boolean> SPECTATOR_VIEW_SETTING = new Setting<>(
      "spectator-view",
      SettingTypes.BOOLEAN,
      true,
      Messages.SETTINGS_SPECTATOR_VIEW,
      Messages.SETTINGS_SPECTATOR_VIEW_SUMMARY
  );

  static {
    PlayerSettings.register(SPECTATOR_VIEW_SETTING);
  }

  private final Match match;
  private final GroupsModule module;

  public ObserverTask(Match match, GroupsModule module) {
    super();
    this.match = match;
    this.module = module;
  }

  @Override
  public void run() {
    execute();
  }

  public ObserverTask start() {
    this.repeat(0, 20);
    return this;
  }

  public void execute() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      boolean observing = this.module.isObserving(player);

      if (observing) {
        player.setFireTicks(0);
        player.setRemainingAir(20);
      }

      for (Player target : Bukkit.getOnlinePlayers()) {
        if (player.equals(target)) {
          continue;
        }

        if (shouldSee(player, target)) {
          player.showPlayer(target);
        } else {
          player.hidePlayer(target);
        }
      }
    }
  }

  private boolean shouldSee(Player player, Player target) {
    boolean targetInTutorial = this.match.getModule(TutorialModule.class)
        .map((t) -> t.isWatchingTutorial(target)).orElse(false);

    if (targetInTutorial) {
      return false;
    }

    boolean playerObserver = this.module.isObserving(player);
    boolean targetObserver = this.module.isObserving(target);
    boolean targetDead = this.module.isObservingOrDead(target) && !targetObserver;

    // No one can see dead players
    if (targetDead) {
      return false;
    }

    boolean inTutorial = this.match.getModule(TutorialModule.class)
        .map((module) -> module.isWatchingTutorial(target)).orElse(false);

    // No one can see players in tutorial
    if (inTutorial) {
      return false;
    }

    // Allow player to see others if they are an observer
    if (playerObserver) {
      return !targetObserver || PlayerSettings.get(player, SPECTATOR_VIEW_SETTING);
    }

    // Otherwise the target must simply be participating in the match
    return !targetObserver;
  }
}
