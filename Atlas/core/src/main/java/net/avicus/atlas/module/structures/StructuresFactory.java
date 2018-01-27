package net.avicus.atlas.module.structures;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.ModuleDocumentation;
import net.avicus.atlas.documentation.attributes.Attribute;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.ModuleBuildException;
import net.avicus.atlas.module.ModuleFactory;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.atlas.util.xml.XmlException;
import net.avicus.magma.util.region.BoundedRegion;

public class StructuresFactory implements ModuleFactory<StructuresModule> {

  @Override
  public ModuleDocumentation getDocumentation() {
    return ModuleDocumentation.builder()
        .category(ModuleDocumentation.ModuleCategory.ADVANCED)
        .name("Schematic Loading/Creation")
        .tagName("structures")
        .description(
            "This module is used to load schematic files for use with the paste-schematic executor.")
        .feature(FeatureDocumentation.builder()
            .name("Schematic")
            .tagName("schematic")
            .description(
                "These represent either schematic files or regions which can be used later on as objects to be pasted in to the match world.")
            .attribute("type", new Attribute() {
              @Override
              public String getName() {
                return "Structure Type";
              }

              @Override
              public String[] getValues() {
                return new String[]{
                    "file - Should be loaded from a file",
                    "region - Should be loaded from the match world based on a region"
                };
              }

              @Override
              public boolean isRequired() {
                return true;
              }

              @Override
              public String[] getDescription() {
                return new String[]{
                    "The type of structure loading method that should be used for this schematic."};
              }
            })
            .attribute("id", Attributes.id(true))
            .attribute("source (Files)", new GenericAttribute(String.class, true,
                "The name of the schematic file to be loaded.",
                "Schematics should be placed in the schematics folder of the map folder.",
                "The source should not include the .schematic extension."
            ))
            .attribute("source (Regions)", Attributes.region(true,
                "The region which the blocks should be copied from.",
                "The copy is made directly after the match is loaded.",
                "It should be noted that the original blocks are not cleared from the world after loading."
            ))
            .build())
        .build();
  }

  @Override
  public Optional<StructuresModule> build(Match match, MatchFactory factory, XmlElement root)
      throws ModuleBuildException {
    if (root.getChildren("structures").isEmpty()) {
      return Optional.empty();
    }

    List<Schematic> loadedSchematics = new ArrayList<>();

    for (XmlElement element : root.getChildren("structures")) {
      for (XmlElement schematic : element.getChildren("schematic")) {
        String id = schematic.getAttribute("id").asRequiredString();
        switch (schematic.getAttribute("type").asRequiredString()) {
          case "file":
            loadedSchematics.add(new FileSchematic(id, new File(
                match.getFolder().getAbsolutePath() + "/schematics/" + schematic
                    .getAttribute("source").asRequiredString() + ".schematic")));
            continue;
          case "region":
            BoundedRegion region = FactoryUtils
                .resolveRequiredRegionChild(match, BoundedRegion.class,
                    schematic.getAttribute("source"), schematic.getChild("source"));

            Schematic loaded = new BoundedRegionSchematic(id, region, match);
            loadedSchematics.add(loaded);
            continue;
          default:
            throw new XmlException(schematic, "Unknown schematic type.");
        }
      }
    }

    loadedSchematics.forEach(match.getRegistry()::add);

    return Optional.of(new StructuresModule(match, loadedSchematics));
  }
}
