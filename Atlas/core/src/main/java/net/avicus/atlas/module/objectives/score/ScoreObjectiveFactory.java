package net.avicus.atlas.module.objectives.score;

import java.util.Optional;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.module.groups.teams.Team;
import net.avicus.atlas.module.objectives.ObjectiveFactory;
import net.avicus.atlas.util.xml.XmlElement;

public class ScoreObjectiveFactory implements ObjectiveFactory<ScoreObjective> {

  @Override
  public FeatureDocumentation getDocumentation() {
    return FeatureDocumentation.builder()
        .name("Score")
        .tagName("scores").tagName("score")
        .description(
            "The score objective requires a team to reach a certain amount of points in order to be marked as completed.")
        .attribute("team", Attributes.idOf(false, "team",
            "The owner of the objective. (This will cause the objective to not be able to be completed by this team and the objective to be grouped under this team in the sidebar.)"))
        .attribute("kills", new GenericAttribute(Integer.class, false,
            "The amount of points that should be rewarded for a kill."))
        .attribute("deaths", new GenericAttribute(Integer.class, false,
            "The amount of points that should be deducted for a death."))
        .build();
  }

  @Override
  public ScoreObjective build(Match match, MatchFactory factory, XmlElement element) {
    element.inheritAttributes("scores");

    Optional<Integer> limit = element.getAttribute("limit").asInteger();
    Optional<Team> team = Optional.empty();
    if (element.hasAttribute("team")) {
      team = match.getRegistry()
          .get(Team.class, element.getAttribute("team").asRequiredString(), true);
    }

    Optional<Integer> kills = element.getAttribute("kills").asInteger();
    Optional<Integer> deaths = element.getAttribute("deaths").asInteger();

    return new ScoreObjective(match, limit, team, kills, deaths);
  }
}
