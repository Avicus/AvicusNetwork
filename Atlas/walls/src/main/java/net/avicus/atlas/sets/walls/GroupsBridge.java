package net.avicus.atlas.sets.walls;

import net.avicus.atlas.event.player.PlayerJoinDelayedEvent;
import net.avicus.atlas.module.ModuleBridge;
import net.avicus.atlas.module.groups.CompetitorRule;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.groups.ffa.FFAModule;
import net.avicus.atlas.module.groups.ffa.FFATeam;
import net.avicus.atlas.module.groups.menu.GroupMenuListener;
import net.avicus.atlas.module.groups.teams.TeamsModule;
import net.avicus.atlas.module.states.StatesModule;
import net.avicus.atlas.util.Events;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GroupsBridge implements ModuleBridge<GroupsModule>, Listener {

  private final GroupsModule module;
  private final StatesModule statesModule;
  private final GroupMenuListener listener;

  public GroupsBridge(TeamsModule module) {
    this((GroupsModule) module);
  }

  public GroupsBridge(FFAModule module) {
    this((GroupsModule) module);
  }

  public GroupsBridge(GroupsModule module) {
    this.module = module;
    this.statesModule = module.getMatch().getRequiredModule(StatesModule.class);
    this.listener = new GroupMenuListener(module);
  }

  @Override
  public void onOpen(GroupsModule module) {
    if (module.getCompetitorRule() == CompetitorRule.TEAM) {
      Events.register(this.listener);
    } else {
      Events.register(this);
    }
  }

  @Override
  public void onClose(GroupsModule module) {
    if (module.getCompetitorRule() == CompetitorRule.TEAM) {
      Events.unregister(this.listener);
    } else {
      Events.unregister(this);
    }
  }

  @EventHandler
  public void onJoin(PlayerJoinDelayedEvent event) {
    if (module.getCompetitorRule() == CompetitorRule.INDIVIDUAL) {
      FFATeam team = ((FFAModule) module).getTeam();
      if (!team.isFull(event.getPlayer())) {
        module.changeGroup(event.getPlayer(), team, false, false);
      }
    }
  }
}
