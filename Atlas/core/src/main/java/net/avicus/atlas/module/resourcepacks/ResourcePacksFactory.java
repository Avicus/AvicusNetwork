package net.avicus.atlas.module.resourcepacks;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.ModuleDocumentation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.module.ModuleBuildException;
import net.avicus.atlas.module.ModuleFactory;
import net.avicus.atlas.module.locales.LocalesModule;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.util.xml.XmlElement;

public class ResourcePacksFactory implements ModuleFactory<RequestResourcePackModule> {

  @Override
  public ModuleDocumentation getDocumentation() {
    return ModuleDocumentation.builder()
        .category(ModuleDocumentation.ModuleCategory.ADVANCED)
        .name("Custom Resource Packs")
        .tagName("resource-packs")
        .tagName("request-resource-pack")
        .description("This module is used to request a custom resource pack during matches.")
        .feature(FeatureDocumentation.builder()
            .name("Pack")
            .tagName("resource-pack")
            .description(
                "A pack represents a resource pack that can be requested for a player to use.")
            .attribute("id", Attributes.id(true))
            .attribute("name", new GenericAttribute(LocalizedXmlString.class, true,
                "The name of the pack when used in UI."))
            .attribute("url", new GenericAttribute(URL.class, true,
                "The direct URL to a zipped version of the resource pack."))
            .build())
        .feature(FeatureDocumentation.builder()
            .name("Resource Pack Request")
            .tagName("request-resource-pack")
            .description(
                "This represents a request that is sent to a player to use a resource pack.")
            .attribute("id", Attributes.idOf(true, "resource pack", "The pack to be requested."))
            .attribute("force", new GenericAttribute(Boolean.class, false,
                "If the pack is required in order for the player to join teams."), false)
            .build())
        .build();
  }

  @Override
  public Optional<RequestResourcePackModule> build(Match match, MatchFactory factory,
      XmlElement root) throws ModuleBuildException {
    List<XmlElement> packs = root.getChildren("resource-packs");
    List<XmlElement> request = root.getChildren("request-resource-pack");

    if (packs.isEmpty() && request.isEmpty()) {
      return Optional.empty();
    }

    if (!packs.isEmpty()) {
      for (XmlElement child : packs.stream().flatMap(e -> e.getChildren("resource-pack").stream())
          .collect(Collectors.toList())) {
        String id = child.getAttribute("id").asRequiredString();
        String rawName = child.getAttribute("name").asRequiredString();
        LocalizedXmlString name = match.getRequiredModule(LocalesModule.class).parse(rawName);
        String url = child.getAttribute("url").asRequiredString();

        ResourcePack pack = new ResourcePack(id, name, url);
        match.getRegistry().add(pack);
      }
    }

    if (!request.isEmpty()) {
      String id = null;
      ResourcePack pack = null;
      boolean force = false;
      for (XmlElement element : request) {
        id = element.getAttribute("id").asString().orElse(id);
        pack = match.getRegistry().get(ResourcePack.class, id, true).orElse(pack);
        force = element.getAttribute("force").asBoolean().orElse(force);
      }
      if (id != null) {
        return Optional.of(new RequestResourcePackModule(match, pack, force));
      }
    }

    return Optional.empty();
  }

}
