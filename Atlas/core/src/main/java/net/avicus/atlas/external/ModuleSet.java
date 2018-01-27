package net.avicus.atlas.external;

import java.util.logging.Logger;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.component.AtlasComponentManager;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.compendium.commands.AvicusCommandsRegistration;

/**
 * Class to represent a jar that is loaded externally.
 **/
public abstract class ModuleSet {

  public abstract void onEnable();

  public abstract void onDisable();

  public void setAtlas(Atlas atlas) {
  }

  public void setMatchFactory(MatchFactory matchFactory) {
  }

  public void setLogger(Logger logger) {
  }

  public void setRegistrar(AvicusCommandsRegistration registrar) {
  }

  public void onComponentsEnable(AtlasComponentManager componentManager) {
  }
}
