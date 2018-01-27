package net.avicus.atlas.match;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.Getter;
import net.avicus.atlas.documentation.ModuleDocumentation;
import net.avicus.atlas.map.AtlasMap;
import net.avicus.atlas.map.library.MapSource;
import net.avicus.atlas.module.Module;
import net.avicus.atlas.module.ModuleBuildException;
import net.avicus.atlas.module.ModuleFactory;
import net.avicus.atlas.module.ModuleFactorySort;
import net.avicus.atlas.module.broadcasts.BroadcastsFactory;
import net.avicus.atlas.module.channels.ChannelsFactory;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.ChecksFactory;
import net.avicus.atlas.module.chests.ChestsFactory;
import net.avicus.atlas.module.compass.CompassFactory;
import net.avicus.atlas.module.damage.DamageFactory;
import net.avicus.atlas.module.decay.DecayFactory;
import net.avicus.atlas.module.display.DisplayFactory;
import net.avicus.atlas.module.doublekill.DoubleKillFactory;
import net.avicus.atlas.module.elimination.EliminationFactory;
import net.avicus.atlas.module.enderchests.EnderChestsFactory;
import net.avicus.atlas.module.executors.ExecutorsFactory;
import net.avicus.atlas.module.fakeguis.FakeGUIsFactory;
import net.avicus.atlas.module.groups.GroupsFactory;
import net.avicus.atlas.module.invsee.InvSeeFactory;
import net.avicus.atlas.module.items.ItemsFactory;
import net.avicus.atlas.module.kills.KillsFactory;
import net.avicus.atlas.module.kits.KitsFactory;
import net.avicus.atlas.module.loadouts.LoadoutsFactory;
import net.avicus.atlas.module.locales.LocalesFactory;
import net.avicus.atlas.module.modifydamage.ModifyDamageFactory;
import net.avicus.atlas.module.objectives.ObjectivesFactory;
import net.avicus.atlas.module.projectiles.ProjectilesFactory;
import net.avicus.atlas.module.regions.RegionsFactory;
import net.avicus.atlas.module.resourcepacks.ResourcePacksFactory;
import net.avicus.atlas.module.results.ResultsFactory;
import net.avicus.atlas.module.shop.ShopsFactory;
import net.avicus.atlas.module.spawns.SpawnsFactory;
import net.avicus.atlas.module.states.StatesFactory;
import net.avicus.atlas.module.stats.StatsFactory;
import net.avicus.atlas.module.structures.StructuresFactory;
import net.avicus.atlas.module.tutorial.TutorialFactory;
import net.avicus.atlas.module.vote.VoteModuleFactory;
import net.avicus.atlas.module.world.WorldFactory;
import net.avicus.atlas.module.zones.ZonesFactory;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.atlas.util.xml.conditionals.ConditionalContext;
import net.avicus.atlas.util.xml.conditionals.ConditionalsFactory;
import net.avicus.atlas.util.xml.groups.ModuleGroup;
import net.avicus.atlas.util.xml.groups.ModuleGroupsFactory;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Parent;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.located.LocatedJDOMFactory;

/**
 * A match factory controls the construction of {@link Match}s from a collection of
 * registered {@link ModuleFactory module factories}.
 */
public class MatchFactory {

  private static final List<ModuleDocumentation> PARENTLESS_DOCUMENTATIONS = Lists.newArrayList();
  private final Map<Class<? extends ModuleFactory<?>>, ModuleFactory<?>> factories = Maps
      .newHashMap();
  @Getter
  private final List<ModuleDocumentation> documentation = Lists.newArrayList();
  private List<ModuleFactory<?>> factoryView;

