package net.avicus.hook.afk;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizableFormat;
import net.avicus.compendium.locale.text.UnlocalizedFormat;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.avicus.hook.utils.HookTask;
import net.avicus.hook.utils.Messages;
import net.avicus.magma.NetworkIdentification;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.joda.time.Instant;
import tc.oc.tracker.event.PlayerCoarseMoveEvent;

public class AFKKickTask extends HookTask implements Listener {

  private final HashMap<Player, Instant> actions = Maps.newHashMap();

  public AFKKickTask start() {
    repeatAsync(0, 600 * 20);
    return this;
  }

  @Override
  public void run() throws Exception {
    Iterator<Map.Entry<Player, Instant>> iterator = this.actions.entrySet().iterator();
    List<Player> toKick = new ArrayList<>();
    while (iterator.hasNext()) {
      Map.Entry<Player, Instant> entry = iterator.next();
      if (!entry.getKey().isOnline()) {
        iterator.remove();
        continue;
      }

      if (Instant.now().getMillis() - entry.getValue().getMillis() > (600 * 1000)) {
        toKick.add(entry.getKey());
      }
    }
    if (!toKick.isEmpty()) {
      Localizable website = new UnlocalizedText(NetworkIdentification.URL, ChatColor.AQUA);
      LocalizableFormat format = new UnlocalizedFormat("{0}\n\n{1}");
      Localizable message = format.with(Messages.UI_AFK_LINE_1.with(ChatColor.BLUE),
          Messages.UI_AFK_LINE_2.with(ChatColor.GOLD, website));
      HookTask.of(() -> {
        toKick.forEach(p -> p.kickPlayer(message.translate(p).toLegacyText()));
      }).now();
    }
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent e) {
    update(e.getPlayer());
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    update(e.getPlayer());
  }

  @EventHandler
  public void onMove(PlayerCoarseMoveEvent e) {
    update(e.getPlayer());
  }

  @EventHandler
  public void onCommand(PlayerCommandPreprocessEvent e) {
    update(e.getPlayer());
  }

  private void update(Player player) {
    if (!player.hasPermission("hook.afk.ignore")) {
      actions.put(player, Instant.now());
    }
  }
}
