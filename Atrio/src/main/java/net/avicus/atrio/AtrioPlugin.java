package net.avicus.atrio;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import net.avicus.compendium.config.ConfigFile;
import net.avicus.compendium.utils.NullChunkGenerator;
import net.avicus.hook.Hook;
import net.avicus.magma.database.model.impl.Announcement;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class AtrioPlugin extends JavaPlugin {

  @Getter
  private static AtrioPlugin instance;

  @Getter
  private World world;


  public void onEnable() {
    instance = this;

    loadConfig();

    int interval = AtrioConfig.getUpdateInterval();
    AtrioListener.Spawn spawn = AtrioConfig.getSpawn();
    List<ServerSign> signs = AtrioConfig.getSigns();
    List<Portal> portals = AtrioConfig.getPortals();
    List<Pad> pads = AtrioConfig.getPads();
    List<Present> presents = AtrioConfig.getPresents();

    // Update schedule
    getServer().getScheduler()
        .runTaskTimer(this, () -> signs.forEach(ServerSign::update), 0, interval);

    // Server book
    Optional<ItemStack> book = Optional.empty();
    if (AtrioConfig.Inventory.Book.isEnabled()) {
      ItemStack stack = new ItemStack(Material.WRITTEN_BOOK);
      BookMeta meta = (BookMeta) stack.getItemMeta();

      meta.setTitle(AtrioConfig.Inventory.Book.getName());
      meta.setAuthor(AtrioConfig.Inventory.Book.getAuthor());
      meta.setPages(AtrioConfig.Inventory.Book.getPages());

      stack.setItemMeta(meta);
      book = Optional.of(stack);
    }

    // Listener
    AtrioListener listener = new AtrioListener(this, spawn, signs, portals, pads, presents, book);
    getServer().getPluginManager().registerEvents(listener, this);

    LatestThreadTask latestThreadTask = new LatestThreadTask(AtrioConfig.getAnnounceForum());
    getServer().getPluginManager().registerEvents(latestThreadTask, this);
    latestThreadTask.repeatAsync(0, 120);

    WorldCreator creator = new WorldCreator("lobby");
    creator.generator(new NullChunkGenerator());
    creator.type(WorldType.FLAT);

    world = creator.createWorld();

    String holiday = setupHolidays();
    if (holiday != null) {
      listener.getTitleOptions().add(new Announcement(holiday));
    }

    world.setGameRuleValue("doDaylightCycle", "false");
    world.setGameRuleValue("doFireTick", "false");
    world.setGameRuleValue("doMobLoot", "false");
    world.setGameRuleValue("mobGriefing", "false");
    world.setGameRuleValue("showDeathMessages", "false");

    new AnnouncementsTask(Hook.database().getAnnouncements().findByType(Announcement.Type.LOBBY))
        .start();
  }

  private String setupHolidays() {
    // Special Holiday tasks/listeners (do these in order)
    LocalDate localDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    int month = localDate.getMonthValue();
    int day = localDate.getDayOfMonth();

    String res = null;

    // New Years
    if ((month == 1 && day == 1) || (month == 12
        && localDate.lengthOfMonth() - localDate.getDayOfMonth() == 0)) {
      List<Color[]> colors = new ArrayList<>();
      colors.add(new Color[]{Color.AQUA, Color.GREEN});
      colors.add(new Color[]{Color.BLUE, Color.RED});
      colors.add(new Color[]{Color.LIME, Color.TEAL});
      colors.add(new Color[]{Color.ORANGE, Color.OLIVE});
      colors.add(new Color[]{Color.PURPLE, Color.NAVY});

      world.setTime(18000);

      res = ChatColor.AQUA + "Happy " + ChatColor.GOLD + "New " + ChatColor.BLUE + " Years"
          + ChatColor.RED + "!";

      new FireworkSpawnTask(colors, new Color[]{}, FireworkEffect.Type.BALL_LARGE).repeat(40, 15);
    } else if (month == 1 && day == 26) { // Australia Day
      List<Color[]> colors = new ArrayList<>();
      colors.add(new Color[]{Color.RED, Color.WHITE, Color.BLUE});
      colors.add(new Color[]{Color.RED, Color.WHITE});
      colors.add(new Color[]{Color.RED, Color.BLUE});

      world.setTime(18000);

      res = ChatColor.WHITE + "Happy " + ChatColor.RED + "Australia " + ChatColor.BLUE + " Day!";

      new FireworkSpawnTask(colors, new Color[]{Color.BLUE}, FireworkEffect.Type.BALL_LARGE)
          .repeat(40, 60);
    } else if (month == 4 && day == 22) { // Earth Day
      List<Color[]> colors = new ArrayList<>();
      colors.add(new Color[]{Color.GREEN});

      world.setTime(18000);

      res = ChatColor.WHITE + "Happy " + ChatColor.GREEN + "Earth Day" + "!";

      new FireworkSpawnTask(colors, new Color[]{Color.BLUE}, FireworkEffect.Type.BALL_LARGE)
          .repeat(40, 60);
    } else if (month == 7 && day == 1) { // Canada Day
      List<Color[]> colors = new ArrayList<>();
      colors.add(new Color[]{Color.RED, Color.WHITE});
      colors.add(new Color[]{Color.RED});
      colors.add(new Color[]{Color.WHITE});

      world.setTime(18000);

      res = ChatColor.WHITE + "Happy " + ChatColor.RED + "Canada" + ChatColor.WHITE + " Day!";

      new FireworkSpawnTask(colors, new Color[]{Color.SILVER}, FireworkEffect.Type.STAR)
          .repeat(40, 40);
    } else if (month == 7 && day == 4) { // Independence Day
      List<Color[]> colors = new ArrayList<>();
      colors.add(new Color[]{Color.RED, Color.WHITE, Color.BLUE});
      colors.add(new Color[]{Color.RED, Color.WHITE});
      colors.add(new Color[]{Color.RED, Color.BLUE});

      world.setTime(18000);

      res = ChatColor.RED + "Happy " + ChatColor.WHITE + "Independence " + ChatColor.BLUE + "Day"
          + ChatColor.WHITE + "!";

      new FireworkSpawnTask(colors, new Color[]{Color.AQUA}, FireworkEffect.Type.STAR)
          .repeat(40, 40);
    } else if (month == 9 && day == 16) { // Mexican Independence Day
      List<Color[]> colors = new ArrayList<>();
      colors.add(new Color[]{Color.RED, Color.WHITE, Color.GREEN});
      colors.add(new Color[]{Color.RED, Color.WHITE});
      colors.add(new Color[]{Color.GREEN, Color.WHITE});
      colors.add(new Color[]{Color.RED, Color.GREEN});

      world.setTime(18000);

      res = ChatColor.GREEN + "Feliz Día de " + ChatColor.RED + " la Independencia!";

      new FireworkSpawnTask(colors, new Color[]{Color.AQUA}, FireworkEffect.Type.BURST)
          .repeat(40, 40);
    } else if (month == 12 && day > 15 && day < 26) { // Christmas
      List<Color[]> colors = new ArrayList<>();
      colors.add(new Color[]{Color.GREEN});
      colors.add(new Color[]{Color.RED});
      colors.add(new Color[]{Color.LIME});
      colors.add(new Color[]{Color.AQUA});

      world.setTime(18000);

      // PC shit
      res = ChatColor.RED + "Happy" + ChatColor.GREEN + " Holidays!";

      new FireworkSpawnTask(colors, new Color[]{Color.AQUA}, FireworkEffect.Type.STAR)
          .repeat(40, 40);
    }
    return res;
  }

  private void loadConfig() {
    File configFile = new File(getDataFolder(), "config.yml");

    try {
      this.saveDefaultConfig();
      this.reloadConfig();
      ConfigFile config = new ConfigFile(configFile);
      config.injector(AtrioConfig.class).inject();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
