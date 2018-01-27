package net.avicus.magma.alerts;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.sk89q.bukkit.util.CommandsManagerRegistration;
import java.util.List;
import java.util.stream.Collectors;
import net.avicus.compendium.settings.PlayerSettings;
import net.avicus.compendium.settings.Setting;
import net.avicus.compendium.settings.types.SettingTypes;
import net.avicus.magma.Magma;
import net.avicus.magma.MagmaConfig;
import net.avicus.magma.api.graph.types.alert.Alert;
import net.avicus.magma.database.model.impl.User;
import net.avicus.magma.util.MagmaTranslations;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Alerts {

  private static final Setting<Boolean> ALERT_NOTIFY_SETTING = new Setting<>(
      "alert-notify",
      SettingTypes.BOOLEAN,
      true,
      MagmaTranslations.SETTING_ALERT_NOTIFY.with(),
      MagmaTranslations.SETTING_ALERT_SUMMARY.with()
  );
  private static final ArrayListMultimap<Integer, Alert> alerts = ArrayListMultimap.create();

  public static void init(CommandsManagerRegistration cmds) {
    if (MagmaConfig.Alerts.isEnabled()) {
      PlayerSettings.register(ALERT_NOTIFY_SETTING);
      new AlertsTask().start();
      cmds.register(AlertsCommands.class);
      Magma.get().getServer().getPluginManager().registerEvents(new AlertsListener(), Magma.get());
    }
  }

  public static void reload(User user) {
    List<Alert> list = Magma.get().getApiClient().getAlerts().getAlerts(user);
    alerts.removeAll(user.getId());
    alerts.putAll(user.getId(), list);
  }

  public static List<Alert> get(User user, boolean onlyUnread) {
    if (user == null) {
      return Lists.newArrayList();
    }

    List<Alert> result = alerts.get(user.getId());
    if (onlyUnread) {
      result = result.stream().filter(a -> !a.isSeen()).collect(Collectors.toList());
    }
    return result;
  }

  public static void add(Alert alert) {
    alerts.put(alert.getUserId(), alert);
  }

  public static void unload(User user) {
    alerts.removeAll(user.getId());
  }

  public static void notify(Player player) {
    if (PlayerSettings.get(player, ALERT_NOTIFY_SETTING)) {
      player.sendMessage(MagmaTranslations.GENERIC_UNREAD_ALERTS.with(ChatColor.YELLOW));
    }
  }
}
