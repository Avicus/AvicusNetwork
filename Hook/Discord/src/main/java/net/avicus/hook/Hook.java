package net.avicus.hook;

import com.google.common.collect.Lists;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import lombok.Getter;
import net.avicus.compendium.StringUtil;
import net.avicus.compendium.config.ConfigFile;
import net.avicus.hook.commands.AltCheckCommand;
import net.avicus.hook.commands.Commands;
import net.avicus.hook.commands.CountCommand;
import net.avicus.hook.commands.PollCommand;
import net.avicus.hook.commands.PunishmentCommads;
import net.avicus.hook.commands.RegisterCommand;
import net.avicus.hook.commands.ReportCommand;
import net.avicus.hook.commands.UnRegisterCommand;
import net.avicus.hook.polling.DiscussionPollingService;
import net.avicus.hook.polling.PrestigeLevelPollingService;
import net.avicus.magma.database.Database;
import net.avicus.magma.redis.Redis;
import net.avicus.quest.database.DatabaseConfig;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.EmbedType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.impl.GuildImpl;
import net.dv8tion.jda.core.entities.impl.MessageEmbedImpl;
import okhttp3.OkHttpClient;
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
  private final ConfigFile dbConfig;
  @Getter
  private final Commands commands;
  @Getter
  private final UserManagementService userManagementService;
  @Getter
  private Database database;
  @Getter
  private Redis redis;

  @Getter
  private JDA jda;
  @Getter
  private GuildImpl mainGuild;
  @Getter
  private GuildImpl tmGuild;

  private RoleManagementService mainRoleManager;
  private RoleManagementService tmRoleManager;

  public Hook(ConfigFile config, ConfigFile dbConfig) {
    this.log = Main.getLogger("Startup");
    this.config = config;
    this.dbConfig = dbConfig;
    this.commands = new Commands(this);
    this.userManagementService = new UserManagementService(this);
  }

  private Database connectDatabase(DatabaseConfig config) {
    this.log.info("Establishing new database connection...");
    Database database = new Database(config);
    try {
      Class.forName("com.mysql.jdbc.Driver");
      database.connect();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    this.log.info("Connected.");
    return database;
  }

  public void start() {
    this.log.info("Discord Bot starting up!");

    this.config.injector(HookConfig.class).inject();
    this.log.info("Configuration loaded");

    this.log.info("Connecting to Discord");

    connectToAPI();

    connectToNetworks();

    this.mainRoleManager = new RoleManagementService(this.mainGuild);
    this.tmRoleManager = new RoleManagementService(this.tmGuild);

    this.userManagementService.init();

    registerCommands();
    createPollingServices();

    Main.getExecutor().scheduleAtFixedRate(() -> {
      this.mainRoleManager.mapRanks();
      this.tmRoleManager.mapRanks();
    }, 5, 20, TimeUnit.MINUTES);

    Main.getExecutor().schedule(() -> System.exit(1), 1, TimeUnit.DAYS);
  }

  public void refreshGuilds() {
    this.mainGuild = (GuildImpl) jda.getGuildById(HookConfig.getGuild());
    this.tmGuild = (GuildImpl) jda.getGuildById(HookConfig.getTmGuild());

    for (Guild guild : jda.getGuilds()) {
      if (!(this.mainGuild.getIdLong() == guild.getIdLong() || this.tmGuild.getIdLong() == guild
          .getIdLong())) {
        guild.leave().complete();
        this.getLog().info("Left guild " + guild.getName());
      }
    }
  }

  private void createPollingServices() {
    HookConfig.getFourmPolls().forEach(c -> {
      new DiscussionPollingService(this.mainGuild.getTextChannelById(c.getAsString("channel")),
          c.getInt("id")).start();
    });

    new PrestigeLevelPollingService(this.mainGuild.getTextChannelById(HookConfig.getPrestigePoll()))
        .start();
  }

  private void connectToNetworks() {
    sendStatusMessage("Connecting to services...", Color.blue);

    dbConfig.injector(DbConfig.class).inject();
    DatabaseConfig databaseConfig = DbConfig.MySQLConfig.create();
    database = connectDatabase(databaseConfig);

    sendStatusMessage("Connected to database.", Color.blue);

    // Redis
    redis = DbConfig.RedisConfig.create().build();
    try {
      redis.enable();
      sendStatusMessage("Connected to redis.", Color.blue);
    } catch (Exception e) {
      sendStatusException("Could not connect to redis!", e);
      e.printStackTrace();
      System.exit(-1);
    }
  }

  private void registerCommands() {
    commands.getCommands().put("counts", new CountCommand());
    commands.getCommands().put("reports", new ReportCommand());
    commands.getCommands().put("altcheck", new AltCheckCommand());
    commands.getCommands().put("poll", new PollCommand());
    commands.getCommands().put("unregister", new UnRegisterCommand());
    commands.getCommands().put("register", new RegisterCommand());
    commands.getCommands().put("warn", new PunishmentCommads.Warn());
    commands.getCommands().put("kick", new PunishmentCommads.Kick());
    commands.getCommands().put("tempban", new PunishmentCommads.TempBan());
    commands.getCommands().put("ban", new PunishmentCommads.Ban());
  }

  private void connectToAPI() {
    try {
      JDABuilder builder = new JDABuilder(AccountType.BOT)
          .setToken(HookConfig.getToken())
          .setHttpClientBuilder(new OkHttpClient.Builder()
              .readTimeout(20, TimeUnit.SECONDS)
              .writeTimeout(20, TimeUnit.SECONDS)
              .connectTimeout(40, TimeUnit.SECONDS)
              .retryOnConnectionFailure(true)
          )
          .addEventListener(new MessageLogger());

      if (HookConfig.isCommands()) {
        builder.addEventListener(this.commands);
      }

      builder.addEventListener(new BanListener());
      builder.addEventListener(this.userManagementService);
      this.jda = builder.buildBlocking();

      refreshGuilds();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }

    sendStatusMessage("Bot is connected.", Color.GREEN);
    logInfo();
  }

  public void logInfo() {
    String chans = StringUtil.join(
        this.mainGuild.getTextChannels().stream().filter(TextChannel::canTalk)
            .collect(Collectors.toList()), ", ", new StringUtil.Stringify<TextChannel>() {
          @Override
          public String on(TextChannel object) {
            return "#" + object.getName() + "(" + object.getId() + ")";
          }
        });
    System.out.println("-- Main Channels --");
    System.out.println(chans);

    chans = StringUtil.join(this.tmGuild.getTextChannels().stream().filter(TextChannel::canTalk)
        .collect(Collectors.toList()), ", ", new StringUtil.Stringify<TextChannel>() {
      @Override
      public String on(TextChannel object) {
        return "#" + object.getName() + "(" + object.getId() + ")";
      }
    });
    System.out.println("-- TM Channels --");
    System.out.println(chans);
  }

  public void sendStatusException(String message, Exception e) {
    this.mainGuild.getTextChannelById(HookConfig.getStatus()).sendMessage(
        generateRichMessage("Error", message, Color.RED,
            Arrays.asList(new MessageEmbed.Field("Exception", e.getClass()
                + " " + e.getMessage(), true)))).complete();
    e.printStackTrace();
  }

  public RoleManagementService getRoleManager(Guild guild) {
    if (guild.getIdLong() == this.mainGuild.getIdLong()) {
      return this.mainRoleManager;
    }
    if (guild.getIdLong() == this.tmGuild.getIdLong()) {
      return this.tmRoleManager;
    }

    throw new RuntimeException("Tried to get role manager for unknown guild: " + guild.getName());
  }

  public void sendStatusMessage(String message, Color color) {
    this.mainGuild.getTextChannelById(HookConfig.getStatus())
        .sendMessage(generateRichMessage("Bot Status", message, color)).complete();
  }

  public MessageEmbed generateRichMessage(String title, String description, Color color) {
    return generateRichMessage(title, description, color, Lists.newArrayList());
  }

  public MessageEmbed generateRichMessage(String title, String description, Color color,
      List<MessageEmbed.Field> fields) {
    MessageEmbedImpl messageEmbed = new MessageEmbedImpl();
    messageEmbed.setColor(color)
        .setTitle(title)
        .setDescription(description)
        .setType(EmbedType.RICH)
        .setFields(fields);

    return messageEmbed;
  }
}
