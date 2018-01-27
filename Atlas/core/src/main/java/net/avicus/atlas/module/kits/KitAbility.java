package net.avicus.atlas.module.kits;

import java.util.Optional;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

@ToString(exclude = "match")
public abstract class KitAbility implements Listener {

  private final Match match;

  public KitAbility(Match match) {
    this.match = match;
  }

  /**
   * Check if a player currently has an ability.
   *
   * @param requirePlaying Set to true to require player to be currently playing in the match.
   */
  protected boolean hasAbility(Player player, boolean requirePlaying) {
    KitsModule kits = this.match.getModule(KitsModule.class).orElse(null);
    if (kits == null) {
      return false;
    }

    Optional<Kit> kit = requirePlaying ? kits.getActiveKit(player) : kits.getCurrentKit(player);

    return kit.map(k -> k.hasAbility(this)).orElse(false);
  }
}
