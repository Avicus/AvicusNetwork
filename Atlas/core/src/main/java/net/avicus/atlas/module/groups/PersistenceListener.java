package net.avicus.atlas.module.groups;

import com.google.common.collect.ArrayListMultimap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.avicus.atlas.event.group.PlayerChangedGroupEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PersistenceListener implements Listener {

  private final GroupsModule module;
  private final ArrayListMultimap<Group, UUID> lockedPlayers;

  public PersistenceListener(GroupsModule module) {
    this.module = module;
    this.lockedPlayers = ArrayListMultimap.create();
  }

  public PersistenceListener init() {
    // Allows this to be enabled mid-game
    for (Group group : this.module.getGroups()) {
      if (group.isSpectator()) {
        continue;
      }

      if (!this.lockedPlayers.containsKey(group)) {
        for (Player player : group.getPlayers()) {
          this.lockedPlayers.put(group, player.getUniqueId());
        }
      }
    }
    return this;
  }

  private Optional<Group> getLockedGroup(Player player) {
    for (Group group : this.lockedPlayers.keySet()) {
      List<UUID> locked = this.lockedPlayers.get(group);
      if (locked.contains(player.getUniqueId())) {
        return Optional.of(group);
      }
    }

    return Optional.empty();
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPlayerChange(PlayerChangedGroupEvent event) {
    // Allow players to leave
    if (event.getGroup().isSpectator()) {
      return;
    }

    // Cache a player with a team
    Optional<Group> locked = getLockedGroup(event.getPlayer());
    if (locked.isPresent()) {
      event.setGroup(locked.get());
    } else {
      this.lockedPlayers.put(event.getGroup(), event.getPlayer().getUniqueId());
    }
  }
}
