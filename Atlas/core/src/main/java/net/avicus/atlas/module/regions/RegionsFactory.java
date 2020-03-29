package net.avicus.atlas.module.regions;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.avicus.atlas.SpecificationVersionHistory;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.ModuleDocumentation;
import net.avicus.atlas.documentation.SpecInformation;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.match.registry.RegisteredObject;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.Module;
import net.avicus.atlas.module.ModuleBuildException;
import net.avicus.atlas.module.ModuleFactory;
import net.avicus.atlas.module.ModuleFactorySort;
import net.avicus.atlas.module.regions.types.FillRegion;
import net.avicus.atlas.module.regions.types.VoidRegion;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.atlas.util.xml.XmlException;
import net.avicus.atlas.util.xml.named.NamedParser;
import net.avicus.atlas.util.xml.named.NamedParsers;
import net.avicus.compendium.inventory.MultiMaterialMatcher;
import net.avicus.magma.util.region.BoundedRegion;
import net.avicus.magma.util.region.Region;
import net.avicus.magma.util.region.modifiers.BoundedJoinRegion;
import net.avicus.magma.util.region.modifiers.BoundedSubtractRegion;
import net.avicus.magma.util.region.modifiers.BoundedTranslateRegion;
import net.avicus.magma.util.region.modifiers.IntersectRegion;
import net.avicus.magma.util.region.modifiers.InvertRegion;
import net.avicus.magma.util.region.modifiers.JoinRegion;
import net.avicus.magma.util.region.modifiers.SubtractRegion;
import net.avicus.magma.util.region.modifiers.TranslateRegion;
import net.avicus.magma.util.region.shapes.BlockRegion;
import net.avicus.magma.util.region.shapes.BoxRegion;
import net.avicus.magma.util.region.shapes.CircleRegion;
import net.avicus.magma.util.region.shapes.CuboidRegion;
import net.avicus.magma.util.region.shapes.CylinderRegion;
import net.avicus.magma.util.region.shapes.PointRegion;
import net.avicus.magma.util.region.shapes.RectangleRegion;
import net.avicus.magma.util.region.shapes.SphereRegion;
import net.avicus.magma.util.region.special.AboveRegion;
import net.avicus.magma.util.region.special.BelowRegion;
import net.avicus.magma.util.region.special.BoundsRegion;
import net.avicus.magma.util.region.special.EverywhereRegion;
import net.avicus.magma.util.region.special.NowhereRegion;
import net.avicus.magma.util.region.special.SectorRegion;
import org.bukkit.util.Vector;

@ModuleFactorySort(ModuleFactorySort.Order.EARLY)
public class RegionsFactory implements ModuleFactory<Module> {

  public final static Table<Object, Method, Collection<String>> NAMED_PARSERS = HashBasedTable
      .create();

  public final static List<FeatureDocumentation> FEATURES = Lists.newArrayList();

  public RegionsFactory() {
    NAMED_PARSERS.row(this).putAll(NamedParsers.methods(RegionsFactory.class));
  }

