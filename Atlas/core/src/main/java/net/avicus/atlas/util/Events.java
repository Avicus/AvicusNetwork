package net.avicus.atlas.util;

import net.avicus.atlas.Atlas;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class Events {

  public static <T extends Listener> T register(T listener) {
    Bukkit.getPluginManager().registerEvents(listener, Atlas.get());
    return listener;
  }

  public static void register(Iterable<Listener> listeners) {
    listeners.forEach(Events::register);
  }

  public static <T extends Listener> T unregister(T listener) {
    HandlerList.unregisterAll(listener);
    return listener;
  }

  public static void unregister(Iterable<? extends Listener> listeners) {
    listeners.forEach(Events::unregister);
  }

  public static <T extends Event> T call(T event) {
    Bukkit.getPluginManager().callEvent(event);
    return event;
  }
}
