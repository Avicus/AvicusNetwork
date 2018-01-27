package net.avicus.magma.announce;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import lombok.Getter;
import net.avicus.magma.Magma;
import net.avicus.magma.module.CommandModule;
import net.avicus.magma.module.ListenerModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.joda.time.Instant;
import org.joda.time.Seconds;

public class Announce implements CommandModule, ListenerModule {

  @Getter
  private AnnounceMessageHandler handler;

  @Override
  public void enable() {
    handler = new AnnounceMessageHandler();
    Magma.get().getRedis().register(handler);
  }


  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onJoin(PlayerJoinEvent event) {
    // Is a lobby, not null and less than 50 seconds old.
    if (this.handler.lastMessageReceived != null
        && Instant.now().getMillis() - this.handler.lastMessageReceived.getMillis() <= (
        Seconds.seconds(50).getSeconds() * 1000)) {
      this.handler.broadcast(this.handler.lastMessage, event.getPlayer());
    }
  }

  @Override
  public void registerCommands(CommandsManagerRegistration registrar) {
    registrar.register(AnnounceCommands.Parent.class);
  }
}
