package net.avicus.atlas.module.executors;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.InfoTable;
import net.avicus.atlas.documentation.ModuleDocumentation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.ModuleBuildException;
import net.avicus.atlas.module.ModuleFactory;
import net.avicus.atlas.module.ModuleFactorySort;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.executors.types.BlockReplaceExecutor;
import net.avicus.atlas.module.executors.types.EnchantExecutor;
import net.avicus.atlas.module.executors.types.EveryoneExecutor;
import net.avicus.atlas.module.executors.types.GroupExecutor;
import net.avicus.atlas.module.executors.types.LoopingExecutor;
import net.avicus.atlas.module.executors.types.SendMessageExecutor;
import net.avicus.atlas.module.executors.types.SoundExecutor;
import net.avicus.atlas.module.executors.types.SummonExecutor;
import net.avicus.atlas.module.executors.types.TakeItemExecutor;
import net.avicus.atlas.module.executors.types.TeleportExecutor;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.atlas.util.xml.XmlException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.event.Event;
import org.bukkit.plugin.EventExecutor;

@ModuleFactorySort(ModuleFactorySort.Order.LAST)
public class ExecutorsFactory implements ModuleFactory<ExecutorsModule> {

  private static List<FeatureDocumentation> FEATURES = Lists.newArrayList();

  static {
    registerDocumentation(EnchantExecutor::documentation);
    registerDocumentation(SoundExecutor::documentation);
    registerDocumentation(BlockReplaceExecutor::documentation);
    registerDocumentation(SendMessageExecutor::documentation);
    registerDocumentation(SummonExecutor::documentation);
    registerDocumentation(TakeItemExecutor::documentation);
    registerDocumentation(TeleportExecutor::documentation);
    registerDocumentation(GroupExecutor::documentation);
    registerDocumentation(LoopingExecutor::documentation);
    registerDocumentation(EveryoneExecutor::documentation);
  }

  private HashMap<String, BiFunction<Match, XmlElement, Executor>> parsableExecutors = new HashMap<>();

  public static void registerDocumentation(Supplier<FeatureDocumentation> documentation) {
    FEATURES.add(documentation.get());
  }

  @Override
  public ModuleDocumentation getDocumentation() {
    ModuleDocumentation.ModuleDocumentationBuilder builder = ModuleDocumentation.builder();

    builder
        .name("Executors")
        .tagName("executors")
        .description("Executors are used to perform various actions during the match.")
        .category(ModuleDocumentation.ModuleCategory.ADVANCED)
        .feature(FeatureDocumentation.builder()
            .name("Shared Executor Attributes")
            .description("These attributes are shared across all executors/")
            .attribute("id", Attributes.id(false))
            .attribute("check", Attributes.check(true, "before the executor is executed"))
            .build());

    FEATURES.forEach(builder::feature);

    InfoTable triggerInfo = new InfoTable("Events", "Identifier", "Event Name");

    ExecutionDispatch.BY_ID.forEach((i, p) -> {
      triggerInfo.row(i, StringUtils.join(p.getKey().getSimpleName().replace("Event", "")
          .split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])"), " "));
    });

    builder.feature(FeatureDocumentation.builder()
        .name("Triggers")
        .tagName("triggers")
        .description("Triggers are a way to perform an executor based on an event in Minecraft.")
        .attribute("on", new GenericAttribute(String.class, true,
            "ID of the event that should trigger this executor."))
        .attribute("execute", Attributes.idOf(true, "executor"))
        .description("Below are a list of triggers that can be used to trigger executors.")
        .table(triggerInfo)
        .build());

    return builder.build();
  }

