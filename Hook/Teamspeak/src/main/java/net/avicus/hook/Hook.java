package net.avicus.hook;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.exception.TS3Exception;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import net.avicus.compendium.config.ConfigFile;
import net.avicus.hook.client.Clients;
import net.avicus.hook.group.Groups;
import net.avicus.hook.temp.TemporaryChannels;
import net.avicus.hook.wrapper.ConfirmableTeamSpeakCommand;
import net.avicus.hook.wrapper.HookClient;
import net.avicus.hook.wrapper.TeamSpeakCommand;
import net.avicus.magma.database.Database;
import net.avicus.magma.redis.Redis;
import net.avicus.quest.database.DatabaseConfig;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class Hook {

  @Getter
  private static final PeriodFormatter periodFormatter = new PeriodFormatterBuilder()
      .appendDays().appendSuffix("d")
      .appendHours().appendSuffix("h")
      .appendMinutes().appendSuffix("m")
      .appendSecondsWithOptionalMillis().appendSuffix("s")
      .appendSeconds()
      .toFormatter();

  @Getter
  private final Logger log;
  private final ConfigFile config;
  @Getter
  private final Database database;
  @Getter
  private final Redis redis;
  @Getter
  private TS3Api api;
  @Getter
  private TS3ApiAsync apiAsync;

  @Getter
  private HashMap<String, TeamSpeakCommand> commands = new HashMap<>();
  @Getter
  private HashMap<String, ConfirmableTeamSpeakCommand> confirmableCommands = new HashMap<>();

  public Hook(ConfigFile config, ConfigFile dbConfig) {
    this.log = Main.getLogger("Startup");
    this.config = config;

    dbConfig.injector(DbConfig.class).inject();
    DatabaseConfig databaseConfig = DbConfig.MySQLConfig.create();
    database = connectDatabase(databaseConfig);

    // Redis
    redis = DbConfig.RedisConfig.create().build();
    try {
      redis.enable();
    } catch (IllegalStateException e) {
      e.printStackTrace();
    }
  }

  private Database connectDatabase(DatabaseConfig config) {
    this.log.info("Establishing new database connection...");
    Database database = new Database(config);
    database.connect();
    this.log.info("Connected.");
    return database;
  }

  public void start() {
    this.log.info("TS Bot starting up!");

    this.config.injector(HookConfig.class).inject();
    this.log.info("Configuration loaded");

    this.log.info("Connecting to hook");
    connectTeamspeak();

    Groups.init();
    TemporaryChannels.init();
    Clients.init();
    commands();

    this.goHome();
  }

  private void commands() {
    commands.put("help", (sender, args) -> {
      sender.message("---- TS Help ----");
      sender.message("!help - View this message");
      if (sender.hasPermission("ranks.temp")) {
        sender.message(
            "!tempassign [username] [rankId] [duration] - Temporarily assign a user to a group.");
      }
    });

    getApiAsync().registerEvent(TS3EventType.TEXT_PRIVATE);
    getApiAsync().addTS3Listeners(new TS3EventAdapter() {
      @Override
      public void onTextMessage(TextMessageEvent e) {
        String clId = e.get("invokeruid");

        if (e.getTargetMode() == TextMessageTargetMode.CLIENT && !clId
            .equals(getApi().whoAmI().get("client_unique_identifier"))) {
          List<String> args = new ArrayList<String>(
              Arrays.asList(e.getMessage().replace("!", "").split(" ")));

          HookClient client = Clients.getClientByUid(clId);
          if (client == null) {
            Clients.messageClient(clId, "You must be registered to perform commands!");
            return;
          }

          getLog().info(args.get(0));

          TeamSpeakCommand command = commands.get(args.get(0));
          ConfirmableTeamSpeakCommand confirmableCommand = confirmableCommands.get(args.get(0));

          if (command != null) {
            try {
              if (args.size() > 1) {
                args.remove(0);
              }
              command.execute(client, args);
            } catch (Exception ex) {
              ex.printStackTrace();
              client.message("Error: " + ex.getMessage());
            }
          } else if (confirmableCommand != null) {
            try {
              if (args.size() > 1) {
                args.remove(0);
              }
              if (confirmableCommand.doPendingChecks(client)) {
                confirmableCommand.execute(client, args);
              }
            } catch (Exception ex) {
              ex.printStackTrace();
              client.message("Error: " + ex.getMessage());
            }
          } else {
            client.message("Command not found!\nUse !help for a list of commands.");
          }
        }
      }
    });
  }

  private void connectTeamspeak() {
    try {
      final TS3Config config = new TS3Config();
      config.setHost(HookConfig.Connection.getHost());
      config.setQueryPort(HookConfig.Connection.getPort());
      config.setDebugLevel(Level.OFF);
      config.setFloodRate(TS3Query.FloodRate.UNLIMITED);

      final TS3Query query = new TS3Query(config);
      query.connect();

      final TS3Api api = query.getApi();
      api.login(HookConfig.Connection.getUsername(), HookConfig.Connection.getPassword());
      api.selectVirtualServerById(HookConfig.Connection.getServer());
      api.setNickname(HookConfig.Connection.getNickname());

      this.api = query.getApi();
      this.apiAsync = query.getAsyncApi();
    } catch (TS3Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public void goHome() {
    api.moveQuery(api.getChannelByNameExact("Lobby/Hub Channels", true));
  }
}
