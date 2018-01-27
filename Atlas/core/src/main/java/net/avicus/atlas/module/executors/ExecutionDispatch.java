package net.avicus.atlas.module.executors;

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import lombok.Getter;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.variable.GroupVariable;
import net.avicus.atlas.module.checks.variable.LocationVariable;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.util.Events;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;


/**
 * Handles the dispatching of events to executors.
 */
@Getter
public class ExecutionDispatch {

  protected static final Map<String, Pair<Class<? extends Event>, EventExecutor>> BY_ID = new HashMap<>();
  public static Listener LISTENER = new ExecutionListener();

  static {
    ExecutionListenerRegistration.register();
  }

  /**
   * Match this object should interact with.
   **/
  private final Match match;
  /**
   * Context used for storing variables.
   **/
  private final CheckContext context;
  /**
   * All enabled listeners (based on id).
   **/
  private final LinkedHashSet<String> enabledListeners;
  /**
   * A mapping of events with the executors that are assigned to them.
   **/
  private final LinkedHashMap<Class<? extends Event>, LinkedHashSet<Executor>> assignedExecutors;
  /**
   * Groups module reference.
   **/
  private final GroupsModule module;
  /**
   * All enabled listeners.
   **/
  private final HashMap<Class<? extends Event>, EventExecutor> registeredExecutors = Maps
      .newHashMap();

  public ExecutionDispatch(Match match, CheckContext context,
      LinkedHashSet<String> enabledListeners,
      LinkedHashMap<Class<? extends Event>, LinkedHashSet<Executor>> assignedExecutors,
      GroupsModule module) {
    this.match = match;
    this.context = context;
    this.enabledListeners = enabledListeners;
    this.assignedExecutors = assignedExecutors;
    this.module = module;

    enabledListeners.forEach(this::constructListener);
  }

  @Nullable
  static ExecutionDispatch getDispatch() {
    Match match = Atlas.getMatch();

    if (match == null) {
      return null;
    }

    return match.getRequiredModule(ExecutorsModule.class).getDispatch();
  }

  public static void registerListener(String id, Class<? extends Event> eventClass,
      Consumer<Event> method) {
    EventExecutor executor = new co.aikar.timings.TimedEventExecutor(new EventExecutor() { // Spigot
      public void execute(Listener listener, Event event) throws EventException {
        try {
          if (!eventClass.isAssignableFrom(event.getClass())) {
            return;
          }
          method.accept(event);
        } catch (Throwable t) {
          throw new EventException(t, event);
        }
      }
    }, Atlas.get(), null, eventClass);
    BY_ID.put(id, Pair.of(eventClass, executor));
  }

  public static Pair<Class<? extends Event>, EventExecutor> byId(String id) {
    return BY_ID.get(id);
  }

  public static void whenDispatcherExists(Consumer<ExecutionDispatch> consumer) {
    ExecutionDispatch dispatcher = ExecutionDispatch.getDispatch();
    if (dispatcher != null) {
      consumer.accept(dispatcher);
    }
  }

  /**
   * Construct a {@link Listener} based on it's id.
   *
   * @param ident id of the listener.
   */
  private void constructListener(String ident) {
    Pair<Class<? extends Event>, EventExecutor> type = byId(ident);
    if (type != null) {
      this.registeredExecutors.put(type.getLeft(), type.getRight());
    }
  }

  /**
   * Register all listeners with bukkit.
   */
  public void registerAll() {
    this.registeredExecutors.forEach((e, x) -> {
      Bukkit.getPluginManager()
          .registerEvent(e, LISTENER, EventPriority.MONITOR, x, Atlas.get(), true);
    });
  }

  /**
   * Unregister all listeners with bukkit.
   */
  public void unregisterAll() {
    Events.unregister(LISTENER);
    this.registeredExecutors.clear();
  }

  /**
   * Pass an event to executors
   *
   * @param event that is being handled
   * @param player that performed the event
   * @param executionLocation location of the event
   */
  public void handleEvent(Event event, @Nullable Player player,
      @Nullable Location executionLocation) {
    handleEvent(this.context.duplicate(), event, player, executionLocation);
  }

  /**
   * Pass an event to executors
   *
   * @param context where variables are stored for executors
   * @param event that is being handled
   * @param player that performed the event
   * @param executionLocation location of the event
   */
  public void handleEvent(CheckContext context, Event event, @Nullable Player player,
      @Nullable Location executionLocation) {
    Class clazz = event.getClass();
    if (!this.assignedExecutors.containsKey(clazz) && clazz.getSuperclass() != null) {
      clazz = clazz.getSuperclass();
    }
    if (!this.assignedExecutors.containsKey(clazz) && clazz.getSuperclass() != null) {
      clazz = clazz.getSuperclass();
    }
    if (!this.assignedExecutors.containsKey(clazz)) {
      throw new RuntimeException(
          "Executor failed: Could not find class " + clazz.getSimpleName() + " when handling "
              + event.getClass().getSimpleName());
    }

    if (executionLocation != null) {
      context.add(new LocationVariable(executionLocation));
    }

    if (player != null) {
      context.add(new PlayerVariable(player));
      context.add(new GroupVariable(module.getGroup(player)));
    }

    this.assignedExecutors.get(clazz).forEach(executor -> executor.executeChecked(context));
  }

  /**
   * This is a simple dummy class that we associate all of our registrations with.
   */
  public static class ExecutionListener implements Listener {

  }
}
