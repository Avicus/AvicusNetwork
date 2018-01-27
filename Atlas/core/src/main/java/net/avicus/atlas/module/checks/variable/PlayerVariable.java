package net.avicus.atlas.module.checks.variable;

import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.module.checks.Variable;
import org.bukkit.entity.Player;

/**
 * The player variable contains information about the player who is performing a checked action.
 * This holds all information about a player but is never used to find a specific player by name.
 */
@ToString
public class PlayerVariable implements Variable {

  @Getter
  private final Player player;

  public PlayerVariable(Player player) {
    this.player = player;
  }
}
