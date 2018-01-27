package net.avicus.atrio;

import net.avicus.compendium.utils.Strings;
import net.avicus.compendium.utils.Task;
import net.avicus.magma.Magma;
import net.avicus.magma.NetworkIdentification;
import net.avicus.magma.database.Database;
import net.avicus.magma.database.model.impl.Discussion;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class LatestThreadTask extends Task implements Listener {

  private final Database database;
  private final int catId;
  private Discussion latest;

  public LatestThreadTask(int catId) {
    this.database = Magma.get().database();
    this.catId = catId;
    this.latest = database.getDiscussions().getLatest(this.catId);
  }

  @Override
  public Plugin getPlugin() {
    return AtrioPlugin.getInstance();
  }

  @Override
  public void run() throws Exception {
    Discussion latest = database.getDiscussions().getLatest(this.catId);
    if (latest == null) {
      return;
    }

    if (latest.getId() != this.latest.getId()) {
      this.latest = latest;
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onJoin(PlayerJoinEvent event) {
    if (this.latest == null) {
      return;
    }

    TextComponent latestAnnounce = new TextComponent("Latest Announcement: ");
    latestAnnounce.setColor(net.md_5.bungee.api.ChatColor.AQUA);
    TextComponent title = new TextComponent(this.latest.getTitle(this.database));
    title.setClickEvent(new ClickEvent(Action.OPEN_URL,
        NetworkIdentification.URL + "/forums/discussions/" + this.latest.getUuid()));
    title.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
        new TextComponent[]{new TextComponent("Click to view.")}));
    title.setColor(net.md_5.bungee.api.ChatColor.GOLD);
    title.setUnderlined(true);

    Player player = event.getPlayer();
    player.sendMessage(Strings.blankLine(ChatColor.GOLD));
    player.sendMessage(new TextComponent(latestAnnounce, title));
    player.sendMessage(Strings.blankLine(ChatColor.GOLD));
  }
}
