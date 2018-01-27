package net.avicus.atlas.module.tutorial;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;
import lombok.ToString;
import net.avicus.atlas.event.match.MatchStateChangeEvent;
import net.avicus.atlas.event.player.PlayerJoinDelayedEvent;
import net.avicus.atlas.event.player.PlayerSpawnBeginEvent;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.Module;
import net.avicus.atlas.module.groups.Group;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.util.AtlasTask;
import net.avicus.atlas.util.Messages;
import net.avicus.atlas.util.Players;
import net.avicus.compendium.settings.PlayerSettings;
import net.avicus.compendium.settings.Setting;
import net.avicus.compendium.settings.types.SettingTypes;
import net.avicus.magma.Magma;
import net.avicus.magma.network.user.Users;
import net.avicus.tutorial.api.ActionResult;
import net.avicus.tutorial.api.ActiveTutorial;
import net.avicus.tutorial.api.Tutorial;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.joda.time.Duration;

@ToString
public class TutorialModule implements Module {

  public static final Setting<Boolean> SHOW_TUTORIAL_SETTING = new Setting<>(
      "tutorial-on-cycle",
      SettingTypes.BOOLEAN,
      true,
      Messages.SETTINGS_SHOW_TUTORIAL,
      Messages.SETTINGS_SHOW_TUTORIAL_SUMMARY
  );
  private static final String IDENTIFIER = ChatColor.BLACK + "Tutorial";
  private final Match match;
  private final Tutorial tutorial;
  private final HashMap<UUID, ActiveTutorial> activeTutorials;

  public TutorialModule(Match match, Tutorial tutorial) {
    this.match = match;
    this.tutorial = tutorial;
    this.activeTutorials = Maps.newHashMap();
  }

  public boolean isWatchingTutorial(Player player) {
    return this.activeTutorials.containsKey(player.getUniqueId());
  }

  public ItemStack createTutorialItem(Player player) {
    ItemStack stack = new ItemStack(Material.BOOK, 1);
    ItemMeta meta = stack.getItemMeta();

    meta.setDisplayName(ChatColor.GREEN + "Map Tutorial");
    meta.setLore(Collections.singletonList(IDENTIFIER));

    stack.setItemMeta(meta);
    return stack;
  }

  public boolean isTutorialItem(ItemStack stack) {
    return stack != null && stack.getItemMeta().hasLore() && stack.getItemMeta().getLore()
        .contains(IDENTIFIER);
  }

  @Override
  public void close() {
    this.activeTutorials.values().forEach(ActiveTutorial::stop);
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onJoin(PlayerJoinDelayedEvent event) {
    Player player = event.getPlayer();
    AtlasTask.of(() -> {
      if (!player.isOnline()) {
        return;
      }
      Duration online = Magma.get().database().getSessions()
          .timeOnline(Users.user(player).getId());
      boolean timeCause = online.getStandardHours() < 5;
      boolean settingCause = PlayerSettings.get(player, SHOW_TUTORIAL_SETTING);
      if (timeCause || settingCause) {
        AtlasTask.of(() -> startTutorial(player, timeCause, settingCause)).now();
      }
    }).nowAsync();
  }

  @EventHandler
  public void onStateChange(MatchStateChangeEvent event) {
    if (!event.getFrom().isPresent()) {
      AtlasTask.of(() -> {
        for (Player player : event.getMatch().getPlayers()) {
          Duration online = Magma.get().database().getSessions()
              .timeOnline(Users.user(player).getId());
          boolean timeCause = online.getStandardHours() < 5;
          boolean settingCause = PlayerSettings.get(player, SHOW_TUTORIAL_SETTING);
          if (timeCause || settingCause) {
            AtlasTask.of(() -> startTutorial(player, timeCause, settingCause)).now();
          }
        }
      }).nowAsync();
    }
  }

  private void startTutorial(Player player, boolean timeCause, boolean settingCause) {
    if (!match.getRequiredModule(GroupsModule.class).isSpectator(player)) {
      return;
    }

    if (timeCause) {
      player.sendMessage(Messages.UI_TUTORIAL_FORCE_TIME.with(ChatColor.GOLD));
    } else {
      player.sendMessage(Messages.UI_TUTORIAL_FORCE_SETTING.with(ChatColor.GOLD));
    }
    startTutorial(player);
  }

  public void startTutorial(Player player) {
    this.tutorial.create(
        player,
        ActionResult.EXIT,
        ActionResult.ALLOW,
        (t) -> this.activeTutorials.put(player.getUniqueId(), t),
        (t) -> this.activeTutorials.remove(player.getUniqueId())
    ).start();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerSpawn(PlayerSpawnBeginEvent event) {
    if (!event.getGroup().isSpectator()) {
      unTutorialPlayingPlayers(event.getPlayer(), event.getGroup());
      return;
    }

    if (!event.isGiveLoadout()) {
      return;
    }

    Player player = event.getPlayer();
    player.getInventory().addItem(createTutorialItem(event.getPlayer()));
  }

  private void unTutorialPlayingPlayers(Player player, Group group) {
    ActiveTutorial active = this.activeTutorials.get(player.getUniqueId());

    if (active == null || !active.isStarted()) {
      return;
    }

    active.stop();
    Players.reset(player);
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    if (event.getAction() != Action.RIGHT_CLICK_BLOCK
        && event.getAction() != Action.RIGHT_CLICK_AIR) {
      return;
    }

    if (!isTutorialItem(event.getItem())) {
      return;
    }

    GroupsModule module = this.match.getRequiredModule(GroupsModule.class);
    if (!module.getGroup(event.getPlayer()).isSpectator()) {
      event.getPlayer().sendMessage(Messages.ERROR_CANNOT_TUTORIAL.with(ChatColor.RED));
      return;
    }

    event.setCancelled(true);
    startTutorial(event.getPlayer());
  }
}