  public MatchFactory() {
    // Please keep this in alphabetical order.
    this.register(BroadcastsFactory.class);
    this.register(ChannelsFactory.class);
    this.register(ChecksFactory.class);
    this.register(ChestsFactory.class);
    this.register(KitsFactory.class);
    this.register(CompassFactory.class);
    this.register(DamageFactory.class);
    this.register(DecayFactory.class);
    this.register(DisplayFactory.class);
    this.register(DoubleKillFactory.class);
    this.register(EliminationFactory.class);
    this.register(EnderChestsFactory.class);
    this.register(ExecutorsFactory.class);
    this.register(FakeGUIsFactory.class);
    this.register(GroupsFactory.class);
    this.register(InvSeeFactory.class);
    this.register(ItemsFactory.class);
    this.register(KillsFactory.class);
    this.register(LoadoutsFactory.class);
    this.register(LocalesFactory.class);
    this.register(ModifyDamageFactory.class);
    this.register(ObjectivesFactory.class);
    this.register(ProjectilesFactory.class);
    this.register(RegionsFactory.class);
    this.register(ResourcePacksFactory.class);
    this.register(ResultsFactory.class);
    this.register(ShopsFactory.class);
    this.register(SpawnsFactory.class);
    this.register(StatesFactory.class);
    this.register(StatsFactory.class);
    this.register(StructuresFactory.class);
    this.register(TutorialFactory.class, Bukkit.getPluginManager().isPluginEnabled("Tutorial"));
    this.register(VoteModuleFactory.class);
    this.register(WorldFactory.class);
    this.register(ZonesFactory.class);

    PARENTLESS_DOCUMENTATIONS.add(StaticDocumentations.root());
    PARENTLESS_DOCUMENTATIONS.add(StaticDocumentations.authors());
    PARENTLESS_DOCUMENTATIONS.add(StaticDocumentations.includes());
    PARENTLESS_DOCUMENTATIONS.add(StaticDocumentations.conditionals());
    PARENTLESS_DOCUMENTATIONS.add(StaticDocumentations.moduleGroups());
  }

  public void refresh() {
    this.factoryView = Ordering.from((Comparator<ModuleFactory>) (f1, f2) -> {
      final ModuleFactorySort o1 = f1.getClass().getAnnotation(ModuleFactorySort.class);
      final ModuleFactorySort o2 = f2.getClass().getAnnotation(ModuleFactorySort.class);
      return (o1 != null ? o1.value() : ModuleFactorySort.Order.NORMAL).ordinal() - (o2 != null ? o2
          .value() : ModuleFactorySort.Order.NORMAL).ordinal();
    }).sortedCopy(this.factories.values());

    this.documentation.clear();
    this.documentation.addAll(PARENTLESS_DOCUMENTATIONS);
    this.factories.values().forEach(v -> {
      if (v.getDocumentation() != null) {
        this.documentation.add(v.getDocumentation());
      }
    });
  }

  /**
   * Register a module factory if a condition is met.
   *
   * @param clazz the factory class
   * @param condition the condition
   * @param <F> the factory class type
   * @param <M> the module type
   * @see #register(Class)
   */
  private <F extends ModuleFactory<M>, M extends Module> void register(Class<F> clazz,
      boolean condition) {
    if (condition) {
      this.register(clazz);
    }
  }

  /**
   * Register a module factory.
   *
   * @param clazz the factory class
   * @param <F> the factory class type
   * @param <M> the module type
   */
  public <F extends ModuleFactory<M>, M extends Module> void register(Class<F> clazz) {
    try {
      this.factories.put(clazz, clazz.newInstance());
    } catch (IllegalAccessException | InstantiationException e) {
      e.printStackTrace();
    }
  }

  /**
   * Gets a module factory by its class.
   *
   * @param clazz the module factory class
   * @param <F> the factory class type
   * @param <M> the module type
   * @return the module factory
   * @throws IllegalStateException if the specified factory class has not been registered
   */
  @SuppressWarnings("unchecked")
  public <F extends ModuleFactory<M>, M extends Module> F getFactory(Class<F> clazz) {
    @Nullable final F factory = (F) this.factories.get(clazz);
    if (factory != null) {
      return factory;
    }

    throw new IllegalStateException(
        "Could not find registered module factory for " + clazz.getName());
  }

