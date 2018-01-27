package net.avicus.atlas.module.executors;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.function.BiFunction;
import lombok.Getter;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.Module;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.executors.types.LoopingExecutor;
import net.avicus.atlas.module.groups.GroupsModule;
import net.avicus.atlas.util.AtlasTask;
import net.avicus.atlas.util.xml.XmlElement;
import org.bukkit.event.Event;

@Getter
public class ExecutorsModule implements Module {

  private final Match match;
  private final LinkedHashSet<Executor> loadedExecutors;
  private final HashMap<String, BiFunction<Match, XmlElement, Executor>> registeredExecutors;
  private final ExecutionDispatch dispatch;

  public ExecutorsModule(Match match,
      LinkedHashSet<Executor> loadedExecutors,
      HashMap<String, BiFunction<Match, XmlElement, Executor>> registeredExecutors,
      LinkedHashSet<String> registeredListeners,
      LinkedHashMap<Class<? extends Event>, LinkedHashSet<Executor>> assignedExecutors) {
    this.match = match;
    this.loadedExecutors = loadedExecutors;
    this.registeredExecutors = registeredExecutors;
    this.dispatch = new ExecutionDispatch(match, new CheckContext(match), registeredListeners,
        assignedExecutors, match.getRequiredModule(GroupsModule.class));
  }

  @Override
  public void open() {
    this.dispatch.registerAll();
  }

  @Override
  public void close() {
    this.loadedExecutors.forEach(e -> {
      if (e instanceof LoopingExecutor) {
        ((LoopingExecutor) e).getRuns().keySet().forEach(AtlasTask::cancel0);
      }
    });
    this.dispatch.unregisterAll();
  }
}