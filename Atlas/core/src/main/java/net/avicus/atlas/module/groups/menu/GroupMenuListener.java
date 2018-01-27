package net.avicus.atlas.module.groups.menu;

import net.avicus.atlas.event.match.MatchOpenEvent;
import net.avicus.atlas.event.player.PlayerJoinDelayedEvent;
import net.avicus.atlas.event.player.PlayerSpawnBeginEvent;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.elimination.EliminationModule;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.states.StatesModule;
import net.avicus.atlas.util.AtlasTask;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class GroupMenuListener implements Listener {

  private final Match match;
  private final GroupsModule module;

  public GroupMenuListener(GroupsModule module) {
    this.match = module.getMatch();
    this.module = module;
  }

  private void openGroupMenu(Player player) {
    GroupMenu menu = new GroupMenu(player, this.match);
    menu.open();
  }

  @EventHandler
  public void onSpawn(PlayerSpawnBeginEvent event) {
    if (!this.module.getSpectators().equals(event.getGroup())) {
      return;
    }

    if (!event.isGiveLoadout()) {
      return;
    }

    Player player = event.getPlayer();
    player.getInventory().addItem(GroupMenu.createMenuOpener(player));
  }

  @EventHandler
  public void onPlayerJoinDelayed(PlayerJoinDelayedEvent event) {
    StatesModule states = this.match.getRequiredModule(StatesModule.class);
    boolean isElimination = this.match.hasModule(EliminationModule.class);

    // Can't join during cycling phase
    if (states.isCycling()) {
      return;
    }

    // Can't join during elim match
    if (states.isPlaying() && isElimination) {
      return;
    }

    openGroupMenu(event.getPlayer());
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onMatchOpen(MatchOpenEvent event) {
    // Delay so as to allow players to load the regions and tp to world first
    AtlasTask.of(() -> {
      this.match.getPlayers().forEach(this::openGroupMenu);
    }).later(5);
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    if (event.getAction() != Action.RIGHT_CLICK_AIR
        && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    if (!GroupMenu.isMenuOpener(event.getItem())) {
      return;
    }

    openGroupMenu(event.getPlayer());
  }
}
