package net.avicus.hook.listener;

import java.util.HashMap;
import java.util.Map;
import net.avicus.compendium.settings.PlayerSettings;
import net.avicus.compendium.settings.SettingModifyEvent;
import net.avicus.hook.Hook;
import net.avicus.magma.database.model.impl.Setting;
import net.avicus.magma.database.table.impl.SettingTable;
import net.avicus.magma.event.user.AsyncHookLoginEvent;
import net.avicus.magma.module.ListenerModule;
import net.avicus.magma.network.user.Users;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class SettingModule implements ListenerModule {

  @Override
  public void enable() {
  }

  @EventHandler
  public void storeSetting(final SettingModifyEvent event) {
    final int userId = Users.user(event.getPlayer()).getId();
    final SettingTable st = Hook.database().getSettings();

    String id = event.getSetting().getSetting().getId();
    String value = event.getSetting().getValue().serialize();

    st.updateOrSet(userId, id, value);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void loadSettings(final AsyncHookLoginEvent event) {
    Map<String, String> settings = new HashMap<>();

    for (Setting setting : Hook.database().getSettings().findByUser(event.getUser().getId())) {
      settings.put(setting.getKey(), setting.getValue());
    }

    PlayerSettings.store()
        .set(event.getUser().getUniqueId(), settings, PlayerSettings.settings(), false);
  }
}