  @Override
  public Optional<ExecutorsModule> build(Match match, MatchFactory factory, XmlElement root)
      throws ModuleBuildException {
    if (!root.hasChild("executors")) {
      return Optional.empty();
    }

    LinkedHashSet<String> registeredListeners = new LinkedHashSet<>();

    LinkedHashSet<Executor> executors = new LinkedHashSet<>();
    this.registerExecutor("enchant-item", EnchantExecutor::parse);
    this.registerExecutor("play-sound", SoundExecutor::parse);
    this.registerExecutor("replace-block", BlockReplaceExecutor::parse);
    this.registerExecutor("send-message", SendMessageExecutor::parse);
    this.registerExecutor("summon-entity", SummonExecutor::parse);
    this.registerExecutor("take-item", TakeItemExecutor::parse);
    this.registerExecutor("teleport-player", TeleportExecutor::parse);
    this.registerExecutor("perform-on-group", GroupExecutor::parse);
    this.registerExecutor("loop", LoopingExecutor::parse);
    this.registerExecutor("perform-on-everyone", EveryoneExecutor::parse);

    root.getChildren("executors").forEach(child ->
        child.getChildren("executor").forEach(exec -> {
          if (exec.getChildren().isEmpty() || exec.getChildren().size() > 1) {
            throw new XmlException(exec, "Executors must have 1 and only one child");
          }
          executors.add(parseExecutor(match, exec.getChildren().get(0)));
        })
    );

    executors.removeAll(Collections.singleton(null));

    LinkedHashMap<Class<? extends Event>, LinkedHashSet<Executor>> assignedExecutors = new LinkedHashMap<>();

    root.getChildren("triggers").forEach(child -> {
      child.getChildren("trigger").forEach(trigger -> {
        String on = trigger.getAttribute("on").asRequiredString();

        Pair<Class<? extends Event>, EventExecutor> type = ExecutionDispatch.byId(on);
        if (type == null) {
          throw new XmlException(trigger, "Could not find specified event type.");
        }

        registeredListeners.add(on);

        Class<? extends Event> event = type.getKey();
        assignedExecutors.putIfAbsent(event, new LinkedHashSet<>());
        assignedExecutors.get(event).add(match.getRegistry()
            .get(Executor.class, trigger.getAttribute("execute").asRequiredString(), true).get());

      });
    });

    ExecutorsModule module = new ExecutorsModule(match, executors, this.parsableExecutors,
        registeredListeners, assignedExecutors);
    this.parsableExecutors.clear();
    return Optional.of(module);
  }

  /**
   * Parse an executor from XML.
   *
   * @param match the executor will be used in
   * @param executor that holds the executor
   * @return a parsed executor
   */
  public Executor parseExecutor(Match match, XmlElement executor) {
    XmlElement parent = executor.getParent().get();
    executor.inheritAttributes(parent.getName(), Lists.newArrayList("id"));
    if (executor.getName().equals("check")) {
      return null;
    }

    executor.inheritAttributes("executor");
    executor.inheritAttributes("executors");

    if (executor.getName().equals("execution-group")) {
      Check check = FactoryUtils
          .resolveRequiredCheckChild(match, parent.getAttribute("check"), parent.getChild("check"));
      LinkedHashSet<Executor> executors = new LinkedHashSet<>();
      executor.getChildren().forEach(child -> {
        executors.add(parseExecutor(match, child));
      });
      executors.removeAll(Collections.singleton(null));
      String id = executor.getAttribute("id").asString().orElse(UUID.randomUUID().toString());
      Executor result = new ExecutorCollection(id, check, executors);
      match.getRegistry().add(result);
      return result;
    } else if (this.parsableExecutors.containsKey(executor.getName())) {
      Executor result = this.parsableExecutors.get(executor.getName()).apply(match, executor);
      match.getRegistry().add(result);
      return result;
    } else {
      throw new XmlException(executor, "Could not find executor of type \"" + executor.getName()
          + "\". It may require another module to be loaded.");
    }

  }

  /**
   * Register an executor that can be parsed for this match.
   *
   * @param xmlIdent identifier used for xml element names
   * @param parseMethod function that takes a match and the element and returns a parsed executor
   */
  public void registerExecutor(String xmlIdent,
      BiFunction<Match, XmlElement, Executor> parseMethod) {
    this.parsableExecutors.put(xmlIdent, parseMethod);
  }
}
