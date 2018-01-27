package net.avicus.atlas.documentation.attributes;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.WordUtils;

public class Attributes {

  public static Attribute id(boolean required) {
    return new Attribute() {
      @Override
      public String getName() {
        return "Feature ID";
      }

      @Override
      public boolean isRequired() {
        return required;
      }

      @Override
      public String[] getDescription() {
        return new String[]{
            "The globally unique ID of this feature.",
            "This can be used to reference this feature from other parts of the XML."
        };
      }
    };
  }

  public static Attribute check(boolean required, String when) {
    return new Attribute() {
      @Override
      public String getName() {
        return "Check";
      }

      @Override
      public String[] getDescription() {
        return new String[]{
            "Check that should be ran " + when + ".",
            "This can either be in ID form, or as a nested XML tag in the syntax of a check."
        };
      }

      @Override
      public boolean isRequired() {
        return required;
      }
    };
  }

  public static Attribute region(boolean required, String... description) {
    return new Attribute() {
      @Override
      public String getName() {
        return "Region";
      }

      @Override
      public String[] getDescription() {
        List<String> desc = new ArrayList<>();
        desc.addAll(Arrays.asList(description));
        desc.add(
            "This can either be in ID form, or as a nested XML tag in the syntax of a region.");
        return desc.toArray(new String[]{});
      }

      @Override
      public boolean isRequired() {
        return required;
      }
    };
  }

  public static Attribute color(boolean required, String... description) {
    return new Attribute() {
      @Override
      public String getName() {
        return "Color";
      }

      @Override
      public String[] getDescription() {
        List<String> desc = new ArrayList<>();
        desc.addAll(Arrays.asList(description));
        desc.add("Colors can be defined in 2 different ways.");
        desc.add(
            "If the color starts with a '#', the hex of the color will be parsed (use this for exact colors)");
        desc.add("If not, a generic color (red, green, blue, etc) should be used.");
        return desc.toArray(new String[]{});
      }

      @Override
      public boolean isRequired() {
        return required;
      }
    };
  }

  public static Attribute materialMatcher(boolean required, boolean multi, String... description) {
    return new Attribute() {
      @Override
      public String getName() {
        return (multi ? "Multi" : "Singular") + " Material Matcher";
      }

      @Override
      public String[] getDescription() {
        List<String> res = Lists.newArrayList();
        res.addAll(Arrays.asList(description));
        res.add(
            "A material is defined in the syntax of matname:damage when matname is the name of the material and damage is the damage value of the material.");
        res.add("A damage value is not required.");
        if (multi) {
          res.add(
              "Multiple materials can be defined by adding a ';' between each individual declaration.");
        }
        return res.toArray(new String[]{});
      }

      @Override
      public boolean isRequired() {
        return required;
      }
    };
  }

  public static Attribute loadout(boolean required, String type, String... description) {
    return new Attribute() {
      @Override
      public String getName() {
        return WordUtils.capitalize(type) + " Loadout Tag";
      }

      @Override
      public String[] getDescription() {
        List<String> res = Lists.newArrayList();
        res.addAll(Arrays.asList(description));
        res.add("This should be written like " + type + " would be configured in a loadout.");
        return res.toArray(new String[]{});
      }

      @Override
      public boolean isRequired() {
        return required;
      }
    };
  }

  public static Attribute idOf(boolean required, String who, String... desc) {
    return new Attribute() {
      @Override
      public String getName() {
        return WordUtils.capitalize(who) + " ID";
      }

      @Override
      public String[] getDescription() {
        List<String> de = Lists.newArrayList();
        de.addAll(Arrays.asList(desc));
        de.add("The id of the " + who + " for reference.");

        return de.toArray(new String[]{});
      }

      @Override
      public boolean isRequired() {
        return required;
      }
    };
  }

  public static Attribute comparator(boolean required, String a, String b) {
    return new Attribute() {
      @Override
      public String getName() {
        return "Number Comparator";
      }

      @Override
      public String[] getDescription() {
        return new String[]{
            "The comparator which is used to compare " + a + " and " + b + "."
        };
      }

      @Override
      public String[] getValues() {
        return new String[]{
            "equals - Check if numbers are exact match.",
            "less than - Check if " + a + " is less than " + b + ".",
            "less than equal - Check if " + a + " is less than, or equal, to " + b + ".",
            "greater than - Check if " + a + " is greater than " + b + ".",
            "greater than equal - Check if " + a + " is greater than, or equal, to " + b + "."
        };
      }

      @Override
      public boolean isRequired() {
        return required;
      }
    };
  }

  public static Attribute action(boolean required, String applied) {
    return new Attribute() {
      @Override
      public String getName() {
        return "Number Action";
      }

      @Override
      public String[] getDescription() {
        return new String[]{
            "The action which is applied to " + applied + "."
        };
      }

      @Override
      public String[] getValues() {
        return new String[]{
            "none - Perform no action and keep " + applied + " the same.",
            "set - Set " + applied + " to the supplied number.",
            "add - Add the supplied number to " + applied + ".",
            "subtract - Subtract the supplied number from " + applied + ".",
            "multiply - Multiply the supplied number with " + applied + ".",
            "divide - Divide " + applied + " by the supplied number.",
            "power - Raise " + applied + " by the supplied number."
        };
      }

      @Override
      public boolean isRequired() {
        return required;
      }
    };
  }

  public static Attribute duration(boolean required, boolean giveFormat, String desc) {
    return new Attribute() {
      @Override
      public String getName() {
        return "Duration";
      }

      @Override
      public String[] getDescription() {
        List<String> res = Lists.newArrayList();
        res.add(desc);
        res.add(
            "This attribute denotes a period of time and can either be in seconds or in a period format.");
        if (giveFormat) {
          res.addAll(Arrays.asList("Formats are as follows:",
              "    d - days",
              "    h - hours",
              "    m - minutes",
              "    s - seconds",
              "These can be combined to create any period. No spaces are allowed."));
        }
        return res.toArray(new String[]{});
      }

      @Override
      public boolean isRequired() {
        return required;
      }
    };
  }

  public static Attribute javaDoc(boolean required, Class clazz, String... desc) {
    return new Attribute() {
      @Override
      public String getName() {
        return clazz.getSimpleName();
      }

      @Override
      public String[] getDescription() {
        return desc;
      }

      @Override
      public String getLink() {
        return "https://hub.spigotmc.org/javadocs/spigot/" + clazz.getPackage().getName()
            .replace(".", "/") + "/" + clazz.getSimpleName() + ".html";
      }

      @Override
      public boolean isRequired() {
        return required;
      }
    };
  }

  public static Attribute vector(boolean required, String... desc) {
    return new Attribute() {
      @Override
      public String getName() {
        return "X,Y,Z Vector";
      }

      @Override
      public String[] getDescription() {
        return desc;
      }

      @Override
      public boolean isRequired() {
        return required;
      }
    };
  }
}
