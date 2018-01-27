package net.avicus.hook;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandNumberFormatException;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.minecraft.util.commands.CommandUsageException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import lombok.Getter;
import net.avicus.compendium.AvicusCommandsManager;
import net.avicus.compendium.commands.AvicusCommandsRegistration;
import net.avicus.compendium.commands.exception.AbstractTranslatableCommandException;
import net.avicus.compendium.config.Config;
import net.avicus.compendium.config.ConfigFile;
import net.avicus.compendium.locale.LocaleBundle;
import net.avicus.compendium.locale.LocaleStrings;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.hook.achievements.Achievements;
import net.avicus.hook.afk.AFKKickTask;
import net.avicus.hook.backend.Backend;
import net.avicus.hook.chat.Chat;
import net.avicus.hook.chat.MessageCommands;
import net.avicus.hook.commands.DevCommands;
import net.avicus.hook.commands.OnlineCommand;
import net.avicus.hook.commands.StatsCommand;
import net.avicus.hook.commands.TeleportCommands;
import net.avicus.hook.commands.UserCommands;
import net.avicus.hook.credits.Credits;
import net.avicus.hook.friends.Friends;
import net.avicus.hook.gadgets.HookGadgets;
import net.avicus.hook.gadgets.types.crates.CrateRewardListener;
import net.avicus.hook.gadgets.types.map.GadgetPopulationUtility;
import net.avicus.hook.listener.AtlasListener;
import net.avicus.hook.listener.BackpackListener;
import net.avicus.hook.listener.SettingModule;
import net.avicus.hook.prestige.ExperienceRewardListener;
import net.avicus.hook.punishment.Punishments;
import net.avicus.hook.rate.MapRatings;
import net.avicus.hook.sessions.Sessions;
import net.avicus.hook.tracking.Tracking;
import net.avicus.hook.utils.Events;
import net.avicus.hook.utils.HookTask;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.Magma;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.module.prestige.PrestigeModule;
import net.avicus.magma.network.server.qp.QuickPlay;
import net.avicus.magma.network.user.Users;
import net.avicus.magma.network.user.rank.Ranks;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class HookPlugin extends JavaPlugin {

  private static final List<String> PREMIUM_COMMANDS = Arrays.asList(
      "rtp",
      "namehistory"
  );
  @Getter
  private static HookPlugin instance;
  @Getter
  private LocaleBundle localeBundle;
  @Getter
  private Server avicusServer;
  private HookModuleManager hmm;
  private AvicusCommandsManager commands;

  @Override
  public void onEnable() {
    instance = this;

    this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

    // Main Config
    {
      this.saveDefaultConfig();
      this.reloadConfig();

      Config config = new ConfigFile(new File(getDataFolder(), "config.yml"));
      config.injector(HookConfig.class).inject();
    }

    // Database Config
    {
      InputStream local = getResource("db.yml");
      File remote = new File(getDataFolder(), "db.yml");
      if (!remote.exists()) {
        try {
          Files.copy(local, remote.toPath());
        } catch (IOException e) {
          e.printStackTrace();
          setEnabled(false);
          return;
        }
      }

      Config config = new ConfigFile(new File(getDataFolder(), "db.yml"));
      config.injector(DbConfig.class).inject();
    }

    try {
      List<LocaleStrings> strings = new ArrayList<>();
      strings.add(LocaleStrings.fromXml(getResource("locales/en_US.xml")));
      strings.add(LocaleStrings.fromXml(getResource("locales/es_ES.xml")));

      this.localeBundle = new LocaleBundle(strings, strings.get(0));
    } catch (Exception e) {
      e.printStackTrace();
      setEnabled(false);
      return;
    }

    // Commands
    this.commands = new AvicusCommandsManager();

    AvicusCommandsRegistration cmds = new AvicusCommandsRegistration(this, this.commands);
    cmds.register(OnlineCommand.class);
    cmds.register(StatsCommand.class);
    cmds.register(TeleportCommands.class);
    cmds.register(DevCommands.Parent.class);

    // Modules
    Chat.init();
    cmds.register(UserCommands.class);
    Ranks.init();
    Credits.init(cmds);
    Sessions.init(cmds);
    Punishments.init(cmds);
    Friends.init(cmds);
    HookGadgets.init(cmds);
    Backend.init(this);
    QuickPlay.init();

    Events.register(new AFKKickTask().start());
    Events.register(new BackpackListener());

    this.hmm = new HookModuleManager(this.getServer().getPluginManager(), this, cmds);
    this.hmm.register(SettingModule.class);
    this.hmm.register(MessageCommands.class);

    if (Hook.atlas()) {
      new HookAtlas();
      if (HookConfig.MapRatings.isEnabled()) {
        this.getServer().getPluginManager().registerEvents(new MapRatings(), this);
        cmds.register(MapRatings.class);
      }

      this.hmm.register(Achievements.class, HookConfig.Achievements.isEnabled());

      Events.register(new AtlasListener());

      if (HookConfig.Tracking.isDeaths() || HookConfig.Tracking.isObjectives()) {
        Tracking.init();
      }

      if (HookConfig.isRewardCrates()) {
        Events.register(new CrateRewardListener());
      }

      if (Magma.get().getMm().hasModule(PrestigeModule.class) && HookConfig.Experience.Rewards
          .isEnabled()) {
        Events.register(new ExperienceRewardListener());
      }

      if (HookConfig.getShutdownOnEmpty().orElse(true)) {
        HookTask.of(() -> {
          if (Bukkit.getOnlinePlayers().isEmpty()) {
            getLogger().info("Shutting down empty server to free up memory.");
            Bukkit.shutdown();
          }
        }).repeat(20 * 60 * 5, 20 * 10); // 5 minutes later, every 10 seconds
      }

      GadgetPopulationUtility.populate();
    }

    HookTask.of(() -> {
      if (Hook.lobby()) {
        // Random is here to prevent lobbies from shutting down at the same time.
        int start = Math.max(new Random().nextInt(60), 10);
        if (HookConfig.getShutdownOnEmpty().orElse(true)) {
          HookTask.of(() -> {
            if (Bukkit.getOnlinePlayers().isEmpty()) {
              getLogger().info("Shutting down empty server to free up memory.");
              Bukkit.shutdown();
            }
          }).repeat(20 * 60 * start, 20 * 10); // At least 10 minutes later, every 10 seconds
        }
      }
    }).later(30 * 20); // Just to make sure it is loaded

    this.hmm.enable();
  }

  @Override
  public void onDisable() {
    this.hmm.disable();

    if (Hook.server() != null) {
      Users.list().forEach(Friends::leave);
    }
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    try {
      this.commands.execute(cmd.getName(), args, sender, sender);
    } catch (AbstractTranslatableCommandException e) {
      sender.sendMessage(AbstractTranslatableCommandException.format(e));
    } catch (CommandNumberFormatException e) {
      sender.sendMessage(AbstractTranslatableCommandException
          .error(net.avicus.compendium.plugin.Messages.ERRORS_COMMAND_NUMBER_EXPECTED,
              new UnlocalizedText(e.getActualText())));
    } catch (CommandPermissionsException e) {
      if (PREMIUM_COMMANDS.contains(cmd.getName())) {
        sender.sendMessage(
            AbstractTranslatableCommandException.error(Messages.ERROR_NO_PERMISSION_PREMIUM));
      } else {
        sender.sendMessage(AbstractTranslatableCommandException
            .error(net.avicus.compendium.plugin.Messages.ERRORS_COMMAND_NO_PERMISSION));
      }
    } catch (CommandUsageException e) {
      sender.sendMessage(AbstractTranslatableCommandException
          .error(net.avicus.compendium.plugin.Messages.ERRORS_COMMAND_INVALID_USAGE,
              new UnlocalizedText(e.getUsage())));
    } catch (CommandException e) {
      sender.sendMessage(AbstractTranslatableCommandException
          .error(net.avicus.compendium.plugin.Messages.ERRORS_COMMAND_INTERNAL_ERROR));
      e.printStackTrace();
    }

    return true;
  }
}
