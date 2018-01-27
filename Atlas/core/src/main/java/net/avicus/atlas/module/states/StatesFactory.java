package net.avicus.atlas.module.states;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.ModuleDocumentation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.module.ModuleBuildException;
import net.avicus.atlas.module.ModuleFactory;
import net.avicus.atlas.module.ModuleFactorySort;
import net.avicus.atlas.util.xml.XmlElement;

@ModuleFactorySort(ModuleFactorySort.Order.FIRST)
public class StatesFactory implements ModuleFactory<StatesModule> {

  @Override
  public ModuleDocumentation getDocumentation() {
    return ModuleDocumentation.builder()
        .name("Match States")
        .tagName("states")
        .category(ModuleDocumentation.ModuleCategory.ADVANCED)
        .description("This module can be used to modify states that the match goes through.")
        .feature(FeatureDocumentation.builder()
            .name("State")
            .tagName("state")
            .description("A state that will be reached in the lifetime of the match.")
            .attribute("id", Attributes.id(true))
            .attribute("playing", new GenericAttribute(Boolean.class, false,
                "If the match should be marked as playing during this state."), true)
            .build())
        .build();
  }

  @Override
  public Optional<StatesModule> build(Match match, MatchFactory factory, XmlElement root)
      throws ModuleBuildException {
    List<XmlElement> elements = root.getChildren("states");

    List<State> states = new ArrayList<>();

    if (elements.isEmpty()) {
      State cycling = new State("cycling", false, Optional.empty());
      State playing = new State("playing", true, Optional.of(cycling));
      State starting = new State("starting", false, Optional.of(playing));
      states.add(starting);
      states.add(playing);
      states.add(cycling);
    } else {
      elements.forEach(element -> {
        List<XmlElement> children = element.getChildren();
        Collections.reverse(children);

        for (int i = 0; i < children.size(); i++) {
          Optional<State> next = Optional.empty();
          if (i > 0) {
            next = Optional.of(states.get(i - 1));
          }
          XmlElement child = children.get(i);
          String id = child.getAttribute("id").asRequiredString();
          boolean playing = child.getAttribute("playing").asBoolean().orElse(true);
          states.add(new State(id, playing, next));
        }
        Collections.reverse(states);
      });
    }

    match.getRegistry().add(states);

    return Optional.of(new StatesModule(match, states));
  }
}
