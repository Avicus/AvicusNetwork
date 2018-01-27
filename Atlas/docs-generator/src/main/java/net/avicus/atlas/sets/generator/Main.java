package net.avicus.atlas.sets.generator;

import lombok.Setter;
import net.avicus.atlas.external.ModuleSet;
import net.avicus.compendium.commands.AvicusCommandsRegistration;

public class Main extends ModuleSet {

  @Setter
  AvicusCommandsRegistration registrar;

  @Override
  public void onEnable() {
    this.registrar.register(GenerateCommand.class);
  }

  @Override
  public void onDisable() {

  }
}
