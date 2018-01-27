package net.avicus.mars;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.minecraft.util.commands.CommandUsageException;
import com.sk89q.minecraft.util.commands.CommandsManager;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.countdown.StartingCountdown;
import net.avicus.compendium.config.Config;
import net.avicus.magma.Magma;
import net.avicus.magma.database.model.impl.Team;
import net.avicus.magma.database.model.impl.TeamMember;
import net.avicus.magma.database.model.impl.Tournament;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.network.user.Users;
import net.avicus.mars.managed.ManagedCommands;
import net.avicus.mars.managed.ManagedEventManager;
import net.avicus.mars.scrimmage.ScrimmageCommands.ScrimmageParentCommand;
import net.avicus.mars.scrimmage.ScrimmageManager;
import net.avicus.mars.tournament.ConditionalStartCountdown;
import net.avicus.mars.tournament.TournamentCommands;
import net.avicus.mars.tournament.TournamentManager;
import net.avicus.mars.tournament.TournamentMatch;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class MarsPlugin extends JavaPlugin implements Listener, Runnable {

  @Getter
  private static MarsPlugin instance;

  private CommandsManager<CommandSender> commands;
  private EventManager<?> events;

  @Override
  public void onEnable() {
    instance = this;

    Config config = config();
    config.injector(MarsConfig.class).inject();

    if (MarsConfig.Tournament.isEnabled()) {
      Tournament tournament = Magma.get().database().getTournaments().select()
          .where("id", MarsConfig.Tournament.getId()).execute().first();
      if (tournament == null) {
        Bukkit.getLogger().severe("Tournament not found!");
        Bukkit.shutdown();
      }

      this.events = new TournamentManager(tournament);

      TournamentManager manager = (TournamentManager) this.events;
      manager.getMatches().add(new TournamentMatch(manager.getTournament(), Atlas.getMatch()));

      StartingCountdown starting = new ConditionalStartCountdown(Atlas.getMatch(),
          manager.getCurrentEvent().get());
      Atlas.get().getMatchManager().getRotation().startMatch(starting);
    } else if (MarsConfig.isScrimmageEnabled()) {
      this.events = new ScrimmageManager(this);
    } else {
      Bukkit.getLogger()
          .info("Neither scrimmage nor tournament is enabled, defaulting to managed mode.");
      this.events = new ManagedEventManager();
    }

    this.events.start();

    getServer().getPluginManager().registerEvents(this, this);

    getServer().getScheduler().runTaskTimer(this, this, 0, 20);
    getServer().getScheduler().runTaskTimer(this, new EventAlertTask(), 0, 20);

    registerCommands();
  }

  public Optional<CompetitiveEvent> getCurrentEvent() {
    return Optional.ofNullable(this.events.getCurrentEvent().orElse(null));
  }

  /**
   * Kick players that should not be here (teams update dynamically).
   */
  public void cleanServer() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (!canJoin(player)) {
        Bukkit.getServer().getScheduler().runTaskLater(MarsPlugin.getInstance(), () -> {
          player.kickPlayer(ChatColor.RED + "You are not allowed to participate in this event.");
        }, 5);
      }
    }
  }

  private void registerCommands() {
    this.commands = new CommandsManager<CommandSender>() {
      @Override
      public boolean hasPermission(CommandSender sender, String permission) {
        return sender.isOp() || sender.hasPermission(permission);
      }
    };

    CommandsManagerRegistration cmds = new CommandsManagerRegistration(this, this.commands);

    cmds.register(ScrimmageParentCommand.class);
    cmds.register(ManagedCommands.ManagedParentCommand.class);
    cmds.register(TournamentCommands.TournamentParentCommand.class);
  }


  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    try {
      this.commands.execute(cmd.getName(), args, sender, sender);
    } catch (CommandPermissionsException e) {
      sender.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
    } catch (CommandUsageException e) {
      sender.sendMessage(ChatColor.RED + "Invalid arguments: " + e.getUsage());
    } catch (CommandException e) {
      sender.sendMessage(ChatColor.RED + "An error occurred. Please try again.");
    }
    return true;
  }

  @Override
  public void run() {
    cleanServer();
  }

  private boolean canJoin(Player player) {
    CompetitiveEvent current = this.events.getCurrentEvent().orElse(null);
    User user = Users.user(player);

    if (current == null || !current.canJoinServer(user)) {
      return player.hasPermission("mars.join");
    }

    return true;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerJoin(PlayerJoinEvent event) {
    cleanServer();
  }

  public Optional<MarsTeam> createTeam(int id) {
    Team team = Magma.get().database().getTeams().findById(id).orElse(null);
    if (team == null) {
      return Optional.empty();
    }
    return Optional.of(createTeam(team));
  }

  public Optional<MarsTeam> createTeam(String query) {
    Team team = Magma.get().database().getTeams().findByTagOrTitle(query).orElse(null);
    if (team == null) {
      return Optional.empty();
    }
    return Optional.of(createTeam(team));
  }

  public MarsTeam createTeam(Team team) {
    List<TeamMember> members = Magma.get().database().getTeamMembers()
        .findByTeam(team.getId(), true);
    return new MarsTeam(team, members);
  }

  private Config config() {
    File configFile = new File(getDataFolder(), "config.yml");

    try {
      this.saveDefaultConfig();
      this.reloadConfig();
      return new Config(new FileInputStream(configFile));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
