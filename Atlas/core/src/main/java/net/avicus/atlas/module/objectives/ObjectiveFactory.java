package net.avicus.atlas.module.objectives;

import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.util.xml.XmlElement;

public interface ObjectiveFactory<T extends Objective> {

  T build(Match match, MatchFactory factory, XmlElement element);

  FeatureDocumentation getDocumentation();
}