  @Override
  public ModuleDocumentation getDocumentation() {
    ModuleDocumentation.ModuleDocumentationBuilder builder = ModuleDocumentation.builder();

    builder
        .name("Regions")
        .tagName("regions")
        .description(
            "The regions element is used to store a collection of regions that can be referenced elsewhere by their ID.")
        .category(ModuleDocumentation.ModuleCategory.CORE);

    builder.feature(FeatureDocumentation.builder()
        .name("Region Reference")
        .tagName("region")
        .specInformation(SpecInformation.builder()
            .change(SpecificationVersionHistory.NEW_RECTANGLES, "All vectors now support decimals.")
            .build())
        .description(
            "You may use the <region> type to reference regions that are listed in the <regions> element by their id.")
        .attribute("id", Attributes.idOf(true, "region"))
        .build())
        .feature(FeatureDocumentation.builder()
            .name("Block")
            .tagName("block")
            .description("The block region represents a single block in the world.")
            .text(Attributes.vector(true, "The location of the block."))
            .build())
        .feature(FeatureDocumentation.builder()
            .name("Box")
            .tagName("box")
            .description(
                "A box is a helper region that will return a cuboid region based on supplied coordinate modifiers and a center.")
            .attribute("center", Attributes.vector(true, "The center of the box."))
            .attribute("x", new GenericAttribute(Integer.class, false,
                "Amount of blocks to expand the box on the X-axis"), 0)
            .attribute("y", new GenericAttribute(Integer.class, false,
                "Amount of blocks to expand the box on the Y-axis"), 0)
            .attribute("z", new GenericAttribute(Integer.class, false,
                "Amount of blocks to expand the box on the Z-axis"), 0)
            .build())
        .feature(FeatureDocumentation.builder()
            .name("circle")
            .tagName("circle")
            .description("A circle is essentially a cylinder with a height of 1.")
            .attribute("center", Attributes.vector(true, "The center of the circle."))
            .attribute("radius",
                new GenericAttribute(Integer.class, true, "The radius of the circle."))
            .build())
        .feature(FeatureDocumentation.builder()
            .name("Cuboid")
            .tagName("cuboid")
            .description("A cuboid is a three-dimensional box.")
            .attribute("min", Attributes.vector(true, "The minimum location of the region."))
            .attribute("max", Attributes.vector(true, "The maximum location of the region."))
            .build())
        .feature(FeatureDocumentation.builder()
            .name("Cylinder")
            .tagName("cylinder")
            .description("A cylinder is a 3-dimensional circle region.")
            .attribute("base", Attributes.vector(true, "The base of the cylinder."))
            .attribute("radius",
                new GenericAttribute(Integer.class, true, "The radius of the cylinder."))
            .attribute("radius",
                new GenericAttribute(Integer.class, true, "The height of the cylinder."))
            .build())
        .feature(FeatureDocumentation.builder()
            .name("Point")
            .tagName("point")
            .description("A point represents a location in the world.")
            .text(Attributes.vector(true, "The location."))
            .build())
        .feature(FeatureDocumentation.builder()
            .name("Rectangle")
            .tagName("rectangle")
            .description("A rectangle is a cuboid region that encompasses all y coordinates.")
            .attribute("min",
                new GenericAttribute(Integer.class, true, "Min location of the rectangle."))
            .attribute("max",
                new GenericAttribute(Integer.class, true, "Max location of the rectangle."))
            .specInformation(SpecInformation.builder()
                .breakingChange(SpecificationVersionHistory.NEW_RECTANGLES,
                    "Rectangles now require min/max values.")
                .build())
            .build())
        .feature(FeatureDocumentation.builder()
            .name("Sphere")
            .tagName("sphere")
            .description(
                "A sphere is a perfectly round geometrical object in three-dimensional space that is the surface of a completely round ball")
            .attribute("origin", Attributes.vector(true, "The origin of the sphere."))
            .attribute("radius",
                new GenericAttribute(Integer.class, true, "The radius of the sphere."))
            .build())
        .feature(FeatureDocumentation.builder()
            .name("Intersect")
            .tagName("intersect")
            .description(
                "This region is comprised of points that are shared by all of the nested regions.")
            .build())
        .feature(FeatureDocumentation.builder()
            .name("Invert")
            .tagName("invert")
            .description(
                "The invert region takes the inverse of the regions provided within the element. For example, if a single point is provided, the inverted version of that region would be every point in the world excluding that point.")
            .build())
        .feature(FeatureDocumentation.builder()
            .name("Join")
            .tagName("join")
            .description(
                "Provide any number of regions within this region type to create a region that combines all the points of the nested regions.")
            .build())
        .feature(FeatureDocumentation.builder()
            .name("Subtract")
            .tagName("subtract")
            .description(
                "The subtract region takes the first listed region and for each successive region listed, removes points from it.")
            .build())
        .feature(FeatureDocumentation.builder()
            .name("Translate")
            .tagName("translate")
            .description("This translates all the regions nested within it by an offset.")
            .attribute("offset",
                Attributes.vector(true, "Amount of blocks to offset the nested regions by."))
            .build())
        .feature(FeatureDocumentation.builder()
            .name("Above")
            .tagName("above")
            .description(
                "The above region select any coordinates above the supplied x, y, or z values.")
            .attribute("x", new GenericAttribute(Integer.class, false,
                "The base block to select above on the X-axis"))
            .attribute("y", new GenericAttribute(Integer.class, false,
                "The base block to select above on the Y-axis"))
            .attribute("z", new GenericAttribute(Integer.class, false,
                "The base block to select above on the Z-axis"))
            .build())
        .feature(FeatureDocumentation.builder()
            .name("Below")
            .tagName("below")
            .description(
                "The below region select any coordinates below the supplied x, y, or z values.")
            .attribute("x", new GenericAttribute(Integer.class, false,
                "The base block to select below on the X-axis"))
            .attribute("y", new GenericAttribute(Integer.class, false,
                "The base block to select below on the Y-axis"))
            .attribute("z", new GenericAttribute(Integer.class, false,
                "The base block to select below on the Z-axis"))
            .build())
        .feature(FeatureDocumentation.builder()
            .name("Bounds")
            .tagName("bounds")
            .description(
                "The bounds region selects the area around the region specified. Similar to the WorldEdit //walls command.")
            .attribute("region", Attributes.region(true, "The region that should be bounded"))
            .attribute("x-axis", new GenericAttribute(Boolean.class, false,
                "If the bounds on the X-axis should be selected."), true)
            .attribute("y-axis", new GenericAttribute(Boolean.class, false,
                "If the bounds on the Y-axis should be selected."), true)
            .attribute("z-axis", new GenericAttribute(Boolean.class, false,
                "If the bounds on the Z-axis should be selected."), true)
            .build())
        .feature(FeatureDocumentation.builder()
            .name("Fill")
            .tagName("fill")
            .description(
                "The fill region represents the coordinates of each block matching the defined material within the bounds supplied.")
            .attribute("bounds",
                Attributes.region(true, "The region that contains the blocks to search for."))
            .attribute("materials",
                Attributes.materialMatcher(true, true, "The blocks to search for."))
            .attribute("start", Attributes.vector(false,
                "Location to start the search from. (Defaults to center of boundary)"))
            .attribute("connect", new GenericAttribute(Boolean.class, false,
                    "If only connected blocks from the start should be matched.",
                    "If false, all blocks, regardless of connection, inside of the region will be checked."),
                false)
            .specInformation(SpecInformation.builder()
                .breakingChange(SpecificationVersionHistory.NEW_RECTANGLES,
                    "Connected now defaults to false.")
                .build())
            .build())
        .feature(FeatureDocumentation.builder()
            .name("Sector")
            .tagName("sector")
            .description(
                "The sector region represents a sector of a circle that covers all Y values.")
            .attribute("x",
                new GenericAttribute(Integer.class, false, "The base of the circle on the X-axis"))
            .attribute("z",
                new GenericAttribute(Integer.class, false, "The base of the circle on the Z-axis"))
            .attribute("start",
                new GenericAttribute(Double.class, true, "The angle that the sector should begin."))
            .attribute("end",
                new GenericAttribute(Double.class, true, "The angle that the sector should end."))
            .build())
        .feature(FeatureDocumentation.builder()
            .name("Everywhere")
            .tagName("everywhere")
            .description("This represents every coordinate in the world.")
            .build())
        .feature(FeatureDocumentation.builder()
            .name("Nowhere")
            .tagName("nowhere")
            .description("This represents no coordinate in the world.")
            .build())
        .feature(FeatureDocumentation.builder()
            .name("Void")
            .tagName("void")
            .specInformation(SpecInformation.builder()
                .change(SpecificationVersionHistory.LOADOUT_SUB_TAG,
                    "Void regions now have configurable min/max/ignored-blocks")
                .build())
            .description("The void region selects all blocks that have only air under it.")
            .attribute("min", new GenericAttribute(Integer.class, false,
                "The y-coordinate to start looking for air at"), 0)
            .attribute("max", new GenericAttribute(Integer.class, false,
                "The y-coordinate to stop looking for air at"), 5)
            .attribute("ignored-blocks",
                Attributes.materialMatcher(false, true, "Blocks which should be counted as air."))
            .build());

    FEATURES.forEach(builder::feature);

    return builder.build();
  }

