package net.avicus.atrio;

import net.avicus.compendium.utils.Strings;
import net.avicus.magma.database.model.impl.Server;
import net.avicus.magma.network.server.ServerStatus;
import net.avicus.magma.network.server.Servers;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ServerSign {

  private final Server server;
  private final Vector vector;

  public ServerSign(Server server, Vector vector) {
    this.server = server;
    this.vector = vector;
  }

  public void update() {
    ServerStatus status = Servers.getStatus(this.server).orElse(null);

    if (status == null) {
      return;
    }

    Sign sign = (Sign) new Location(AtrioPlugin.getInstance().getWorld(), vector.getX(),
        vector.getY(),
        vector.getZ()).getBlock().getState();
    ServerStatus.State state = status.getState();

    String line1 = "^b^l" + this.server.getName();
    String line2 = "^a" + status.getPlayers().size() + " ^b/ ^4" + status.getMaxPlayers();
    String line3 = state.getColor().toString();
    String line4 = state.getColor().toString();

    if (!status.isOnline()) {
      line4 += "Offline";
    } else if (status.getMessage() != null) {
      String[] lines = WordUtils.wrap(status.getMessage().orElse(""), 14, "\n", false).split("\n");
      if (lines.length == 1) {
        line4 += lines[0];
      } else {
        line3 += lines[0];
        line4 += lines[1];
      }
    }

    sign.setLine(0, Strings.addColors(line1));
    sign.setLine(1, Strings.addColors(line2));
    sign.setLine(2, Strings.addColors(line3));
    sign.setLine(3, Strings.addColors(line4));
    sign.update();
  }

  public boolean isSign(Block block) {
    return this.vector.equals(block.getLocation().toVector());
  }

  public void connect(Player player) {
    Servers.connect(player, this.server, false, true);
  }
}
