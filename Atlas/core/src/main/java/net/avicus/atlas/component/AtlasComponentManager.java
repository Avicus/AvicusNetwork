package net.avicus.atlas.component;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import net.avicus.magma.module.ModuleManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

/**
 * This is called component as to not conflict with XML modules.
 */
public class AtlasComponentManager extends ModuleManager {

  public AtlasComponentManager(PluginManager pluginManager, Plugin plugin,
      CommandsManagerRegistration commandRegistrar) {
    super(pluginManager, plugin, commandRegistrar);
  }
}
