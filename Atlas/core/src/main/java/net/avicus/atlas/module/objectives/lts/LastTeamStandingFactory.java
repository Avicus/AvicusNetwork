package net.avicus.atlas.module.objectives.lts;

import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.module.groups.teams.Team;
import net.avicus.atlas.module.objectives.ObjectiveFactory;
import net.avicus.atlas.util.xml.XmlElement;

public class LastTeamStandingFactory implements ObjectiveFactory<LastTeamStanding> {

  @Override
  public FeatureDocumentation getDocumentation() {
    return FeatureDocumentation.builder()
        .name("Last Team Standing")
        .tagName("last-team-standing")
        .description(
            "The last team standing objective is used to mark the winner of the match as the team with any players on it after all others are empty.")
        .description("These should be declared for every team if every team is allowed to win.")
        .attribute("id", Attributes.id(false))
        .attribute("team",
            Attributes.idOf(true, "team", "Team that would win if all others are empty."))
        .build();
  }

  @Override
  public LastTeamStanding build(Match match, MatchFactory factory, XmlElement element) {
    Team team = match.getRegistry()
        .get(Team.class, element.getAttribute("team").asRequiredString(), true).get();
    return new LastTeamStanding(match, team);
  }
}
