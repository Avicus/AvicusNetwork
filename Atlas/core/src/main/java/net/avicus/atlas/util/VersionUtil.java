package net.avicus.atlas.util;

import com.comphenix.protocol.ProtocolLibrary;

public final class VersionUtil {

  private VersionUtil() {
  }

  public static boolean isCombatUpdate() {
    String version = ProtocolLibrary.getProtocolManager().getMinecraftVersion().getVersion();
    return version.contains("1.9") || version.contains("1.10");
  }
}
