package net.avicus.atlas.match;

import java.io.File;
import java.net.URL;
import java.time.Month;
import java.util.UUID;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.GameType;
import net.avicus.atlas.SpecificationVersionHistory;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.InfoTable;
import net.avicus.atlas.documentation.ModuleDocumentation;
import net.avicus.atlas.documentation.attributes.Attribute;
import net.avicus.atlas.documentation.attributes.Attributes;
import net.avicus.atlas.documentation.attributes.EnumAttribute;
import net.avicus.atlas.documentation.attributes.GenericAttribute;
import net.avicus.atlas.documentation.attributes.RangeAttribute;
import net.avicus.magma.util.MapGenre;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;

public class StaticDocumentations {

  public static ModuleDocumentation root() {
    return ModuleDocumentation.builder()
        .name("Root Map Element")
        .tagName("map")
        .category(ModuleDocumentation.ModuleCategory.CORE)
        .description(
            "This is the main, root element the all maps must provide. Every subsequent element is nested inside the map element. Note that for an XML file to be valid, it must contain the <?xml version=\"1.0\"?> header.")
        .feature(FeatureDocumentation.builder()
            .name("Main Map Configuration")
            .description("These attributes contain key configuration options about the map.")
            .attribute("name", new GenericAttribute(String.class, true, "The name of the map."))
            .attribute("slug", new GenericAttribute(String.class, false,
                "The network identifiable slug for the map. THis is used for things such as ratings and stats.",
                "This should usually be left to Atlas, unless you change the name of the map and want to keep ratings data."))
            .attribute("spec", new Attribute() {
              @Override
              public String getName() {
                return "Specification Version";
              }

              @Override
              public boolean isRequired() {
                return true;
              }

              @Override
              public String[] getDescription() {
                return new String[]{"The version of the Atlas XML specification."};
              }

              @Override
              public String[] getValues() {
                return new String[]{"1.0.0 - " + SpecificationVersionHistory.CURRENT.toString()};
              }
            })
            .attribute("version", new Attribute() {
              @Override
              public String getName() {
                return "Semantic Version";
              }

              @Override
              public boolean isRequired() {
                return true;
              }

              @Override
              public String[] getDescription() {
                return new String[]{"The version of the map."};
              }

              @Override
              public String getLink() {
                return "http://semver.org";
              }
            })
            .attribute("genre", new EnumAttribute(MapGenre.class, false,
                "The genre of the map. Atlas will attempt to determine the genre based on the modules used in the map if this is not provided."))
            .subFeature(FeatureDocumentation.builder()
                .name("Game Types")
                .tagName("gametype")
                .description(
                    "Game Types are used to group maps with similar objectives together for various UI applications.")
                .text(new EnumAttribute(GameType.class, true, "The game type of this map."))
                .build())
            .build())
        .build();
  }

  public static ModuleDocumentation authors() {
    return ModuleDocumentation.builder()
        .name("Authors and Contributors")
        .tagName("authors")
        .description(
            "The authors element is provided to give credit to the people that made contributions to the creation of a map. Their minecraft usernames are listed in a variety of locations while the map is being played.")
        .category(ModuleDocumentation.ModuleCategory.CORE)
        .feature(FeatureDocumentation.builder()
            .name("Author")
            .tagName("author")
            .description("An author is a person who contributed to the map in some way.")
            .attribute("contributor", new GenericAttribute(Boolean.class, false,
                "If a player should be counted as a main author or as a contributor."), false)
            .attribute("uuid", new GenericAttribute(UUID.class, true,
                "The UUID of the player that should be credited."))
            .attribute("role", new GenericAttribute(String.class, false,
                "The specific task that the user performed."))
            .attribute("promo",
                new GenericAttribute(URL.class, false, "A link to the player's website."))
            .build())
        .feature(FeatureDocumentation.builder()
            .name("Organisation ")
            .tagName("organization")
            .description("An organisation represents a group of people who helped with the map.")
            .attribute("name",
                new GenericAttribute(String.class, true, "The name of the organisation."))
            .attribute("role", new GenericAttribute(String.class, false,
                "The specific task that the group performed."))
            .attribute("promo",
                new GenericAttribute(URL.class, false, "A link to the group's website."))
            .build())
        .build();
  }

