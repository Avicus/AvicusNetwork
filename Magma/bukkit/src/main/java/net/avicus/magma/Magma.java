package net.avicus.magma;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandNumberFormatException;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.minecraft.util.commands.CommandUsageException;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Getter;
import net.avicus.compendium.AvicusCommandsManager;
import net.avicus.compendium.commands.AvicusCommandsRegistration;
import net.avicus.compendium.commands.exception.AbstractTranslatableCommandException;
import net.avicus.compendium.config.Config;
import net.avicus.compendium.config.ConfigFile;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.magma.alerts.Alerts;
import net.avicus.magma.announce.Announce;
import net.avicus.magma.api.API;
import net.avicus.magma.api.APIClient;
import net.avicus.magma.channel.ChannelManager;
import net.avicus.magma.channel.distributed.DistributedChannels;
import net.avicus.magma.channel.premium.Premium;
import net.avicus.magma.channel.report.Reports;
import net.avicus.magma.channel.staff.StaffChannels;
import net.avicus.magma.command.GenericCommands;
import net.avicus.magma.command.exception.PremiumCommandPermissionsException;
import net.avicus.magma.database.Database;
import net.avicus.magma.database.model.impl.PrestigeSeason;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.database.model.impl.ServerCategory;
import net.avicus.magma.database.table.impl.ServerTable;
import net.avicus.magma.item.LockingSharingListener;
import net.avicus.magma.module.ModuleManager;
import net.avicus.magma.module.freeze.FreezeModule;
import net.avicus.magma.module.gadgets.Gadgets;
import net.avicus.magma.module.prestige.PrestigeModule;
import net.avicus.magma.network.NetworkConstants;
import net.avicus.magma.network.rtp.RemoteTeleports;
import net.avicus.magma.network.server.ServerModule;
import net.avicus.magma.network.server.ServerStatus;
import net.avicus.magma.network.server.Servers;
import net.avicus.magma.network.user.Users;
import net.avicus.magma.redis.Redis;
import net.avicus.magma.restart.RestartMessageHandler;
import net.avicus.magma.util.MagmaTask;
import net.avicus.magma.util.properties.BlockPropStore;
import net.avicus.quest.database.DatabaseConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.joda.time.Instant;

/**
 * This is Magma.
 */
public final class Magma extends JavaPlugin {

  /**
   * The magma instance.
   */
  private static Magma magma;
  /**
   * The channel manager.
   */
  private ChannelManager channelManager;
  @Getter
  private Redis redis;
  private Database database;
  @Getter
  private API apiClient;
  private Server localServer;
  private ServerCategory localCategory;
  private AvicusCommandsManager commands;
  @Getter
  private ModuleManager mm;
  @Getter
  private PrestigeSeason currentSeason;
  private AtomicBoolean ready = new AtomicBoolean(false);

  public Magma() {
    magma = this;
  }

  /**
   * Gets the instance of magma.
   *
   * @return the magma instance
   */
  public static Magma get() {
    return magma;
  }

  // do not remove this. trust me. bad things will happen.
  private void preloadClasses() {
    Instant.class.getName();
    ServerStatus.State.class.getName();
  }

  @Override
  public void onEnable() {
    Bukkit.getPluginManager().registerEvents(new Listener() {
      @EventHandler(priority = EventPriority.LOWEST)
      public void onPlayerLogin(PlayerLoginEvent event) {
        if (ready.get())
          return;
        String error = ChatColor.RED + "The server has not loaded, please rejoin in a moment.";
        event.disallow(PlayerLoginEvent.Result.KICK_OTHER, error);
      }
    }, this);
    this.preloadClasses();

    this.saveDefaultConfig();
    this.reloadConfig();

    Config config = new ConfigFile(new File(getDataFolder(), "config.yml"));
    config.injector(MagmaConfig.class).inject();

    this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    this.getServer().getMessenger()
        .registerOutgoingPluginChannel(this, NetworkConstants.CONNECT_CHANNEL);

    this.channelManager = new ChannelManager();

    this.initServices();

    this.loadLocalServer();

    this.commands = new AvicusCommandsManager();
    final AvicusCommandsRegistration registrar = new AvicusCommandsRegistration(this,
        this.commands);
    this.mm = new ModuleManager(this.getServer().getPluginManager(), this, registrar);
    registrar.register(GenericCommands.class);
    this.registerModules();
    this.mm.enable();
    Servers.init(registrar);
    Alerts.init(registrar);

    Bukkit.getPluginManager().registerEvents(new LockingSharingListener(), this);
    BlockPropStore.initBlocks();
    BlockPropStore.initTools();

    getRedis().register(new RestartMessageHandler());
    Users.init(registrar);

    MagmaTask.of(() -> ready.set(true)).later(40);
  }

