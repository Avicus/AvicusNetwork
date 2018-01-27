package net.avicus.atlas.module.executors.types;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.ToString;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.executors.Executor;
import net.avicus.atlas.module.regions.types.FillRegion;
import net.avicus.atlas.module.regions.types.VoidRegion;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.atlas.util.xml.XmlException;
import net.avicus.magma.util.region.Region;
import net.avicus.magma.util.region.special.SectorRegion;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

/**
 * An executor that will rapidly replace blocks inside of a region.
 */
@ToString(exclude = "match")
public class BlockReplaceExecutor extends Executor {

  private final Match match;
  private final Region region;
  private final Material find;
  private final MaterialData replace;

  public BlockReplaceExecutor(String id, Check check, Match match, Region region, Material find,
      MaterialData replace) {
    super(id, check);
    this.match = match;
    this.region = region;
    this.find = find;
    this.replace = replace;
  }

  public static FeatureDocumentation documentation() {
    return FeatureDocumentation.builder()
        .name("Replace Blocks")
        .tagName("replace-block")
        .description("An executor that will rapidly replace blocks inside of a region.")
        .attribute("find", Attributes.materialMatcher(true, false, "Material to search for."))
        .attribute("replace",
            Attributes.materialMatcher(true, false, "Material to replace found blocks with."))
        .build();
  }

  public static Executor parse(Match match, XmlElement element) throws XmlException {
    Check check = FactoryUtils
        .resolveRequiredCheckChild(match, element.getAttribute("check"), element.getChild("check"));
    String id = element.getAttribute("id").asString().orElse(UUID.randomUUID().toString());
    Region region = FactoryUtils
        .resolveRequiredRegionChild(match, Region.class, element.getAttribute("region"),
            element.getChild("region"));
    if (region instanceof FillRegion || region instanceof SectorRegion
        || region instanceof VoidRegion) {
      throw new XmlException(element, "This region type is not supported for this executor.");
    }
    Material find = element.getAttribute("find").asRequiredMaterialMatcher().getMaterial();
    MaterialData replace = element.getAttribute("replace").asRequiredMaterialMatcher()
        .toMaterialData();
    return new BlockReplaceExecutor(id, check, match, region, find, replace);
  }

  @Override
  public void execute(CheckContext context) {
    Set<Vector> vectors = new HashSet<>();
    Set<Chunk> chunks = this.region.getChunks(this.match.getWorld());
    chunks.forEach(chunk -> vectors.addAll(
        chunk.getBlocks(this.find).stream().map(block -> block.getLocation().toVector())
            .filter(this.region::contains).collect(Collectors.toSet())));
    this.match.getWorld().fastBlockChange(vectors, this.replace);
  }
}
