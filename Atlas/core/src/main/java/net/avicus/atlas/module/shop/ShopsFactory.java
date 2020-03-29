package net.avicus.atlas.module.shop;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.FeatureDocumentation.FeatureDocumentationBuilder;
import net.avicus.atlas.documentation.ModuleDocumentation;
import net.avicus.atlas.documentation.ModuleDocumentation.ModuleCategory;
import net.avicus.atlas.documentation.ModuleDocumentation.ModuleDocumentationBuilder;
import net.avicus.atlas.documentation.attributes.Attribute;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import net.avicus.atlas.module.FactoryUtils;
import net.avicus.atlas.module.ModuleBuildException;
import net.avicus.atlas.module.ModuleFactory;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.loadouts.LoadoutsFactory;
import net.avicus.atlas.module.locales.LocalesModule;
import net.avicus.atlas.module.locales.LocalizedXmlString;
import net.avicus.atlas.module.shop.items.ItemStackItem;
import net.avicus.atlas.util.ScopableItemStack;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.atlas.util.xml.named.NamedParser;
import net.avicus.atlas.util.xml.named.NamedParsers;
import net.avicus.magma.module.prestige.PrestigeLevel;
import org.apache.commons.lang3.tuple.Pair;

public class ShopsFactory implements ModuleFactory<ShopModule> {

  public final static List<FeatureDocumentationBuilder> FEATURES = Lists.newArrayList();
  public final static Table<Object, Method, Collection<String>> NAMED_PARSERS = HashBasedTable
      .create();
  private final static Map<String, Pair<Attribute, Object>> SHARED_SHOP_ITEM_ATTR = Maps
      .newHashMap();

  static {
    SHARED_SHOP_ITEM_ATTR.put("price",
        Pair.of(new GenericAttribute(Integer.class, true, "The price of the item"), null));
    SHARED_SHOP_ITEM_ATTR.put("prestige", Pair.of(new GenericAttribute(Integer.class, false,
        "The prestige level required to purchase the item."), 0));
    SHARED_SHOP_ITEM_ATTR.put("name",
        Pair.of(new GenericAttribute(LocalizedXmlString.class, true, "The name of the item."),
            null));
    SHARED_SHOP_ITEM_ATTR.put("description", Pair.of(
        new GenericAttribute(LocalizedXmlString.class, false, "The description of the item."),
        null));
    SHARED_SHOP_ITEM_ATTR
        .put("check", Pair.of(Attributes.check(true, "before the item can be purchased"), null));
    FEATURES.add(FeatureDocumentation.builder()
        .name("Item Stacks")
        .tagName("stack")
        .description("These can be used to give item stacks to players upon purchase.")
    );
  }

  public ShopsFactory() {
    NAMED_PARSERS.row(this).putAll(NamedParsers.methods(ShopsFactory.class));
  }

  @Override
  public ModuleDocumentation getDocumentation() {
    ModuleDocumentationBuilder builder = ModuleDocumentation.builder()
        .category(ModuleCategory.ADVANCED)
        .name("Shops Module")
        .tagName("shops");

    FeatureDocumentationBuilder shop = FeatureDocumentation.builder()
        .name("Shop")
        .tagName("shop")
        .attribute("id", Attributes.id(true))
        .attribute("name",
            new GenericAttribute(LocalizedXmlString.class, true, "The name of the shop."))
        .attribute("open-check", Attributes.check(true, "before the shop can be opened"));

    FeatureDocumentationBuilder points = FeatureDocumentation.builder()
        .name("Point Config")
        .description(
            "Configuration options regarding to how many points should be earned for performing actions.")
        .tagName("point-config");

    PointEarnConfig.CONFIGURABLES.forEach(a -> {
      points.attribute(a, new GenericAttribute(Integer.class, false,
          "The amount of points that should be earned when the performs the " + a + " action"), 0);
    });

    shop.subFeature(points.build());

    FeatureDocumentationBuilder items = FeatureDocumentation.builder()
        .name("Shop Items")
        .tagName("items")
        .description("These define which items are inside of the shop.");
    FEATURES.forEach(f -> {
      f.attributes(SHARED_SHOP_ITEM_ATTR);
      items.subFeature(f.build());
    });
    shop.subFeature(items.build());

    builder.feature(shop.build());

    return builder.build();
  }

  @Override
  public Optional<ShopModule> build(Match match, MatchFactory factory, XmlElement root)
      throws ModuleBuildException {

    List<Shop> shops = Lists.newArrayList();
    List<XmlElement> elements = root.getChildren("shops");

    elements.forEach(top ->
        top.getChildren("shop").forEach(el -> {
          String id = el.getAttribute("id").asRequiredString();

          String nameRaw = el.getAttribute("name").asRequiredString();
          LocalizedXmlString name = match.getRequiredModule(LocalesModule.class).parse(nameRaw);

          List<ShopItem> items = Lists.newArrayList();
          el.getRequiredChild("items").getChildren().forEach(i -> items.add(parseItem(match, i)));

          Check openCheck = FactoryUtils
              .resolveRequiredCheck(match, el.getAttribute("open-check"),
                  el.getChild("open-check"));
          PointListener listener = new PointListener(match);

          XmlElement earn = el.getRequiredChild("point-config");
          HashMap<String, Integer> points = Maps.newHashMap();
          PointEarnConfig.CONFIGURABLES.forEach(
              c -> earn.getChild(c).ifPresent(p -> points.put(c, p.getText().asRequiredInteger())));

          Shop shop = new Shop(id, match, new PointEarnConfig(points), items, name, openCheck,
              listener);

          shops.add(shop);
        })
    );

    return Optional
        .of(new ShopModule(match, shops));
  }

  private ShopItem parseItem(Match match, XmlElement element) {
    return NamedParsers
        .invokeMethod(NAMED_PARSERS, element, "Unknown item type.", new Object[]{match, element});
  }

  @NamedParser("item")
  public ItemStackItem parseStack(Match match, XmlElement element) {
    int price = element.getAttribute("price").asRequiredInteger();
    PrestigeLevel level = PrestigeLevel
        .fromID(element.getAttribute("prestige").asInteger().orElse(0));
    String nameRaw = element.getAttribute("name").asRequiredString();
    LocalizedXmlString name = match.getRequiredModule(LocalesModule.class).parse(nameRaw);

    List<LocalizedXmlString> description = Lists.newArrayList();
    element.getChild("description").ifPresent(c -> c.getChildren().forEach(l -> {
      String raw = l.getText().asRequiredString();
      description.add(match.getRequiredModule(LocalesModule.class).parse(raw));
    }));

    Check purchase = FactoryUtils
        .resolveRequiredCheck(match, element.getAttribute("check"), element.getChild("check"));

    ScopableItemStack stack = match.getFactory().getFactory(LoadoutsFactory.class)
        .parseItemStack(match, element.getRequiredChild("stack"));

    return new ItemStackItem(price, level, name, description, purchase, stack);
  }
}
