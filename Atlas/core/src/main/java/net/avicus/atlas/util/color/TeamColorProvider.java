package net.avicus.atlas.util.color;

import java.util.Optional;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.compendium.color.ColorProvider;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

/**
 * Converts {@link TeamColor}s to colors that can be used for dying items and setting color damage
 * values.
 */
public class TeamColorProvider implements ColorProvider {

  /**
   * Match that the provider is handling colors for.
   */
  private final Match match;

  /**
   * Constructor.
   *
   * @param match match that the provider is handling colors for
   */
  public TeamColorProvider(Match match) {
    this.match = match;
  }

  /**
   * Helper method to getFirst the team color of a player.
   *
   * @param player player to getFirst the color for
   * @return color of the player's group
   */
  private TeamColor getTeamColor(Player player) {
    return this.match.getRequiredModule(GroupsModule.class).getGroup(player).getTeamColor();
  }

  public DyeColor getDyeColor(Optional<Player> player) {
    if (player.isPresent()) {
      return getTeamColor(player.get()).getDyeColor();
    }
    return DyeColor.BLACK;
  }

  @Override
  public Color getColor(Optional<Player> player) {
    return getDyeColor(player).getColor();
  }
}