  @Override
  public Optional<Module> build(Match match, MatchFactory factory, XmlElement root)
      throws ModuleBuildException {
    List<XmlElement> elements = root.getChildren("regions");

    // built-in regions
    match.getRegistry().add(new RegisteredObject<>("everywhere", new EverywhereRegion()));
    match.getRegistry().add(new RegisteredObject<>("nowhere", new NowhereRegion()));

    if (elements.isEmpty()) {
      return Optional.empty();
    }

    elements.forEach(element -> {
      for (XmlElement child : element.getChildren()) {
        String id = child.getAttribute("id").asRequiredString();
        Region region = parseRegion(match, child);
        match.getRegistry().add(new RegisteredObject<>(id, region));
      }
    });

    return Optional.empty();
  }

  public Region parseRegionAs(Match match, XmlElement element, Class<? extends Region> type) {
    if (element.getName().equalsIgnoreCase("region") && element.getChildren().isEmpty()) {
      return parseRegionId(match, element);
    }
    if (type == Region.class) {
      return parseJoin(match, element);
    } else if (type == BoundedJoinRegion.class) {
      return parseJoin(match, element);
    } else if (type == BoundedRegion.class) {
      return parseJoin(match, element);
    }

    return NamedParsers
        .invokeMethod(NAMED_PARSERS, element, "Unknown region type.", new Object[]{match, element});
  }

