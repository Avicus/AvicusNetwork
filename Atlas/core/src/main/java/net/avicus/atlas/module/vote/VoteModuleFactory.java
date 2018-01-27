package net.avicus.atlas.module.vote;

import java.util.Optional;
import net.avicus.atlas.documentation.ModuleDocumentation;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.module.ModuleBuildException;
import net.avicus.atlas.module.ModuleFactory;
import net.avicus.atlas.util.xml.XmlElement;

public final class VoteModuleFactory implements ModuleFactory<VoteModule> {

  @Override
  public ModuleDocumentation getDocumentation() {
    // TODO: This should be a component.
    return null;
  }

  @Override
  public Optional<VoteModule> build(final Match match, final MatchFactory factory,
      final XmlElement root) throws ModuleBuildException {
    return Optional.of(new VoteModule(match));
  }
}
