package net.avicus.hook.achievements;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.event.competitor.CompetitorWinEvent;
import net.avicus.atlas.event.player.PlayerReceiveMVPEvent;
import net.avicus.atlas.module.vote.PlayerCastVoteEvent;
import net.avicus.compendium.settings.PlayerSettings;
import net.avicus.compendium.settings.Setting;
import net.avicus.compendium.settings.types.SettingTypes;
import net.avicus.compendium.utils.Strings;
import net.avicus.grave.event.PlayerDeathEvent;
import net.avicus.hook.Hook;
import net.avicus.hook.rate.MapRatedEvent;
import net.avicus.hook.utils.Events;
import net.avicus.hook.utils.HookTask;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.Magma;
import net.avicus.magma.database.Database;
import net.avicus.magma.database.model.impl.Achievement;
import net.avicus.magma.database.model.impl.AchievementPursuit;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.event.user.AsyncHookLoginEvent;
import net.avicus.magma.module.ListenerModule;
import net.avicus.magma.module.Module;
import net.avicus.magma.network.user.Users;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import tc.oc.tracker.DamageInfo;
import tc.oc.tracker.damage.GravityDamageInfo;
import tc.oc.tracker.damage.MeleeDamageInfo;
import tc.oc.tracker.damage.ProjectileDamageInfo;

public class Achievements implements Module, ListenerModule {

  private static final Setting<Boolean> ACHIEVEMENT_SETTING = new Setting<>(
      "other-achievements",
      SettingTypes.BOOLEAN,
      true,
      Messages.SETTINGS_ACHIEVEMENTS,
      Messages.SETTINGS_ACHIEVEMENTS_SUMMARY
  );

  private Table<String, Integer, Achievement> incrementalAchievements = TreeBasedTable.create();
  private HashMap<String, Achievement> oneTimeAchievements = Maps.newHashMap();

  private Database database;

  private static void message(User user, Achievement achievement) {
    Users.player(user).ifPresent(p -> {
      p.playSound(p.getLocation(), Sound.CREEPER_HISS, 1.3f, 1.5f);
      p.sendMessage("");
      p.sendMessage("");
      p.sendMessage(Strings
          .padChatMessage("Achievement Unlocked: " + achievement.getName(), " ", ChatColor.BLUE,
              ChatColor.GREEN));
      if (!achievement.getDescription().isEmpty()) {
        p.sendMessage(ChatColor.AQUA + ChatColor.ITALIC.toString() + achievement.getDescription());
      }
      p.sendMessage("");
      p.sendMessage("");

      Bukkit.getOnlinePlayers().forEach(o -> {
        if (PlayerSettings.get(o, ACHIEVEMENT_SETTING)) {
          o.playSound(o.getLocation(), Sound.FIREWORK_LAUNCH, 1, 1.4f);
          o.sendMessage(Messages.UI_ACHIEVEMENT
              .with(ChatColor.GOLD, p.getDisplayName(), achievement.getName()));
        }
      });
    });
  }