  public Region parseRegion(Match match, XmlElement element) {
    return NamedParsers
        .invokeMethod(NAMED_PARSERS, element, "Unknown region type.", new Object[]{match, element});
  }

  @NamedParser("region")
  public Region parseRegionId(Match match, XmlElement element) {
    String id = element.getAttribute("id").asRequiredString();
    return match.getRegistry().get(Region.class, id, true).get();
  }

  @NamedParser("block")
  public BlockRegion parseBlock(Match match, XmlElement element) {
    Vector vector = element.getText().asRequiredVector();
    return new BlockRegion(vector);
  }

  @NamedParser("box")
  public BoxRegion parseBox(Match match, XmlElement element) {
    Vector center = element.getAttribute("center").asRequiredVector();
    int x = element.getAttribute("x").asInteger().orElse(0);
    int y = element.getAttribute("y").asInteger().orElse(0);
    int z = element.getAttribute("z").asInteger().orElse(0);
    return new BoxRegion(center, x, y, z);
  }

  @NamedParser("circle")
  public CircleRegion parseCircle(Match match, XmlElement element) {
    Vector center = element.getAttribute("center").asRequiredVector();
    int radius = element.getAttribute("radius").asRequiredInteger();
    return new CircleRegion(center, radius);
  }

  @NamedParser("cuboid")
  public CuboidRegion parseCuboid(Match match, XmlElement element) {
    Vector min = element.getAttribute("min").asRequiredVector();
    Vector max = element.getAttribute("max").asRequiredVector();
    return new CuboidRegion(min, max);
  }

  @NamedParser("cylinder")
  public CylinderRegion parseCylinder(Match match, XmlElement element) {
    Vector base = element.getAttribute("base").asRequiredVector();
    int radius = element.getAttribute("radius").asRequiredInteger();
    int height = element.getAttribute("height").asRequiredInteger();
    return new CylinderRegion(base, radius, height);
  }

  @NamedParser("point")
  public PointRegion parsePoint(Match match, XmlElement element) {
    Vector vector = element.getText().asRequiredVector();
    return new PointRegion(vector);
  }

  @NamedParser("rectangle")
  public RectangleRegion parseRectangle(Match match, XmlElement element) {
    if (match.getMap().getVersion().greaterEqual(SpecificationVersionHistory.NEW_RECTANGLES)) {
      String[] min = element.getAttribute("min").asRequiredString().split(",");
      String[] max = element.getAttribute("max").asRequiredString().split(",");
      if (min.length != 2 || max.length != 2) {
        throw new XmlException(element, "Min/Max coordinates must be in the \"x, z\" format");
      }

      try {
        double xMin = Double.valueOf(min[0]);
        double zMin = Double.valueOf(min[1]);
        double xMax = Double.valueOf(max[0]);
        double zMax = Double.valueOf(max[0]);

        return new RectangleRegion(xMin, zMin, xMax, zMax);
      } catch (NumberFormatException e) {
        throw new XmlException(element, "Supplied vector is not valid!");
      }
    } else {
      int x = element.getAttribute("x").asRequiredInteger();
      int z = element.getAttribute("z").asRequiredInteger();
      return new RectangleRegion(x, z, x, z);
    }
  }

  @NamedParser("sphere")
  public SphereRegion parseSphere(Match match, XmlElement element) {
    Vector origin = element.getAttribute("origin").asRequiredVector();
    int radius = element.getAttribute("radius").asRequiredInteger();
    return new SphereRegion(origin, radius);
  }

  @NamedParser("intersect")
  public IntersectRegion parseIntersect(Match match, XmlElement element) {
    JoinRegion children = parseJoin(match, element);
    return new IntersectRegion(children);
  }

  @NamedParser("invert")
  public InvertRegion parseInvert(Match match, XmlElement element) {
    JoinRegion children = parseJoin(match, element);
    return new InvertRegion(children);
  }

