package net.avicus.atlas.sets.competitve.objectives.bridges;

import net.avicus.atlas.event.match.MatchOpenEvent;
import net.avicus.atlas.event.player.PlayerJoinDelayedEvent;
import net.avicus.atlas.module.ModuleBridge;
import net.avicus.atlas.module.groups.ffa.FFAModule;
import net.avicus.atlas.module.groups.ffa.FFATeam;
import net.avicus.atlas.module.groups.menu.GroupMenuListener;
import net.avicus.atlas.module.groups.teams.TeamsModule;
import net.avicus.atlas.module.states.StatesModule;
import net.avicus.atlas.util.AtlasTask;
import net.avicus.atlas.util.Events;
import net.avicus.atlas.util.Messages;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class GroupsBridge {

  public static class TeamsBridge implements ModuleBridge<TeamsModule> {

    private final GroupMenuListener listener;

    public TeamsBridge(TeamsModule module) {
      this.listener = new GroupMenuListener(module);
    }

    @Override
    public void onOpen(TeamsModule module) {
      Events.register(this.listener);
    }

    @Override
    public void onClose(TeamsModule module) {
      Events.unregister(this.listener);
    }
  }

  public static class FFABridge implements ModuleBridge<FFAModule>, Listener {

    private final StatesModule statesModule;
    private final FFAModule ffa;

    public FFABridge(FFAModule module) {
      this.statesModule = module.getMatch().getRequiredModule(StatesModule.class);
      this.ffa = module;
    }

    @Override
    public void onOpen(FFAModule module) {
      Events.register(this);
    }

    @Override
    public void onClose(FFAModule module) {
      Events.unregister(this);
    }

    @EventHandler
    public void onJoin(PlayerJoinDelayedEvent event) {
      if (this.statesModule.isStarting()) {
        join(event.getPlayer());
      }
    }

    private void join(Player p) {
      FFATeam team = this.ffa.getTeam();
      if (!team.isFull(p)) {
        this.ffa.changeGroup(p, team, false, false);
        p.sendMessage(
            Messages.GENERIC_JOINED
                .with(team.getName().toText(team.getChatColor())));
      }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMatchOpen(MatchOpenEvent event) {
      // Delay so as to allow players to load the regions and tp to world first
      AtlasTask.of(() -> {
        this.ffa.getMatch().getPlayers().forEach(this::join);
      }).later(5);
    }
  }
}
