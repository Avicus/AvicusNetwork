package net.avicus.atlas.module.invsee;

import java.util.Optional;
import net.avicus.atlas.documentation.ModuleDocumentation;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.module.ModuleBuildException;
import net.avicus.atlas.module.ModuleFactory;
import net.avicus.atlas.util.xml.XmlElement;

public class InvSeeFactory implements ModuleFactory<InvSeeModule> {

  @Override
  public ModuleDocumentation getDocumentation() {
    // TODO: This should be a component.
    return null;
  }

  @Override
  public Optional<InvSeeModule> build(Match match, MatchFactory factory, XmlElement root)
      throws ModuleBuildException {
    // Always enabled
    return Optional.of(new InvSeeModule(match));
  }

}