  @NamedParser("join")
  @SuppressWarnings("unchecked")
  public <T extends JoinRegion> T parseJoin(Match match, XmlElement element) {
    List<Region> list = new ArrayList<>();
    List<BoundedRegion> bounded = new ArrayList<>();

    List<XmlElement> children = element.getChildren();

    for (XmlElement child : children) {
      Region region = parseRegion(match, child);
      list.add(region);
      if (region instanceof BoundedRegion) {
        bounded.add((BoundedRegion) region);
      }
    }

    if (list.isEmpty()) {
      throw new XmlException(element, "Join regions require children.");
    }

    if (bounded.size() == list.size()) {
      return (T) new BoundedJoinRegion(bounded);
    }
    return (T) new JoinRegion(list);
  }

  @NamedParser("subtract")
  public SubtractRegion parseSubtract(Match match, XmlElement element) {
    JoinRegion children = parseJoin(match, element);
    if (children instanceof BoundedJoinRegion) {
      return new BoundedSubtractRegion(children);
    } else {
      return new SubtractRegion(children);
    }
  }

  @NamedParser("translate")
  public TranslateRegion parseTranslate(Match match, XmlElement element) {
    Vector offset = element.getAttribute("offset").asRequiredVector();
    JoinRegion children = parseJoin(match, element);
    if (children instanceof BoundedRegion) {
      return new BoundedTranslateRegion((BoundedRegion) children, offset);
    }
    return new TranslateRegion(children, offset);
  }

  @NamedParser("above")
  public AboveRegion parseAbove(Match match, XmlElement element) {
    Optional<Integer> x = element.getAttribute("x").asInteger();
    Optional<Integer> y = element.getAttribute("y").asInteger();
    Optional<Integer> z = element.getAttribute("z").asInteger();

    return new AboveRegion(x, y, z);
  }

  @NamedParser("below")
  public BelowRegion parseBelow(Match match, XmlElement element) {
    Optional<Integer> x = element.getAttribute("x").asInteger();
    Optional<Integer> y = element.getAttribute("y").asInteger();
    Optional<Integer> z = element.getAttribute("z").asInteger();

    return new BelowRegion(x, y, z);
  }

  @NamedParser("bounds")
  public BoundsRegion parseBounds(Match match, XmlElement element) {
    BoundedRegion base = FactoryUtils
        .resolveRequiredRegionChild(match, BoundedRegion.class, element.getAttribute("region"),
            element.getChild("region"));
    boolean xSide = element.getAttribute("x-axis").asBoolean().orElse(true);
    boolean ySide = element.getAttribute("y-axis").asBoolean().orElse(true);
    boolean zSide = element.getAttribute("z-axis").asBoolean().orElse(true);

    return new BoundsRegion(base, xSide, ySide, zSide);
  }

  @NamedParser("fill")
  public FillRegion parseFill(Match match, XmlElement element) {
    BoundedRegion bounds = FactoryUtils
        .resolveRequiredRegionAs(match, BoundedRegion.class, element.getAttribute("bounds"),
            element.getChild("bounds"));
    MultiMaterialMatcher materials = element.getAttribute("materials")
        .asRequiredMultiMaterialMatcher();
    Optional<Vector> start = element.getAttribute("start").asVector();
    boolean connect = element.getAttribute("connected").asBoolean().orElse(false);

    return new FillRegion(match, bounds, materials, start, connect);
  }

  @NamedParser("sector")
  public SectorRegion parseSector(Match match, XmlElement element) {
    int x = element.getAttribute("x").asRequiredInteger();
    int z = element.getAttribute("z").asRequiredInteger();
    double start = element.getAttribute("start").asRequiredDouble();
    double end = element.getAttribute("end").asRequiredDouble();
    return new SectorRegion(x, z, start, end);
  }

  @NamedParser("everywhere")
  public EverywhereRegion parseEverywhere(Match match, XmlElement element) {
    return new EverywhereRegion();
  }

  @NamedParser("nowhere")
  public NowhereRegion parseNowhere(Match match, XmlElement element) {
    return new NowhereRegion();
  }

  @NamedParser("void")
  public VoidRegion parseVoid(Match match, XmlElement element) {
    int min = element.getAttribute("min").asInteger().orElse(0);
    int max = element.getAttribute("max").asInteger().orElse(5);
    Optional<MultiMaterialMatcher> ignoredBlocks = element.getAttribute("ignored-blocks")
        .asMultiMaterialMatcher();
    return new VoidRegion(match, min, max, ignoredBlocks);
  }
}
