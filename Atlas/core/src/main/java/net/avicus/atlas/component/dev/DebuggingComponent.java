package net.avicus.atlas.component.dev;

import java.text.MessageFormat;
import java.util.logging.Level;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.event.match.MatchStateChangeEvent;
import net.avicus.magma.module.ListenerModule;
import org.bukkit.event.EventHandler;

public class DebuggingComponent implements ListenerModule {

  private void debug(String prefix, String message, Object... vars) {
    Atlas.get().getLogger()
        .log(Level.INFO, MessageFormat.format("[" + prefix + "] " + message, vars) + "");
  }

  @Override
  public void enable() {

  }

  @EventHandler
  public void onMatchStateChange(MatchStateChangeEvent event) {
    debug("State", "{0} -> {1}", event.getFrom(), event.getTo());
  }
}
