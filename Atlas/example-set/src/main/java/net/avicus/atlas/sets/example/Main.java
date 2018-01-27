package net.avicus.atlas.sets.example;

import net.avicus.atlas.external.ModuleSet;
import org.bukkit.Bukkit;

public class Main extends ModuleSet {

  @Override
  public void onEnable() {
    Bukkit.getLogger().info("Hello World!");
  }

  @Override
  public void onDisable() {
    Bukkit.getLogger().info("Goodbye Cruel World!");
  }
}