  public static ModuleDocumentation includes() {
    ModuleDocumentation.ModuleDocumentationBuilder builder = ModuleDocumentation.builder()
        .name("XML Includes")
        .tagName("include")
        .description(
            "Includes are used to split a large and redundant configuration file into multiple smaller files. <include> elements should only be found directly as a child of the main <map> element.")
        .category(ModuleDocumentation.ModuleCategory.CORE)
        .feature(FeatureDocumentation.builder()
            .name("Include")
            .attribute("src", new GenericAttribute(String.class, true,
                "The path to another map configuration file."))
            .attribute("local", new GenericAttribute(Boolean.class, false,
                "If set to to true, it will search for the import in the same directory as the map.xml, otherwise it searches in the shared maps directory."))
            .build());

    InfoTable table = new InfoTable("Shared Includes", "Path", "Description");

    SAXBuilder sax = new SAXBuilder();

    try {
      File def = Atlas.get().getMapManager().getLibraries().get(0).getFile("defaults.xml");

      Document doc = sax.build(def);
      table.row("defaults.xml", doc.getContent().get(0).getValue());

      Atlas.get().getMapManager().getLibraries().forEach(l -> {
        try {
          searchShared(l.getFile("Shared"), table, sax, "Shared");
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
    }

    builder.table(table);

    return builder.build();
  }

  private static void searchShared(File toSearch, InfoTable toAdd, SAXBuilder builder,
      String prefix) throws Exception {
    for (File shared : toSearch.listFiles()) {
      if (shared.isDirectory()) {
        searchShared(shared, toAdd, builder, prefix + "/" + shared.getName());
      } else {
        if (shared.getName().contains(".xml")) {
          Document doc = builder.build(shared);
          toAdd.row(prefix + "/" + shared.getName(), doc.getContent().get(0).getValue());
        }
      }
    }
  }

  public static ModuleDocumentation conditionals() {
    return ModuleDocumentation.builder()
        .category(ModuleDocumentation.ModuleCategory.ADVANCED)
        .name("XML Conditionals")
        .tagName("conditional")
        .description(
            "Conditionals are elements which can go anywhere in the XML that will load the enclosed modules if the condition passes.")
        .feature(FeatureDocumentation.builder()
            .name("Conditional")
            .attribute("season", new Attribute() {
              @Override
              public String getName() {
                return "Season";
              }

              @Override
              public boolean isRequired() {
                return false;
              }

              @Override
              public String[] getDescription() {
                return new String[]{"The current season of the year."};
              }

              @Override
              public String[] getValues() {
                return new String[]{"winter", "spring", "summer", "fall"};
              }
            })
            .attribute("month",
                new EnumAttribute(Month.class, false, "The current month of the year."))
            .attribute("holiday", new Attribute() {
              @Override
              public String getName() {
                return "Holiday";
              }

              @Override
              public boolean isRequired() {
                return false;
              }

              @Override
              public String[] getDescription() {
                return new String[]{"The current holiday."};
              }

              @Override
              public String[] getValues() {
                return new String[]{"christmas", "independence-day", "april-fools", "new-years"};
              }
            })
            .table(new InfoTable("Seasons", "Season", "Dates")
                .row("winter", "December, January, February, March")
                .row("spring", "April, May")
                .row("summer", "June, July, August, September")
                .row("fall", "October, November"))
            .table(new InfoTable("Holidays", "Holiday", "Dates")
                .row("christmas", "December 15-26")
                .row("independence-day", "July 4")
                .row("april-fools", "April 1")
                .row("new-years", "Last Day of December - Jan 1"))
            .build())
        .build();
  }

  public static ModuleDocumentation moduleGroups() {
    return ModuleDocumentation.builder()
        .name("Module Groups")
        .tagName("module-group")
        .tagName("load-group")
        .category(ModuleDocumentation.ModuleCategory.ADVANCED)
        .description(
            "Module groups allow groups of modules to be referenced by an ID and then loaded later based on various circumstances. The group ident elements must be at the top level but the load elements can be anywhere. These can be combined with conditionals.")
        .description(
            "An example use-case of this would be having a lot of different groups in an include file and then loading them in the XML.")
        .feature(FeatureDocumentation.builder()
            .name("Group")
            .attribute("id", Attributes.id(true))
            .attribute("chance", new RangeAttribute(0, 1, false,
                "The chance of this group being loaded when the load tag is called."), 1)
            .build())
        .build();
  }
}
