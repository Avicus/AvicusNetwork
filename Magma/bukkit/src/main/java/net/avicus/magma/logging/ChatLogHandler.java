package net.avicus.magma.logging;

import com.google.common.collect.ImmutableMap;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

public class ChatLogHandler extends Handler {

  private static final ImmutableMap<Level, ChatColor> coloredLevels = ImmutableMap.<Level, ChatColor>builder()
      .put(Level.ALL, ChatColor.GREEN)
      .put(Level.FINEST, ChatColor.AQUA)
      .put(Level.FINER, ChatColor.AQUA)
      .put(Level.FINE, ChatColor.AQUA)
      .put(Level.CONFIG, ChatColor.LIGHT_PURPLE)
      .put(Level.INFO, ChatColor.GREEN)
      .put(Level.WARNING, ChatColor.GOLD)
      .put(Level.SEVERE, ChatColor.RED)
      .build();


  private final String prefix;
  private final String readPerm;

  public ChatLogHandler(String prefix, String readPerm) {
    this.prefix = prefix;
    this.readPerm = readPerm;
  }

  @Override
  public void publish(LogRecord record) {
    ChatColor level = coloredLevels.get(record.getLevel());
    String message = ChatColor.DARK_BLUE + "[" +
        ChatColor.AQUA + prefix +
        ChatColor.DARK_BLUE + "]" +
        ChatColor.DARK_GRAY + ": " +
        ChatColor.DARK_AQUA +
        "[" + level + record.getLevel().getName() +
        ChatColor.DARK_AQUA + "] " + ChatColor.GRAY +
        record.getMessage();
    Bukkit.broadcast(message, readPerm);
    Bukkit.getConsoleSender().sendMessage(message);
  }

  @Override
  public void flush() {
  }

  @Override
  public void close() throws SecurityException {
  }
}
