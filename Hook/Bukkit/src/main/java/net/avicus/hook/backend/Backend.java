package net.avicus.hook.backend;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import net.avicus.compendium.config.Config;
import net.avicus.compendium.config.ConfigFile;
import net.avicus.hook.DbConfig;
import net.avicus.hook.HookConfig;
import net.avicus.hook.HookPlugin;
import net.avicus.hook.backend.leaderboard.LeaderboardTask;
import net.avicus.hook.backend.leaderboard.XPLeaderboardTask;
import net.avicus.hook.backend.votes.Votes;
import net.avicus.magma.Magma;
import net.avicus.magma.database.Database;
import net.avicus.quest.database.DatabaseConfig;
import org.bukkit.Bukkit;

public class Backend {

  private final Logger log;

  public Backend(HookPlugin plugin) {
    this.log = plugin.getLogger();
  }

  public static void init(HookPlugin plugin) {
    if (!HookConfig.isBackend()) {
      return;
    }

    File remote = new File(plugin.getDataFolder(), "backend.yml");
    InputStream local = plugin.getResource("backend.yml");

    Config config = new Config(local);

    if (!remote.exists()) {
      config.save(remote);
    }
    config = new ConfigFile(remote);

    config.injector(BackendConfig.class).inject();

    Backend backend = new Backend(plugin);
    backend.start();
  }

  private Database connectDatabase(DatabaseConfig config) {
    this.log.info("Establishing new database connection...");
    Database database = new Database(config);
    database.connect();
    this.log.info("Connected.");
    return database;
  }

  public void start() {
    this.log.info("Backend starting up!");

    List<Thread> runningTasks = new ArrayList<>();

    // Leaderboards
    if (BackendConfig.Leaderboards.isEnabled()) {
      this.log.info("Enabling Leaderboards");
      Database database = connectDatabase(DbConfig.MySQLConfig.create());

      LeaderboardTask task = new LeaderboardTask(database);
      task.start();
      runningTasks.add(task);
    }

    // XP Leaderboard
    if (BackendConfig.Leaderboards.isXpEnabled() && Magma.get().getCurrentSeason() != null) {
      this.log.info("Enabling XP Leaderboards");
      Database database = connectDatabase(DbConfig.MySQLConfig.create());

      XPLeaderboardTask task = new XPLeaderboardTask(database);
      task.start();
      runningTasks.add(task);
    }

    // VOTES!
    Votes.init();

    this.log.info("Backend started up.");

    // Ends when all tasks are DONE!
    Timer timer = new Timer();
    timer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        boolean allDone = true;

        for (Thread thread : runningTasks) {
          if (thread.isAlive()) {
            allDone = false;
            break;
          }
        }

        if (allDone) {
          Bukkit.shutdown();
        }
      }
    }, 40 * 1000, 120 * 1000);
  }
}
