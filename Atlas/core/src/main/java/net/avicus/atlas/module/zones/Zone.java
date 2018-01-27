package net.avicus.atlas.module.zones;

import java.util.Optional;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.magma.util.region.Region;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

@Getter
@ToString(exclude = "match")
public abstract class Zone implements Listener {

  protected final Match match;
  protected final Region region;
  protected final Optional<ZoneMessage> message;

  public Zone(Match match, Region region, Optional<ZoneMessage> message) {
    this.match = match;
    this.region = region;
    this.message = message;
  }

  public boolean isObserving(Match match, Player player) {
    try {
      return match.getRequiredModule(GroupsModule.class).isObservingOrDead(player);
    } catch (RuntimeException e) {
      // Not in a group, count as observing.
      return true;
    }
  }

  public abstract boolean isActive();

  public void message(Player player) {
    this.message.ifPresent(m -> m.send(player));
  }
}
