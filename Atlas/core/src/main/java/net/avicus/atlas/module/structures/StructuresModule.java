package net.avicus.atlas.module.structures;

import java.util.List;
import lombok.ToString;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.Module;
import net.avicus.atlas.module.executors.ExecutorsFactory;

@ToString
public class StructuresModule implements Module {

  static {
    ExecutorsFactory.registerDocumentation(() -> FeatureDocumentation.builder()
        .name("Paste Schematic")
        .tagName("paste-schematic")
        .description("This executor is used to paste a structure in the world.")
        .attribute("schematic", Attributes.idOf(true, "structure", "Structure to paste."))
        .attribute("natural", new GenericAttribute(Boolean.class, false,
                "If block place effects should be played when a block from the structure is placed."),
            false)
        .attribute("random", new GenericAttribute(Boolean.class, false,
            "If blocks should be placed in a random order."), false)
        .attribute("ignore-air", new GenericAttribute(Boolean.class, false,
            "If air blocks should be ignored when pasting in the rest of the blocks."), true)
        .attribute("paste-delay",
            Attributes.duration(false, true, "The delay between each block place."), 0)
        .attribute("region", Attributes.region(false,
            "The region that the schematic should be pasted in. If this is not supplied, the location will be determined from the execution context."))
        .build());
  }

  private final List<Schematic> loadedSchematics;

  public StructuresModule(Match match, List<Schematic> loadedSchematics) {
    this.loadedSchematics = loadedSchematics;

    match.getFactory().getFactory(ExecutorsFactory.class)
        .registerExecutor("paste-schematic", PasteSchematicExecutor::parse);
  }

  @Override
  public void open() {
    // This is here because we need a world in order to load schematics.
    this.loadedSchematics.forEach(Schematic::load);
  }
}
