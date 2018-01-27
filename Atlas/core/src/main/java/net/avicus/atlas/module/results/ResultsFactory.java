package net.avicus.atlas.module.results;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.InfoTable;
import net.avicus.atlas.documentation.ModuleDocumentation;
import net.avicus.atlas.documentation.attributes.Attribute;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.ModuleBuildException;
import net.avicus.atlas.module.ModuleFactory;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.groups.teams.Team;
import net.avicus.atlas.module.results.scenario.EndScenario;
import net.avicus.atlas.module.results.scenario.ObjectivesScenario;
import net.avicus.atlas.module.results.scenario.TeamScenario;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.atlas.util.xml.XmlException;

public class ResultsFactory implements ModuleFactory<ResultsModule> {

  @Override
  public ModuleDocumentation getDocumentation() {
    return ModuleDocumentation.builder()
        .category(ModuleDocumentation.ModuleCategory.ADVANCED)
        .name("Results")
        .tagName("results")
        .description(
            "The results module can be used to determine how and when a match should end, and how the winner should be decided.")
        .feature(FeatureDocumentation.builder()
            .name("Win Element")
            .tagName("win")
            .description(
                "The win element is used to specify when and how a competitor should win the match.")
            .attribute("scenario", new Attribute() {
              @Override
              public String getName() {
                return "End Scenario";
              }

              @Override
              public boolean isRequired() {
                return false;
              }

              @Override
              public String[] getDescription() {
                return new String[]{"The scenario that should be executed if the check passes."};
              }

              @Override
              public String[] getValues() {
                return new String[]{"team", "objectives"};
              }
            }, "objectives")
            .attribute("check", Attributes.check(true, "in order for the result to execute"))
            .attribute("places", new GenericAttribute(Integer.class, false,
                    "This attribute determines how many win places will be displayed and how many places of winners will receive some type of reward. This only works with the objectives result type and it is possible for multiple teams to score in the same place."),
                1)
            .build())
        .feature(FeatureDocumentation.builder()
            .name("End Scenarios")
            .description(
                "Scenarios are what course Atlas will take to determine a winner of a match. There are currently only 2 supported scenario results.")
            .table(new InfoTable("Scenario Types", "Name", "Description").row("objectives",
                "The objectives result type will choose the winner based on which team has the most objectives. This can also result in a match tie.")
                .row("team", "The team result type will reward the win to the team specified."))
            .build())
        .build();
  }

  @Override
  public Optional<ResultsModule> build(Match match, MatchFactory factory, XmlElement root)
      throws ModuleBuildException {
    List<XmlElement> elements = root.getChildren("results");

    List<EndScenario> scenarios = new ArrayList<>();

    if (!elements.isEmpty()) {
      for (XmlElement element : elements) {
        List<XmlElement> children = element.getDescendants("win");
        for (XmlElement child : children) {
          scenarios.add(parseScenario(match, child));
        }

        if (scenarios.isEmpty()) {
          throw new ModuleBuildException(this, "No win scenarios found in results element.");
        }
      }
    }

    return Optional.of(new ResultsModule(match, scenarios));
  }

  private EndScenario parseScenario(Match match, XmlElement element) throws XmlException {
    Check check = FactoryUtils
        .resolveRequiredCheckChild(match, element.getAttribute("check"), element.getChild("check"));
    int places = element.getAttribute("places").asInteger().orElse(1);

    switch (element.getAttribute("scenario").asString().orElse("objectives")) {
      case "team":
        Team team = match.getRegistry()
            .get(Team.class, element.getAttribute("team").asRequiredString(), true).get();
        return new TeamScenario(match, check, places, team);
      case "objectives":
        return new ObjectivesScenario(match, check, places);
      default:
        throw new XmlException(element.getAttribute("scenario"), "Undefined scenario supplied.");
    }
  }

}