  /**
   * Create a {@link Match} from a {@link MapSource}.
   *
   * @param map the map to create a match from
   * @return the match
   * @throws MatchBuildException if an exception occurs while parsing the XML for the map
   * @throws MatchBuildException if an exception occurs while processing map includes
   * @throws ModuleBuildException if an exception occurs building a module
   */
  public Match create(final AtlasMap map) throws MatchBuildException, ModuleBuildException {
    final SAXBuilder sax = new SAXBuilder();
    sax.setJDOMFactory(new LocatedJDOMFactory());

    final Document document = map.createDocument();

    final Match match = new Match(map, this);

    // For includes
    handleConditionals(match, document);

    final IncludeProcessor processor = new IncludeProcessor(map, document, sax);
    while (processor.shouldProcess()) {
      try {
        processor.process();
      } catch (JDOMException | IOException e) {
        throw new MatchBuildException(
            "An exception occurred while processing includes for map '" + map.getName() + '\'', e);
      }
    }

    // Ran after so we don't do extra work if the map can't parse anyway.
    runPrerequisites(match, document);

    final XmlElement root = new XmlElement(document.getRootElement());

    // Use this to debug stuff that changes the document before loading.
        /*
            try {
                XMLOutputter xmlOutput = new XMLOutputter();

                // display nice nice
                xmlOutput.setFormat(Format.getPrettyFormat());
                xmlOutput.output(document, new FileWriter("/Users/austinlm/Server/Atlas/test.xml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        */

    refresh();
    for (final ModuleFactory<?> factory : this.factoryView) {
      try {
        Optional<? extends Module> module = factory.build(match, this, root);
        module.ifPresent(match::addModule);
      } catch (Exception e) {
        throw new ModuleBuildException(factory, e);
      }
    }

    map.detectGameTypes(match);

    try {
      map.determineGenre(match);
    } catch (RuntimeException e) {
      throw new MatchBuildException(
          "Map genre could not be determined from modules, please specify in XML.", e);
    }

    return match;
  }

  private void runPrerequisites(Match match, Document document)
      throws MatchBuildException, ModuleBuildException {
    // Parse conditionals from includes.
    handleConditionals(match, document);

    // We do this here because it needs to happen first.
    // Also doesn't use our wrappers for speed and context reasons.
    List<ModuleGroup> parsedGroups = ModuleGroupsFactory.loadGroups(document.getRootElement());
    parsedGroups = ModuleGroupsFactory.overrideAndReturn(parsedGroups);

    if (!parsedGroups.isEmpty()) {
      parsedGroups.forEach(match.getRegistry()::add);

      List<Pair<Element, ModuleGroup>> groups = ModuleGroupsFactory
          .allThatShouldLoad(match, document.getRootElement());

      groups.forEach((entry) -> {
        Element loadLocation = entry.getKey();
        Parent parent = loadLocation.getParent();
        loadLocation.getParent().removeContent(loadLocation);
        ModuleGroup group = entry.getValue();
        group.getLoadTag().detach();
        parent.addContent(group.getElements());
      });
    }
  }

  private void handleConditionals(Match match, Document document) {
    final CheckContext checkContext = new CheckContext(match);

    HashMap<Parent, List<Element>> passToAdd = new HashMap<>();

    final ElementFilter filter = new ElementFilter("conditional");
    Iterator<Element> conditionals = document.getRootElement().getDescendants(filter).iterator();
    while (conditionals.hasNext()) {
      Element conditionalElement = conditionals.next();
      Optional<ConditionalContext> context = ConditionalsFactory.parseContext(conditionalElement);

      if (context.isPresent()) {
        // Finally, clean up conditional element.
        Parent parent = conditionalElement.getParent();
        parent.removeContent(conditionalElement);
        List<Element> passClones = new ArrayList<>();
        Iterator<Element> passIterator = context.get().getPassingElements(checkContext).iterator();
        while (passIterator.hasNext()) {
          Element child = passIterator.next();
          passClones.add(child.clone());
          passIterator.remove();
        }
        // Move any passing elements up to same level as conditional.
        passToAdd.put(parent, passClones);
      }
    }

    passToAdd.forEach(Parent::addContent);
  }
}