  private void loadAchievables() {
    // large
    int[] large = new int[]{10, 20, 50, 100, 200, 500, 1000, 1500, 2000, 2500, 3000, 5000};
    for (String id : new String[]{
        "kills", // get x kills
        "fist-kills", // get x kills with a fist
        "hill-captures", // capture x hills
        "wool-touches", // touch x wools
        "wool-places", // place x wools
        "monument-touches", // touch x monuments
        "monument-completions", // complete x monuments
        "leakable-touches", // touch x leakables
        "leakable-leaks", // leak x leakables
        "flag-captures", // capture a flag x times
        "credits", // have x credits
    }) {
      for (int num : large) {
        incrementalAchievements
            .put(id, num, this.database.getAchievements().getOrCreate(num + "-" + id));
      }
    }

    // small ranges
    int[] small = new int[]{5, 10, 15, 20, 30, 40, 50, 75, 100, 150, 200, 250};
    for (String id : new String[]{
        "match-mvps", // get match mvp x times
        "map-votes-cast", // cast a next map vote x times
        "map-ratings", // rate a map x times
        "wins", // be on the winning team x times
    }) {
      for (int num : small) {
        incrementalAchievements
            .put(id, num, this.database.getAchievements().getOrCreate(num + "-" + id));
      }
    }

    // distance
    int[] distance = new int[]{50, 60, 80, 100, 125, 180, 200, 220, 250};
    for (String id : new String[]{
        "fall-death", // die from a fall of x
        "fall-kill", // kill someone from a height of x
        "bow-hit", // hit someone with a bow x blocks away
        "bow-kill" // kill someone with a box x blocks away
    }) {
      for (int dist : distance) {
        incrementalAchievements
            .put(id, dist, this.database.getAchievements().getOrCreate(id + "-" + dist));
      }
    }

    // Tiny
    int[] tiny = new int[]{1, 5, 10, 15, 20, 50, 100};
    String[] tinyGoals = new String[]{
        "kills-on-horse", // on a horse
        "kills-in-boat", // in a boat
        "kills-in-void", // below y=0
        "kills-on-pig", // riding a pig
        "kills-in-minecart" // riding in a minecart
    };
    for (String prefix : new String[]{
        "own", // killer is x
        "other", // victim is x
        "both" // both killer and victim are x
    }) {
      for (String id : tinyGoals) {
        for (int num : tiny) {
          incrementalAchievements.put(id + "-" + prefix, num,
              this.database.getAchievements().getOrCreate(num + "-" + id + "-" + prefix));
        }
      }
    }

    for (String s : new String[]{
        "ts-register", // register with the teamspeak bot
        "web-register", // register on the forums
        "donate", // donate on the shop (donation package)
    }) {
      oneTimeAchievements.put(s, this.database.getAchievements().getOrCreate(s));
    }
  }

  @Override
  public void enable() {
    this.database = Hook.database();

    HookTask.of(() -> this::loadAchievables).nowAsync();

    if (Atlas.get().getLoader().hasModule("competitive-objectives")) {
      Events.register(new CompetitveAchievements(this));
    }

    PlayerSettings.register(ACHIEVEMENT_SETTING);
  }

  private void load(User user) {
    Set<String> slugs = Sets.newHashSet();
    slugs.addAll(incrementalAchievements.rowKeySet());

    slugs = slugs.stream().filter(s -> !database.getPursuits().isPursuing(s, user))
        .collect(Collectors.toSet());
    slugs.forEach(s -> {
      int val = 0;
      switch (s) {
        // Do all special conversion from legacy statistics
        case "kills":
          val = database.getDeaths().kills(user.getId());
          break;
        case "hill-captures":
          val = database.getObjectiveCompletions()
              .getTypeCount(user.getId(), database.getObjectiveTypes().findOrCreate("hill"));
          break;
        case "wool-places":
          val = database.getObjectiveCompletions()
              .getTypeCount(user.getId(), database.getObjectiveTypes().findOrCreate("wool"));
          break;
        case "monument-completions":
          val = database.getObjectiveCompletions()
              .getTypeCount(user.getId(), database.getObjectiveTypes().findOrCreate("monument"));
          break;
        case "leakable-leaks":
          val = database.getObjectiveCompletions()
              .getTypeCount(user.getId(), database.getObjectiveTypes().findOrCreate("leakable"));
          break;
        case "flag-captures":
          val = database.getObjectiveCompletions()
              .getTypeCount(user.getId(), database.getObjectiveTypes().findOrCreate("flag"));
          break;
        case "credits":
          val = database.getCreditTransactions().sumCredits(user.getId());
      }
      if (val > 0) {
        database.getPursuits().insert(new AchievementPursuit(s, user, val)).execute();
      }
    });

    database.getAchievements().findByUser(user.getId(), database.getReceivers()).forEach(ac -> {
      database.getReceivers().removeLower(ac, user, database.getAchievements());
    });

    oneTimeAchievements.values().forEach(a -> {
      if (database.getReceivers().hasAchievement(a, user)) {
        return;
      }

      switch (a.getSlug()) {
        case "ts-register":
          if (!database.getTeamSpeakUsers().findByUser(user.getId()).isEmpty()) {
            database.getReceivers()
                .give(a, user, Magma.get().database().getAchievements(), Achievements::message);
          }
          break;
        case "web-register":
          if (database.getUsers().isRegistered(user)) {
            database.getReceivers()
                .give(a, user, Magma.get().database().getAchievements(), Achievements::message);
          }
          break;
      }
    });
  }

