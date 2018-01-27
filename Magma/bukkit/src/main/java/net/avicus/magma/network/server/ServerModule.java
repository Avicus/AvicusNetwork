package net.avicus.magma.network.server;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.avicus.magma.Magma;
import net.avicus.magma.module.Module;
import net.avicus.magma.network.NetworkConstants;
import org.bukkit.entity.Player;

public class ServerModule implements Module {

  public void moveToLobby(final Player player) {
    final ByteArrayDataOutput output = ByteStreams.newDataOutput();
    output.writeUTF(player.getName());
    output.writeUTF(NetworkConstants.LOBBY_SERVER);
    player.sendPluginMessage(Magma.get(), NetworkConstants.CONNECT_CHANNEL, output.toByteArray());
  }
}
