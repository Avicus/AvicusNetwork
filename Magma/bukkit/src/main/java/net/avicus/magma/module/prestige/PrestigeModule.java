package net.avicus.magma.module.prestige;

import com.google.common.base.Preconditions;
import com.sk89q.bukkit.util.CommandsManagerRegistration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.avicus.compendium.TextStyle;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizableFormat;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.compendium.locale.text.UnlocalizedFormat;
import net.avicus.magma.Magma;
import net.avicus.magma.database.model.impl.ExperienceTransaction;
import net.avicus.magma.database.model.impl.PrestigeSeason;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.event.user.AsyncHookLoginEvent;
import net.avicus.magma.event.user.AsyncHookLogoutEvent;
import net.avicus.magma.module.CommandModule;
import net.avicus.magma.module.ListenerModule;
import net.avicus.magma.network.user.Users;
import net.avicus.magma.util.MagmaTask;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class PrestigeModule implements ListenerModule, CommandModule {

  private static final LocalizableFormat REWARD_DISPLAY = new UnlocalizedFormat("+{0} XP » {1}");

  protected Map<UUID, Integer> xpBalances;  // user uuid -> XP
  private Map<UUID, PrestigeLevel> levels;  // user uuid -> level

  private PrestigeSeason currentSeason;

  private ActionBarDisplay barDisplay;

  public static String getPrefix(User user, boolean appendSpace) {
    String base = appendSpace ? " " : "";
    if (Magma.get().getMm().hasModule(PrestigeModule.class)) {
      PrestigeModule module = Magma.get().getMm().get(PrestigeModule.class);

      // Not enabled
      if (module.xpBalances == null) {
        return base;
      }

      if (user == User.CONSOLE) {
        return base + PrestigeLevel.MAX.getSymbol();
      }

      if (!Users.player(user).isPresent()) {
        return base + PrestigeLevel.fromDB(Magma.get().database().getPrestigeLevels()
            .currentLevel(user.getId(), Magma.get().getCurrentSeason())).getSymbol();
      }

      return base + module.getPrestigeLevel(user.getUniqueId()).getSymbol();
    }
    return base;
  }

  public static String getPrefix(Player player, boolean appendSpace) {
    String base = appendSpace ? " " : "";
    if (Magma.get().getMm().hasModule(PrestigeModule.class)) {
      PrestigeModule module = Magma.get().getMm().get(PrestigeModule.class);

      // Not enabled
      if (module.xpBalances == null) {
        return base;
      }

      return base + module.getPrestigeLevel(player).getSymbol();
    }
    return base;
  }

  @Override
  public void registerCommands(CommandsManagerRegistration registrar) {
    registrar.register(PrestigeCommands.class);
  }

  @Override
  public void enable() {
    this.currentSeason = Magma.get().getCurrentSeason();
    this.xpBalances = new HashMap<>();
    this.levels = new HashMap<>();

    this.barDisplay = new ActionBarDisplay(this);
    Magma.get().getServer().getPluginManager().registerEvents(this.barDisplay, Magma.get());
  }

  private void modifyLocalBalance(UUID user, int amount) {
    xpBalances.put(user, xpBalances.getOrDefault(user, 0) + amount);
  }

  public void load(User user) {
    xpBalances.put(user.getUniqueId(),
        Magma.get().database().getXpTransations().sumXP(user.getId(), currentSeason));
    levels.put(user.getUniqueId(), PrestigeLevel.fromDB(
        Magma.get().database().getPrestigeLevels().currentLevel(user.getId(), currentSeason)));
  }

  public void unload(User user) {
    xpBalances.remove(user.getUniqueId());
    levels.remove(user.getUniqueId());
  }

  public int getXPLevelsNeeded(double current, PrestigeLevel goal) {
    double goalXp = goal.getXp();
    double level = 1 - (current / goalXp);
    return (int) (level * 100);
  }

  public int getPrestigeLevelId(Player player) {
    net.avicus.magma.database.model.impl.PrestigeLevel level = Magma.get().database()
        .getPrestigeLevels()
        .currentLevel(Users.user(player).getId(), Magma.get().getCurrentSeason());

    if (level == null) {
      return 0;
    }

    return level.getLevel();
  }

  public PrestigeLevel getPrestigeLevel(Player player) {
    return levels.getOrDefault(player.getUniqueId(), PrestigeLevel.LEVEL_0);
  }

  public PrestigeLevel getPrestigeLevel(UUID uuid) {
    return levels.getOrDefault(uuid, PrestigeLevel.LEVEL_0);
  }

  public int getXPLevel(Player player) {
    PrestigeLevel next = getPrestigeLevel(player).next();
    return (getXP(player) / next.getXp());
  }

  public int getXPRelative(Player player) {
    return getXP(player) - getPrestigeLevel(player).getXp();
  }

  public int getXPLevelRelative(Player player) {
    PrestigeLevel current = getPrestigeLevel(player);
    PrestigeLevel next = getPrestigeLevel(player).next();
    if (current == PrestigeLevel.MAX) {
      return getXP(player) / Integer.MAX_VALUE;
    }
    double xpPad = getXP(player) - current.getXp();
    double levelPad = next.getXp() - current.getXp();
    double level = (xpPad / levelPad);
    return (int) (level * 100);
  }

  public int getXP(Player player) {
    return xpBalances.getOrDefault(player.getUniqueId(), 0);
  }

  public double getProgress(Player player) {
    PrestigeLevel next = getPrestigeLevel(player).next();
    double xp = getXP(player);
    double nextXp = next.getXp();
    return xp / nextXp;
  }

  public boolean hasAtLeast(Player player, int XP) {
    return getXP(player) >= XP;
  }

  public void give(Player player, int amount, String genre) {
    User user = Users.user(player);
    amount = Math.abs(amount);

    ExperienceTransaction transaction = new ExperienceTransaction(currentSeason.getId(),
        user.getId(), amount, genre, new Date());
    MagmaTask.of(() -> Magma.get().database().getXpTransations().insert(transaction).execute())
        .nowAsync();
    modifyLocalBalance(user.getUniqueId(), amount);

    MagmaTask.of(() -> this.barDisplay.prepareUpdate(player.getUniqueId())).nowAsync();
  }

  public void reward(Player player, int amount, Localizable reason, String genre) {
    Preconditions.checkArgument(amount > 0);
    player.sendMessage(rewardDisplay(amount, reason));
    give(player, amount, genre);
  }

  public void reward(Player player, int amount, LocalizableFormat reason, String genre) {
    reward(player, amount, reason.with(), genre);
  }

  private Localizable rewardDisplay(int amount, Localizable reason) {
    Localizable textAmount = new LocalizedNumber(amount, TextStyle.ofColor(ChatColor.GOLD).bold());
    return REWARD_DISPLAY.with(ChatColor.GRAY, textAmount, reason);
  }

  public void levelUp(User user) {
    PrestigeLevel level = PrestigeLevel.fromDB(Magma.get().database().getPrestigeLevels()
        .currentLevel(user.getId(), Magma.get().getCurrentSeason()));
    net.avicus.magma.database.model.impl.PrestigeLevel insert = new net.avicus.magma.database.model.impl.PrestigeLevel(
        user.getId(), Magma.get().getCurrentSeason().getId(), level.next().getId());
    Magma.get().database().getPrestigeLevels().insert(insert).execute();
    levels.put(user.getUniqueId(), level.next());
    MagmaTask.of(() -> this.barDisplay.prepareUpdate(user.getUniqueId())).nowAsync();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onAsyncHookLogin(AsyncHookLoginEvent event) {
    load(event.getUser());
  }

  @EventHandler
  public void onAsyncHookLogin(AsyncHookLogoutEvent event) {
    unload(event.getUser());
  }
}
