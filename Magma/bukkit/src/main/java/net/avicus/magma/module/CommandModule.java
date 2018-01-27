package net.avicus.magma.module;

import com.sk89q.bukkit.util.CommandsManagerRegistration;

/**
 * A module with commands.
 */
public interface CommandModule extends Module {

  /**
   * Register commands.
   *
   * @param registrar the command registrar
   */
  void registerCommands(CommandsManagerRegistration registrar);
}
