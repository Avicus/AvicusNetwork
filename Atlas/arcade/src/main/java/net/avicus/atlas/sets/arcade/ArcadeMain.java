package net.avicus.atlas.sets.arcade;

import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.component.AtlasComponentManager;
import net.avicus.atlas.component.visual.SidebarComponent;
import net.avicus.atlas.countdown.CyclingCountdown;
import net.avicus.atlas.countdown.StartingCountdown;
import net.avicus.atlas.external.ModuleSet;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.module.map.CountdownConfig;
import net.avicus.atlas.sets.arcade.carts.CartsFactory;
import org.joda.time.Seconds;

@Getter
public class ArcadeMain extends ModuleSet {

  @Getter
  private static ArcadeMain instance;
  @Setter
  private Atlas atlas;
  @Setter
  private MatchFactory matchFactory;
  @Setter
  private Logger logger;
  @Getter
  private SBHook sbHook;

  @Override
  public void onEnable() {
    instance = this;
    this.logger.info("Enabling arcade set.");

    CountdownConfig.DEFAULT_VALUES.clear();
    CountdownConfig.DEFAULT_VALUES
        .put(StartingCountdown.class, Seconds.seconds(15).toStandardDuration());
    CountdownConfig.DEFAULT_VALUES
        .put(CyclingCountdown.class, Seconds.seconds(10).toStandardDuration());

    matchFactory.register(CartsFactory.class);

    this.logger.info("Enabled arcade set.");
  }

  @Override
  public void onDisable() {
    this.logger.info("Disabled arcade set.");
  }

  @Override
  public void onComponentsEnable(AtlasComponentManager componentManager) {
    if (componentManager.hasModule(SidebarComponent.class)) {
      this.sbHook = new SBHook(this);
      SidebarComponent.HOOKS.add(this.sbHook);
      this.logger.info("Registered sidebar hook.");
    }
  }
}
