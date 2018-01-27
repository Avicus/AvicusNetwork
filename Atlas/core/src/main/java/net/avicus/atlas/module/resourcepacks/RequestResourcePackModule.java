package net.avicus.atlas.module.resourcepacks;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.ToString;
import net.avicus.atlas.event.group.PlayerChangeGroupEvent;
import net.avicus.atlas.event.player.PlayerJoinDelayedEvent;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.Module;
import net.avicus.atlas.util.Messages;
import net.avicus.compendium.TextStyle;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;

@ToString(exclude = "match")
public class RequestResourcePackModule implements Module {

  private final Match match;
  private final ResourcePack request;
  private final boolean force;
  private final Map<UUID, Boolean> accepted;

  /**
   * Constructor.
   *
   * @param request The resource pack to request each player to download.
   * @param force If players must download the resource pack to play.
   */
  public RequestResourcePackModule(Match match, ResourcePack request, boolean force) {
    this.match = match;
    this.request = request;
    this.force = force;
    this.accepted = new HashMap<>();
  }

  public void request(Player player) {
    this.request.requestDownload(player);
  }

  public boolean hasAccepted(Player player) {
    return this.accepted.getOrDefault(player.getUniqueId(), false);
  }

  @EventHandler
  public void onPlayerChangeGroup(PlayerChangeGroupEvent event) {
    if (this.force) {
      if (event.getGroup().isSpectator()) {
        return;
      }

      if (!hasAccepted(event.getPlayer())) {
        TextStyle style = TextStyle.ofColor(ChatColor.RED).bold()
            .click(new ClickEvent(Action.RUN_COMMAND, "/resourcepack"));
        event.getPlayer().sendMessage(Messages.ERROR_ACCEPT_RESOURCE_PACK.with(style));
        event.setCancelled(true);
      }
    }
  }

  @Override
  public void open() {
    this.match.getPlayers().forEach(this.request::requestDownload);
  }

  @EventHandler
  public void onPlayerResourcePackStatus(PlayerResourcePackStatusEvent event) {
    boolean result = event.getStatus() == Status.SUCCESSFULLY_LOADED;
    this.accepted.put(event.getPlayer().getUniqueId(), result);

    if (result) {
      event.getPlayer().sendMessage(Messages.GENERIC_RESOURCE_PACK_ACCEPTED.with(ChatColor.GREEN));
    } else if (this.force && event.getStatus() == Status.DECLINED) {
      event.getPlayer().sendMessage(Messages.ERROR_RESOURCE_PACK_DECLINED.with(ChatColor.RED));
    }
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinDelayedEvent event) {
    request(event.getPlayer());
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    this.accepted.remove(event.getPlayer().getUniqueId());
  }
}
