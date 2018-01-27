package net.avicus.atlas.component.network;

import java.util.Date;
import javax.annotation.Nullable;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.elimination.EliminationModule;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.module.states.StatesModule;
import net.avicus.atlas.util.AtlasTask;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.countdown.Countdown;
import net.avicus.compendium.countdown.CountdownStartEvent;
import net.avicus.compendium.countdown.RestartingCountdown;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.compendium.settings.PlayerSettings;
import net.avicus.compendium.settings.Setting;
import net.avicus.compendium.settings.types.SettingTypes;
import net.avicus.magma.Magma;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.module.ListenerModule;
import net.avicus.magma.network.server.Servers;
import net.avicus.magma.network.server.qp.PlayerRequestHandler;
import net.avicus.magma.network.server.qp.QuickPlay;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.scheduler.BukkitTask;

public class AtlasQuickPlayComponent implements ListenerModule {

  public static final Setting<Boolean> SETTING = new Setting<>(
      "hub-on-restart",
      SettingTypes.BOOLEAN,
      false,
      Messages.SETTINGS_HUB_RESTART,
      Messages.SETTINGS_HUB_RESTART_SUMMARY
  );
  private BukkitTask task;

  @Override
  public void enable() {
    PlayerSettings.register(SETTING);
    this.task = Atlas.get().getServer().getScheduler().runTaskTimer(Atlas.get(), () -> {
      Match match = Atlas.getMatch();
      if (match == null) {
        return;
      }

      StatesModule states = match.getRequiredModule(StatesModule.class);
      GroupsModule groups = match.getRequiredModule(GroupsModule.class);

      boolean starting = states.isStarting();
      boolean playing = states.isPlaying();

      // Cycling, no request for players
      if (!starting && !playing) {
        return;
      }

      // Don't request during elimination matches.
      if (playing && match.getModule(EliminationModule.class).isPresent()) {
        return;
      }

      int playersNeeded = 0;
      if (starting) {
        for (Group group : groups.getGroups()) {
          if (group.isSpectator()) {
            continue;
          }
          playersNeeded += Math.max(0, group.getMinPlayers() - group.size());
        }
      }

      int slotsAvailable = 0;
      for (Group group : groups.getGroups()) {
        if (group.isSpectator()) {
          continue;
        }
        slotsAvailable += Math.max(0, group.getMaxPlayers() - group.size());
      }

      Date requestExpiration = new Date(System.currentTimeMillis() + 1000 * 10);

      PlayerRequestHandler.PlayerRequestMessage message = new PlayerRequestHandler.PlayerRequestMessage(
          Magma.get().localServer(),
          playersNeeded,
          slotsAvailable,
          requestExpiration
      );
      Magma.get().getRedis().publish(message);
    }, 0, 20 * 10);

    this.task = Atlas.get().getServer().getScheduler().runTaskTimer(Atlas.get(), () -> {
      Match match = Atlas.getMatch();

      if (match == null) {
        return;
      }

      for (Player player : Bukkit.getOnlinePlayers()) {
        Group group = match.getRequiredModule(GroupsModule.class).getGroup(player);
        if (!group.isSpectator()) {
          continue;
        }

        PlayerRequestHandler.PlayerRequestMessage request = QuickPlay.highestRequest(player)
            .orElse(null);

        if (request == null) {
          continue;
        }

        boolean localServer = request.getServer().getId() == Magma.get().localServer().getId();

        Localizable message;

        if (localServer) {
          message = Messages.GENERIC_LOCAL_MATCH.with();
        } else {
          Localizable serverName = new UnlocalizedText(request.getServer().getName(),
              TextStyle.ofBold());

          if (request.getPlayersNeeded() > 0) {
            Localizable neededNumber = new LocalizedNumber(request.getPlayersNeeded());

            if (request.getPlayersNeeded() == 1) {
              message = Messages.GENERIC_REMOTE_MATCH_WAITING.with(serverName, neededNumber);
            } else {
              message = Messages.GENERIC_REMOTE_MATCH_WAITING_PLURAL.with(serverName, neededNumber);
            }
          } else if (request.getSlotsAvailable() > 0) {
            Localizable availableNumber = new LocalizedNumber(request.getSlotsAvailable());

            if (request.getPlayersNeeded() == 1) {
              message = Messages.GENERIC_REMOTE_MATCH.with(serverName, availableNumber);
            } else {
              message = Messages.GENERIC_REMOTE_MATCH_PLURAL.with(serverName, availableNumber);
            }
          } else {
            return;
          }
          message.style().click(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
              "/server " + request.getServer().getName()));
        }

        message.style().color(ChatColor.YELLOW);

        player.sendMessage(message);
      }
    }, 20 * 10, 20 * 60);
  }

  @Override
  public void disable() {
    PlayerSettings.unregister(SETTING);
    if (this.task != null) {
      this.task.cancel();
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onRestartBegin(CountdownStartEvent event) {
    Countdown countdown = event.getStarted();

    if (!(countdown instanceof RestartingCountdown)) {
      return;
    }

    // We do this in batches to prevent killing other servers.
    // We have ~25 seconds to do this and we need to group players in equal batches.
    // Each batch is 5 players.
    // The formula we use for this is s/(p/5) where s is the # of seconds (20) and p is the number of players online.

    int sleep = (int) Math.ceil(20 / Math.min(1, Bukkit.getOnlinePlayers().size() / 5));

    AtlasTask.of(() -> {
      int count = 0;
      for (Player player : Bukkit.getOnlinePlayers()) {
        if (!player.isOnline()) {
          continue;
        }

        if (transitionToTarget(player)) {
          count++;
        }

        if (count >= 5) {
          count = 0;
          try {
            Thread.sleep(sleep * 1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }).nowAsync();
  }

  private boolean transitionToTarget(Player player) {
    if (PlayerSettings.get(player, AtlasQuickPlayComponent.SETTING)) {
      // The player does not wish to be sent to their quick play target. Boo.
      return false;
    }

    @Nullable final PlayerRequestHandler.PlayerRequestMessage request = QuickPlay
        .highestRequest(player).orElse(null);
    if (request == null) {
      // A quick play request was not found for the player.
      return false;
    }

    final Server server = request.getServer();
    if (server.isLocal() || request.getSlotsAvailable() <= 0) {
      // It would be silly to try and send the player to the same
      // server, or to a server with no room for them.
      return false;
    }

    player.sendMessage(Messages.GENERIC_SENT_BY_RESTART.with(ChatColor.AQUA, server.getName()));
    Servers.connect(player, server, false, true);
    return true;
  }
}
