package net.avicus.atlas.component.visual;

import java.util.ArrayList;
import java.util.List;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.event.objective.ObjectiveTouchEvent;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.groups.Spectators;
import net.avicus.atlas.module.states.StatesModule;
import net.avicus.atlas.util.AtlasTask;
import net.avicus.atlas.util.Events;
import net.avicus.compendium.sound.SoundEvent;
import net.avicus.compendium.sound.SoundLocation;
import net.avicus.compendium.sound.SoundType;
import net.avicus.magma.module.ListenerModule;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;


public class SoundComponent implements ListenerModule {

  @Override
  public void enable() {

  }

  public void objectiveComplete() {
    // Run a second later to check if match has ended.
    AtlasTask.of(() -> {
      if (Atlas.getMatch() == null || !Atlas.getMatch().getRequiredModule(StatesModule.class)
          .isPlaying()) {
        return;
      }

      Bukkit.getOnlinePlayers().forEach((p) -> {
        SoundEvent call = Events
            .call(new SoundEvent(p, SoundType.WITHER_SHOOT, SoundLocation.OBJECTIVE_COMPLETE));
        call.getSound().play(p, 1.4F);
      });
    }).later(20);
  }

  @EventHandler
  public void onTouch(ObjectiveTouchEvent event) {
    GroupsModule groups = Atlas.getMatch().getRequiredModule(GroupsModule.class);
    Group playerGroup = groups.getGroup(event.getPlayer());
    Spectators spectators = groups.getSpectators();

    List<Player> toMessage = new ArrayList<>();
    toMessage.addAll(playerGroup.getPlayers());
    toMessage.addAll(spectators.getPlayers());

    toMessage.forEach((player -> {
      SoundEvent call = Events
          .call(new SoundEvent(player, SoundType.LAUNCH, SoundLocation.OBJECTIVE_TOUCH));
      call.getSound().play(player, 1.3F);
    }));
  }
}
