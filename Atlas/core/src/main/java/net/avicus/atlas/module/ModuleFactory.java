package net.avicus.atlas.module;

import java.util.Optional;
import net.avicus.atlas.documentation.ModuleDocumentation;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.util.xml.XmlElement;

public interface ModuleFactory<M extends Module> {

  Optional<M> build(Match match, MatchFactory factory, XmlElement root) throws ModuleBuildException;

  ModuleDocumentation getDocumentation();
}