  private void registerModules() {
    this.mm.register(ServerModule.class);
    this.mm.register(RemoteTeleports.class);
    if (MagmaConfig.Channel.isEnabled()) {
      this.mm.register(DistributedChannels.class);
      this.mm.register(StaffChannels.class, MagmaConfig.Channel.Staff.isEnabled());
      this.mm.register(Reports.class, MagmaConfig.Channel.Report.isEnabled());
      this.mm.register(Premium.class);
    }
    this.mm.register(Announce.class);
    this.mm.register(FreezeModule.class, MagmaConfig.Freeze.isEnabled());
    this.database().getSeasons().findCurrentSeason().ifPresent(season -> {
      this.currentSeason = season;
      this.mm.register(PrestigeModule.class);
    });
    this.mm.register(Gadgets.class);
  }

  private void initServices() {
    final Configuration config = this.getConfig();

    getLogger().info("Connecting to Redis...");
    final Redis.Builder builder = Redis.builder(config.getString("redis.hostname"))
        .database(config.getInt("redis.database"));
    if (config.getBoolean("redis.auth.enabled")) {
      builder.password(config.getString("redis.auth.password"));
    }
    this.redis = builder.build();
    try {
      this.redis.enable();
    } catch (IllegalStateException e) {
      e.printStackTrace();
      this.getServer().getPluginManager().disablePlugin(this);
      Bukkit.shutdown();
      return;
    }
    getLogger().info("Connected to Redis!");

    getLogger().info("Connecting to API...");
    try {
      this.apiClient = new API(new APIClient(MagmaConfig.API.getUrl(), MagmaConfig.API.getKey()));
    } catch (Exception e) {
      getLogger().severe("Failed to connect to API!");
      e.printStackTrace();
      this.getPluginLoader().disablePlugin(this);
      Bukkit.shutdown();
      return;
    }
    getLogger().info("Connected to API!");

    getLogger().info("Connecting to database...");
    this.database = new Database(DatabaseConfig.builder(
        config.getString("database.hostname"),
        config.getString("database.database"),
        config.getString("database.auth.username"),
        config.getString("database.auth.password")
    ).reconnect(true).build());
    try {
      this.database.enable();
    } catch (IllegalStateException e) {
      e.printStackTrace();
      this.getServer().getPluginManager().disablePlugin(this);
      Bukkit.shutdown();
    }
    getLogger().info("Connected to database!");
  }

  @Override
  public void onDisable() {
    if (this.mm != null) {
      this.mm.disable();
    }

    if (this.localServer != null) {
      Servers.syncUp(this.redis, new ServerStatus(this.localServer));
    }
  }

  private void loadLocalServer() {
    String folder = getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getName();
//            String name = HookConfig.Server.getName().orElse(folder);
    // Todo
    String name = folder;
    final ServerTable servers = Magma.get().database().getServers();
    Optional<Server> server = servers.findByName(name);

    String host = getServer().getIp();
    int port = getServer().getPort();

    if (host == null || host.length() == 0 ||
        host.startsWith("0.0" // localhost (need real IP)
        )) {
      try {
        host = InetAddress.getLocalHost().getHostAddress();
      } catch (UnknownHostException e) {
        e.printStackTrace();
        setEnabled(false);
        return;
      }
    }

    if (server.isPresent()) {
      this.localServer = server.get();
      this.localCategory = server.get().getCategory(database().getServerCategories()).orElse(null);

      // update ip/port
      servers.update().set("host", host).set("port", port).where("id", server.get().getId())
          .execute();
    } else {
      Server created = new Server(name, host, port, false);
      this.localServer = created;

      // insert new server
      servers.insert(created).execute();
    }

    Server.local = this.localServer;
  }

  /**
   * Gets the channel manager.
   *
   * @return the channel manager
   */
  public ChannelManager getChannelManager() {
    return this.channelManager;
  }

  public Database database() {
    return this.database;
  }

  public Server localServer() {
    return this.localServer;
  }

  public ServerCategory localCategory() {
    return this.localCategory;
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
    } catch (PremiumCommandPermissionsException e) {
      sender.sendMessage(e.asTranslatable());
    } catch (CommandPermissionsException e) {
      if (PremiumCommandPermissionsException.PREMIUM_COMMANDS.contains(cmd.getName())) {
        sender.sendMessage(PremiumCommandPermissionsException.MESSAGE);
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
