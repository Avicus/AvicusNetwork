package net.avicus.hook;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import net.avicus.magma.module.ModuleManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class HookModuleManager extends ModuleManager {

  public HookModuleManager(PluginManager pluginManager, Plugin plugin,
      CommandsManagerRegistration commandRegistrar) {
    super(pluginManager, plugin, commandRegistrar);
  }
}