  public void increment(String slug, Player player) {
    if (this.incrementalAchievements.containsRow(slug)) {
      HookTask.of(() -> {
        User user = Users.user(player);
        Map<Integer, Achievement> checkPoints = this.incrementalAchievements.row(slug);
        int current = database.getPursuits().increment(slug, user);
        if (checkPoints.containsKey(current)) {
          database.getReceivers()
              .give(checkPoints.get(current), user, Magma.get().database().getAchievements(),
                  Achievements::message);
        }
      }).nowAsync();
    } else {
      throw new UnsupportedOperationException(slug + " achievement not found");
    }
  }

  // Tracking

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void doLoad(AsyncHookLoginEvent event) {
    load(event.getUser());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerDeath(PlayerDeathEvent event) {
    Player cause = null;
    boolean horseOwn = false;
    boolean boatOwn = false;
    boolean inVoidOwn = false;
    boolean minecartOwn = false;
    boolean pigOwn = false;
    boolean horseOther = false;
    boolean boatOther = false;
    boolean inVoidOther = false;
    boolean minecartOther = false;
    boolean pigOther = false;

    boolean fist = false;

    if (event.getLifetime().getLastDamage() != null) {
      Entity dieVehicle = event.getPlayer().getVehicle();
      if (dieVehicle != null) {
        boatOther = dieVehicle instanceof Boat;
        horseOther = dieVehicle instanceof Horse;
        minecartOther = dieVehicle instanceof Minecart;
        pigOther = dieVehicle instanceof Pig;
      }
      inVoidOther = event.getLifetime().getLastDamage().getInfo().getDamageCause()
          != EntityDamageEvent.DamageCause.VOID &&
          event.getPlayer().getLocation().getY() <= 0;

      LivingEntity entity = event.getLifetime().getLastDamage().getInfo().getResolvedDamager();
      if (entity instanceof Player) {
        Player killer = (Player) entity;
        cause = killer;

        DamageInfo info = event.getLifetime().getLastDamage().getInfo();
        if (info instanceof MeleeDamageInfo) {
          fist = ((MeleeDamageInfo) info).getWeapon() == null
              || ((MeleeDamageInfo) info).getWeapon() == Material.AIR;
        }

        if (info instanceof ProjectileDamageInfo) {
          Double dist = ((ProjectileDamageInfo) info).getDistance();
          if (dist != null) {
            Achievement bowKill = null;
            for (Map.Entry<Integer, Achievement> entry : incrementalAchievements.row("bow-kill")
                .entrySet()) {
              if (dist >= entry.getKey()) {
                bowKill = entry.getValue();
              }
            }
            if (bowKill != null) {
              final Achievement bow = bowKill;
              HookTask.of(() -> database.getReceivers()
                  .give(bow, Users.user(killer), Magma.get().database().getAchievements(),
                      Achievements::message))
                  .nowAsync();
            }
          }
        } else if (info instanceof GravityDamageInfo && event.getLocation().getY() > 0) {
          double dist = 0;
          if (((GravityDamageInfo) info).getFallLocation() != null) {
            dist = ((GravityDamageInfo) info).getFallLocation()
                .distanceSquared(event.getLocation());
          }
          Achievement fallKill = null;
          for (Map.Entry<Integer, Achievement> entry : incrementalAchievements.row("fall-kill")
              .entrySet()) {
            if (dist >= entry.getKey()) {
              fallKill = entry.getValue();
            }
          }
          if (fallKill != null) {
            final Achievement fallFinal = fallKill;
            HookTask.of(
                () -> database.getReceivers().give(fallFinal, Users.user(killer),
                    Magma.get().database().getAchievements(), Achievements::message))
                .nowAsync();
          }
          Achievement fallDeath = null;
          for (Map.Entry<Integer, Achievement> entry : incrementalAchievements.row("fall-death")
              .entrySet()) {
            if (dist >= entry.getKey()) {
              fallDeath = entry.getValue();
            }
          }
          if (fallDeath != null) {
            final Achievement deathFinal = fallDeath;
            HookTask.of(() -> database.getReceivers()
                .give(deathFinal, Users.user(event.getPlayer()),
                    Magma.get().database().getAchievements(), Achievements::message)).nowAsync();
          }
        }

        Entity causeVehicle = event.getPlayer().getVehicle();
        if (causeVehicle != null) {
          boatOwn = causeVehicle instanceof Boat;
          horseOwn = causeVehicle instanceof Horse;
          minecartOwn = causeVehicle instanceof Minecart;
          pigOwn = causeVehicle instanceof Pig;
        }
        inVoidOwn = killer.getLocation().getY() <= 0;
      }
    }

    boolean horseBoth = horseOther && horseOwn;
    boolean boatBoth = boatOwn && boatOther;
    boolean inVoidBoth = inVoidOwn && inVoidOther;
    boolean minecartBoth = minecartOther && minecartOwn;
    boolean pigBoth = pigOther && pigOwn;

    if (cause != null) {
      increment("kills", cause);
      if (fist) {
        increment("fist-kills", cause);
      }
      if (inVoidBoth) {
        increment("kills-in-void-both", cause);
      } else {
        if (inVoidOther) {
          increment("kills-in-void-other", cause);
        }
        if (inVoidOwn) {
          increment("kills-in-void-own", cause);
        }
      }

      if (minecartBoth) {
        increment("kills-in-minecart-both", cause);
      } else {
        if (minecartOther) {
          increment("kills-in-minecart-other", cause);
        }
        if (minecartOwn) {
          increment("kills-in-minecart-own", cause);
        }
      }

      if (boatBoth) {
        increment("kills-in-boat-both", cause);
      } else {
        if (boatOther) {
          increment("kills-in-boat-other", cause);
        }
        if (boatOwn) {
          increment("kills-in-boat-own", cause);
        }
      }

      if (horseBoth) {
        increment("kills-in-boat-both", cause);
      } else {
        if (horseOther) {
          increment("kills-on-horse-other", cause);
        }
        if (horseOwn) {
          increment("kills-on-horse-own", cause);
        }
      }

      if (pigBoth) {
        increment("kills-on-pig-both", cause);
      } else {
        if (pigOther) {
          increment("kills-on-pig-other", cause);
        }
        if (pigOwn) {
          increment("kills-on-pig-own", cause);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onHit(EntityDamageByEntityEvent event) {
    double dist = 0;
    if (event.getDamager() instanceof Player && event.getEntity() instanceof Player
        && event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
      dist = event.getDamager().getLocation().distance(event.getEntity().getLocation());
      Achievement bowHit = null;
      for (Map.Entry<Integer, Achievement> entry : incrementalAchievements.row("bow-hit")
          .entrySet()) {
        if (dist > entry.getKey()) {
          bowHit = entry.getValue();
        }
      }
      if (bowHit != null) {
        final Achievement bowFinal = bowHit;
        HookTask.of(() -> database.getReceivers()
            .give(bowFinal, Users.user(event.getDamager()),
                Magma.get().database().getAchievements(), Achievements::message)).nowAsync();
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onMVP(PlayerReceiveMVPEvent event) {
    increment("match-mvps", event.getPlayer());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onWin(CompetitorWinEvent event) {
    event.getWinner().getPlayers().forEach((p) -> {
      increment("wins", p);
    });
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onVote(PlayerCastVoteEvent event) {
    increment("map-votes-cast", event.getPlayer());
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onRate(MapRatedEvent event) {
    increment("map-ratings", event.getPlayer());
  }
}
