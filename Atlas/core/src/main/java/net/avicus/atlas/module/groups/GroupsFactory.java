package net.avicus.atlas.module.groups;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.ModuleDocumentation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.EnumAttribute;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.module.ModuleBuildException;
import net.avicus.atlas.module.ModuleFactory;
import net.avicus.atlas.module.ModuleFactorySort;
import net.avicus.atlas.module.groups.ffa.FFAModule;
import net.avicus.atlas.module.groups.ffa.FFATeam;
import net.avicus.atlas.module.groups.teams.Team;
import net.avicus.atlas.module.groups.teams.TeamsModule;
import net.avicus.atlas.module.locales.LocalesModule;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.util.Messages;
import net.avicus.atlas.util.color.TeamColor;
import net.avicus.atlas.util.xml.XmlElement;

@ModuleFactorySort(ModuleFactorySort.Order.EARLY)
public class GroupsFactory implements ModuleFactory<GroupsModule> {

  @Override
  public ModuleDocumentation getDocumentation() {
    return ModuleDocumentation.builder()
        .category(ModuleDocumentation.ModuleCategory.CORE)
        .name("Groups")
        .tagName("teams")
        .tagName("ffa")
        .description(
            "This module is used to define how many and what attributes teams should possess in the match.")
        .description(
            "Note that the team name is not an attribute but rather is defined as text within the element. Team names should be localized, short, and easy to remember.")
        .feature(FeatureDocumentation.builder()
            .name("Teams")
            .tagName("team")
            .description("Teams represent groups of players in the match with a specific color.")
            .attribute("id", Attributes.id(true))
            .attribute("color", new EnumAttribute(TeamColor.class, true, "The color of the team."))
            .attribute("min", new GenericAttribute(Integer.class, true,
                "The minimum number of players on the team for the match to start."))
            .attribute("max", new GenericAttribute(Integer.class, true,
                "The soft maximum number of players allowed on the team.",
                "Premium users and staff are able to join beyond this value."))
            .attribute("max-overfill", new GenericAttribute(Integer.class, false,
                "The maximum number of players allowed on the team."), "1.25 * max")
            .text(new GenericAttribute(LocalizedXmlString.class, true, "The name of theam/"))
            .build())
        .feature(FeatureDocumentation.builder()
            .name("FFA")
            .tagName("ffa")
            .description(
                "When FFA is enabled, players are placed onto their own teams and optionally assigned different colors.")
            .attribute("id", Attributes.id(true))
            .attribute("min", new GenericAttribute(Integer.class, true,
                "The minimum number of players playing for the match to start."))
            .attribute("max", new GenericAttribute(Integer.class, true,
                "The soft maximum number of players allowed in the match.",
                "Premium users and staff are able to join beyond this value."))
            .attribute("max-overfill", new GenericAttribute(Integer.class, false,
                "The maximum number of players allowed in the match."), "1.25 * max")
            .attribute("colorize", new GenericAttribute(Boolean.class, false,
                    "If each player should be assigned their own color out of all of the possible team colors."),
                true)
            .attribute("friendly-fire",
                new GenericAttribute(Boolean.class, false, "If players can hit each other."), true)
            .build())
        .build();
  }

  @Override
  public Optional<GroupsModule> build(Match match, MatchFactory factory, XmlElement root)
      throws ModuleBuildException {
    List<XmlElement> teams = root.getChildren("teams");
    List<XmlElement> ffa = root.getChildren("ffa");

    if (!teams.isEmpty() && !ffa.isEmpty()) {
      throw new ModuleBuildException(this, "Modules <teams> and <ffa> cannot be both present.");
    }

    if (!teams.isEmpty()) {
      return Optional.of(parseTeams(match, teams));
    } else {
      return Optional.of(parseFFA(match, ffa));
    }
  }

  private TeamsModule parseTeams(Match match, List<XmlElement> elements) {
    List<Team> teams = Lists.newArrayList();
    CompetitorRule rule = CompetitorRule.TEAM;
    boolean lockPlayers = false;

    for (XmlElement element : elements) {
      teams.addAll(element.getChildren()
          .stream()
          .map(child -> buildTeam(match, child))
          .collect(Collectors.toList()));

      // register ids
      match.getRegistry().add(teams);

      // competitor rule
      rule = element.getAttribute("competitor").asEnum(CompetitorRule.class, true).orElse(rule);

      lockPlayers = element.getAttribute("lock-players").asBoolean().orElse(lockPlayers);
    }

    Spectators spectators = new Spectators();
    match.getRegistry().add(spectators);

    return new TeamsModule(match, teams, rule, spectators, lockPlayers);
  }

  private Team buildTeam(Match match, XmlElement element) {
    String nameRaw = element.getText().asRequiredString();
    LocalizedXmlString name = match.getRequiredModule(LocalesModule.class).parse(nameRaw);

    String id = element.getAttribute("id").asRequiredString();
    TeamColor color = element.getAttribute("color").asRequiredEnum(TeamColor.class, true);
    int min = element.getAttribute("min").asRequiredInteger();
    int max = element.getAttribute("max").asRequiredInteger();

    int defaultOverfill = (int) Math.floor((double) max * 1.25);
    int overfill = element.getAttribute("max-overfill").asInteger().orElse(defaultOverfill);

    return new Team(id, name, color, min, max, overfill);
  }

  private FFAModule parseFFA(Match match, List<XmlElement> elements) {
    LocalizedXmlString name = new LocalizedXmlString(Messages.UI_PLAYERS);

    int min = 0;
    int max = 0;
    int overfill = 0;
    boolean colorize = true;
    boolean friendlyFire = true;

    for (XmlElement element : elements) {
      if (element.hasAttribute("name")) {
        String nameRaw = element.getAttribute("name").asRequiredString();
        name = match.getRequiredModule(LocalesModule.class).parse(nameRaw);
      }

      min = element.getAttribute("min").asInteger().orElse(min);
      max = element.getAttribute("max").asInteger().orElse(max);

      int defaultOverfill = (int) Math.floor((double) max * 1.25);

      overfill = element.getAttribute("overfill").asInteger().orElse(defaultOverfill);

      colorize = element.getAttribute("colorize").asBoolean().orElse(colorize);

      friendlyFire = element.getAttribute("friendly-fire").asBoolean().orElse(friendlyFire);
    }

    FFATeam team = new FFATeam(name, min, max, overfill, colorize, friendlyFire);

    Spectators spectators = new Spectators();
    match.getRegistry().add(team);
    match.getRegistry().add(spectators);

    return new FFAModule(match, team, spectators);
  }

}
