package net.avicus.atlas.component.network;

import javax.annotation.Nullable;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.map.AtlasMap;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.states.StatesModule;
import net.avicus.magma.Magma;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.module.Module;
import net.avicus.magma.network.server.ServerStatus;

public class StatusComponent implements Module, Runnable {

  private Atlas atlas = Atlas.get();

  @Override
  public void enable() {
    this.atlas.getServer().getScheduler().runTaskTimerAsynchronously(atlas, this, 0, 20 * 3);
  }

  @Override
  public void run() {
    @Nullable final Match match = this.atlas.getMatchManager().getRotation().getMatch();
    if (match == null) {
      return;
    }

    final StatesModule sm = match.getRequiredModule(StatesModule.class);
    final GroupsModule gm = match.getRequiredModule(GroupsModule.class);
    final AtlasMap map = match.getMap();

    ServerStatus.State state = ServerStatus.State.DEFAULT;
    if (sm.isStarting()) {
      state = ServerStatus.State.STARTING;
    } else if (sm.isPlaying()) {
      state = ServerStatus.State.PLAYING;
    } else if (sm.isCycling()) {
      state = ServerStatus.State.CYCLING;
    }

    int spectating = 0;
    int count = 0;
    int max = 0;
    for (Group group : gm.getGroups()) {
      if (group.isSpectator()) {
        spectating += group.getMembers().size();
      } else {
        count += group.getMembers().size();
        max += group.getMaxPlayers();
      }
    }

    Server server = Magma.get().localServer();
    server.setState(state.name());
    server.setActiveMap(map.getName());
    server.setSpectators(spectating);
    server.setMaxPlayers(max);
    server.setPlayers(count);
  }
}
