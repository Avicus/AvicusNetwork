package net.avicus.magma.module.prestige;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.compendium.settings.PlayerSettings;
import net.avicus.compendium.settings.Setting;
import net.avicus.compendium.settings.SettingModifyEvent;
import net.avicus.compendium.settings.types.BooleanSettingType;
import net.avicus.compendium.settings.types.SettingTypes;
import net.avicus.magma.event.user.AsyncHookLogoutEvent;
import net.avicus.magma.util.MagmaTask;
import net.avicus.magma.util.MagmaTranslations;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import us.myles.ViaVersion.api.Via;

public class ActionBarDisplay implements Listener {

  private static final Setting<Boolean> SHOW_BAR = new Setting<>(
      "show-xp-action-bar",
      SettingTypes.BOOLEAN,
      true,
      MagmaTranslations.SETTING_BAR_NAME.with(),
      MagmaTranslations.SETTING_BAR_DESCRIPTION.with()
  );
  private final Map<UUID, BaseComponent> activeBars;
  private final PrestigeModule module;

  public ActionBarDisplay(PrestigeModule module) {
    this.activeBars = Maps.newHashMap();
    this.module = module;

    MagmaTask.of(() -> {
      Map<UUID, BaseComponent> copy = ImmutableMap.copyOf(this.activeBars);
      copy.keySet().forEach(this::sendUpdate);
    }).repeatAsync(0, 10);

    PlayerSettings.register(SHOW_BAR);
  }

  public void prepareUpdate(UUID uuid) {
    if (!PlayerSettings.get(uuid, SHOW_BAR)) {
      return;
    }

    if (Via.getAPI().getPlayerVersion(uuid) >= 47) {
      BaseComponent newBar = generateBar(Bukkit.getPlayer(uuid));
      this.activeBars.remove(uuid);
      this.activeBars.put(uuid, newBar);
    }
  }

  public void sendUpdate(UUID uuid) {
    Player p = Bukkit.getPlayer(uuid);
    if (p == null) {
      return;
    }

    String json = ComponentSerializer.toString(this.activeBars.get(uuid));
    IChatBaseComponent text = IChatBaseComponent.ChatSerializer.a(json);
    PacketPlayOutChat bar = new PacketPlayOutChat(text, (byte) 2);
    ((CraftPlayer) p).getHandle().playerConnection.sendPacket(bar);
  }

  public BaseComponent generateBar(Player player) {
    int xp = module.getXPRelative(player);
    int level = module.getXPLevelRelative(player);
    double percentage = module.getProgress(player);

    TextComponent SPACE = new TextComponent(" ");

    int fullDisplay = 25;
    char BAR_CHAR = '❚';
    int completed = (int) (fullDisplay * percentage);
    int remaining = fullDisplay - completed;

    String barComplete = "";
    for (int i = 0; i < completed; i++) {
      barComplete += BAR_CHAR;
    }
    TextComponent complete = new TextComponent(ChatColor.DARK_GREEN.toString() + barComplete);

    String barLeft = "";
    for (int i = 0; i < remaining; i++) {
      barLeft += BAR_CHAR;
    }
    TextComponent remainingBar = new TextComponent(ChatColor.DARK_GRAY.toString() + barLeft);

    TextComponent xpLabel = new TextComponent(ChatColor.BLUE.toString() + "XP: ");

    BaseComponent xpNum = new TextComponent(new TextComponent(ChatColor.GREEN.toString()),
        new LocalizedNumber(xp).translate(player));

    TextComponent levelLabel = new TextComponent(ChatColor.GOLD.toString() + "LVL: ");

    BaseComponent levelNum = new TextComponent(new TextComponent(ChatColor.GREEN.toString()),
        new LocalizedNumber(level).translate(player));

    return new TextComponent(complete, remainingBar, SPACE, xpLabel, xpNum, SPACE, levelLabel,
        levelNum);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onAsyncHookLogin(PlayerJoinEvent event) {
    prepareUpdate(event.getPlayer().getUniqueId());
  }

  @EventHandler
  public void onAsyncHookLogout(AsyncHookLogoutEvent event) {
    this.activeBars.remove(event.getUser().getUniqueId());
  }

  @EventHandler
  public void onToggle(SettingModifyEvent event) {
    if (event.getSetting().getSetting().getId().equals(SHOW_BAR.getId())) {
      BooleanSettingType.BooleanSettingValue show = (BooleanSettingType.BooleanSettingValue) event
          .getSetting().getValue();
      if (show.raw()) {
        BaseComponent newBar = generateBar(Bukkit.getPlayer(event.getPlayer().getUniqueId()));
        this.activeBars.put(event.getPlayer().getUniqueId(), newBar);
      } else {
        this.activeBars.remove(event.getPlayer().getUniqueId());
      }
    }
  }
}
