package net.avicus.atlas.util.xml;

import com.google.common.base.Splitter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import net.avicus.atlas.util.color.TeamColor;
import net.avicus.compendium.inventory.MultiMaterialMatcher;
import net.avicus.compendium.inventory.SingleMaterialMatcher;
import net.avicus.compendium.number.NumberAction;
import net.avicus.compendium.number.NumberComparator;
import net.avicus.magma.util.Version;
import org.apache.commons.lang.math.IntRange;
import org.apache.commons.lang.math.Range;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.util.Vector;
import org.joda.time.Duration;
import org.joda.time.Seconds;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/**
 * Represents a value that may be present or not, and may be
 * parsed into objects.
 */
public abstract class ParsableXmlString {

  private static final PeriodFormatter periodFormatter = new PeriodFormatterBuilder()
      .appendDays().appendSuffix("d")
      .appendHours().appendSuffix("h")
      .appendMinutes().appendSuffix("m")
      .appendSecondsWithOptionalMillis().appendSuffix("s")
      .appendSeconds()
      .toFormatter();
  @Getter
  private final XmlElement element;
  private final Optional<String> value;

  public ParsableXmlString(XmlElement element, Optional<String> value) {
    this.element = element;
    this.value = value;
  }

  public Optional<String> asString() {
    return this.value;
  }

  public abstract String asRequiredString();

  public boolean isValuePresent() {
    return asString().isPresent();
  }

  public Optional<Boolean> asBoolean() {
    if (isValuePresent()) {
      return Optional.of(asRequiredBoolean());
    }
    return Optional.empty();
  }

  public boolean asRequiredBoolean() {
    String text = asRequiredString();

    switch (text) {
      case "true":
      case "yes":
      case "allow":
        return true;
      case "false":
      case "no":
      case "deny":
        return false;
      default:
        throw new XmlException(this, "Invalid boolean.");
    }
  }

  public Optional<Number> asNumber() {
    if (isValuePresent()) {
      return Optional.of(asRequiredNumber());
    }
    return Optional.empty();
  }

  public Number asRequiredNumber() {
    try {
      return NumberFormat.getInstance().parse(asRequiredString());
    } catch (ParseException e) {
      throw new XmlException(this, e);
    }
  }

  public Optional<Integer> asInteger() {
    if (isValuePresent()) {
      return Optional.of(asRequiredInteger());
    }
    return Optional.empty();
  }

  public int asRequiredInteger() {
    try {
      String text = asRequiredString();
      if (text.equals("oo")) {
        return Integer.MAX_VALUE;
      } else if (text.equals("-oo")) {
        return Integer.MIN_VALUE;
      }
      return Integer.parseInt(text);
    } catch (Exception e) {
      throw new XmlException(this, e);
    }
  }

  public Optional<Double> asDouble() {
    if (isValuePresent()) {
      return Optional.of(asRequiredDouble());
    }
    return Optional.empty();
  }

  public double asRequiredDouble() {
    try {
      String text = asRequiredString();
      if (text.equals("oo")) {
        return Double.MAX_VALUE;
      } else if (text.equals("-oo")) {
        return Double.MIN_VALUE;
      }
      return Double.parseDouble(text);
    } catch (Exception e) {
      throw new XmlException(this, e);
    }
  }

  public Optional<Float> asFloat() {
    if (isValuePresent()) {
      return Optional.of(asRequiredFlot());
    }
    return Optional.empty();
  }

  public Float asRequiredFlot() {
    try {
      String text = asRequiredString();
      if (text.equals("oo")) {
        return Float.MAX_VALUE;
      } else if (text.equals("-oo")) {
        return Float.MIN_VALUE;
      }
      return Float.parseFloat(text);
    } catch (Exception e) {
      throw new XmlException(this, e);
    }
  }

  public <E extends Enum<E>> Optional<E> asEnum(Class<E> clazz, boolean normalEnum) {
    if (isValuePresent()) {
      return Optional.of(asRequiredEnum(clazz, normalEnum));
    }
    return Optional.empty();
  }

  public <E extends Enum<E>> E asRequiredEnum(Class<E> clazz, boolean normalEnum) {
    try {
      String text = asRequiredString();
      if (normalEnum) {
        text = text.toUpperCase().replace(" ", "_").replace("-", "_");
      }
      return Enum.valueOf(clazz, text);
    } catch (Exception e) {
      throw new XmlException(this, e);
    }
  }

  public Optional<List<GenericXmlString>> asList(String separator, boolean removeSpaces) {
    if (isValuePresent()) {
      return Optional.of(asRequiredList(separator, removeSpaces));
    }
    return Optional.empty();
  }

  public List<GenericXmlString> asRequiredList(String separator, boolean removeSpaces) {
    String complete = removeSpaces ? asRequiredString().replace(" ", "") : asRequiredString();

    List<String> list = Splitter.on(separator).splitToList(complete);
    List<GenericXmlString> values = list.stream()
        .map(text -> new GenericXmlString(this.element, asRequiredString(), text))
        .collect(Collectors.toList());
    return values;
  }

  public Optional<Color> asColor() {
    if (isValuePresent()) {
      return Optional.of(asRequiredColor());
    }
    return Optional.empty();
  }

  public Color asRequiredColor() {
    String text = asRequiredString();

    // hex
    if (text.startsWith("#")) {
      String hex = text.substring(1);
      try {
        int red = Integer.valueOf(hex.substring(0, 2), 16);
        int green = Integer.valueOf(hex.substring(2, 4), 16);
        int blue = Integer.valueOf(hex.substring(4, 6), 16);
        return Color.fromRGB(red, green, blue);
      } catch (Exception e) {
        throw new XmlException(this, "Invalid hex code.");
      }
    }
    // red, blue, etc...
    else {
      TeamColor color = asRequiredEnum(TeamColor.class, true);
      return color.getDyeColor().getColor();
    }
  }

  public Optional<Duration> asDuration() {
    if (isValuePresent()) {
      return Optional.of(asRequiredDuration());
    }
    return Optional.empty();
  }

  public Duration asRequiredDuration() {
    String text = asRequiredString().toLowerCase().replace(" ", "");

    if (text.equals("oo")) {
      return Seconds.MAX_VALUE.toStandardDuration();
    } else if (text.equals("-oo")) {
      return Seconds.MIN_VALUE.toStandardDuration();
    }

    try {
      return periodFormatter.parsePeriod(text).toStandardDuration();
    } catch (Exception e) {
      throw new XmlException(this, "Invalid duration format.");
    }
  }

  public Optional<Vector> asVector() {
    if (isValuePresent()) {
      return Optional.of(asRequiredVector());
    }
    return Optional.empty();
  }

  public Vector asRequiredVector() {
    List<GenericXmlString> values = asRequiredList(",", true);
    if (values.size() != 3) {
      throw new XmlException(this,
          "Vectors must contain three and only three elements: x, y and z.");
    }

    return new Vector(values.get(0).asRequiredDouble(), values.get(1).asRequiredDouble(),
        values.get(2).asRequiredDouble());
  }

  public Optional<NumberAction> asNumberAction() {
    if (isValuePresent()) {
      return Optional.of(asRequiredNumberAction());
    }
    return Optional.empty();
  }

  public NumberAction asRequiredNumberAction() {
    String text = asRequiredString().toLowerCase().replace(" ", "");

    switch (text) {
      case "none":
        return NumberAction.NONE;
      case "set":
        return NumberAction.SET;
      case "add":
        return NumberAction.ADD;
      case "subtract":
        return NumberAction.SUBTRACT;
      case "multiply":
        return NumberAction.MULTIPLY;
      case "divide":
        return NumberAction.DIVIDE;
      case "power":
        return NumberAction.POWER;
    }

    throw new XmlException(this, "Invalid number action.");
  }

  public Optional<NumberComparator> asComparator() {
    if (isValuePresent()) {
      return Optional.of(asRequiredComparator());
    }
    return Optional.empty();
  }

  public NumberComparator asRequiredComparator() {
    String text = asRequiredString().toLowerCase().replace(" ", "_").replace("-", "_");

    if (text.equals("equals")) {
      return NumberComparator.EQUALS;
    } else if (text.equals("less_than") || text.equals("less")) {
      return NumberComparator.LESS_THAN;
    } else if (text.equals("less_than_equal")) {
      return NumberComparator.LESS_THAN_EQUAL;
    } else if (text.equals("greater_than") || text.equals("greater")) {
      return NumberComparator.GREATER_THAN;
    } else if (text.equals("greater_than_equal")) {
      return NumberComparator.GREATER_THAN_EQUAL;
    } else if (text.startsWith("mod")) {
      String mod = text.replace("_", "").replace("mod", "");
      int modValue = Integer.parseInt(mod);
      return NumberComparator.MODULO(modValue);
    }

    throw new XmlException(this, "Unknown number comparator.");
  }

  public Optional<Range> asRange() {
    if (isValuePresent()) {
      return Optional.of(asRequiredRange());
    }
    return Optional.empty();
  }

  public Range asRequiredRange() {
    List<String> list = Splitter.on("-").splitToList(asRequiredString());
    if (list.isEmpty() || list.size() > 2) {
      throw new XmlException(this, "Ranges must contain two numbers.");
    }

    return new IntRange(Integer.parseInt(list.get(0)), Integer.parseInt(list.get(1)));
  }

  public Optional<Version> asVersion() {
    if (isValuePresent()) {
      return Optional.of(asRequiredVersion());
    }
    return Optional.empty();
  }

  public Version asRequiredVersion() {
    List<GenericXmlString> list = asRequiredList(".", true);
    if (list.size() != 3) {
      throw new XmlException(this, "Version must be in semantic format.");
    }
    return new Version(list.get(0).asRequiredInteger(), list.get(1).asRequiredInteger(),
        list.get(2).asRequiredInteger());
  }

  public Optional<SingleMaterialMatcher> asMaterialMatcher() {
    if (isValuePresent()) {
      return Optional.of(asRequiredMaterialMatcher());
    }
    return Optional.empty();
  }

  public SingleMaterialMatcher asRequiredMaterialMatcher() {
    List<GenericXmlString> list = asRequiredList(":", false);

    if (list.size() > 2) {
      throw new XmlException(this, "Invalid material matcher.");
    }

    Material material = list.get(0).asRequiredEnum(Material.class, true);
    Optional<Byte> data = Optional.empty();
    if (list.size() == 2) {
      data = Optional.of(list.get(1).asRequiredNumber().byteValue());
    }

    return new SingleMaterialMatcher(material, data);
  }

  public Optional<MultiMaterialMatcher> asMultiMaterialMatcher() {
    if (isValuePresent()) {
      return Optional.of(asRequiredMultiMaterialMatcher());
    }
    return Optional.empty();
  }

  public MultiMaterialMatcher asRequiredMultiMaterialMatcher() {
    List<GenericXmlString> list = asRequiredList(";", false);
    List<SingleMaterialMatcher> matchers = list.stream()
        .map(GenericXmlString::asRequiredMaterialMatcher)
        .collect(Collectors.toList());

    return new MultiMaterialMatcher(matchers);
  }

  public Optional<URL> asURL() {
    if (isValuePresent()) {
      return Optional.of(asRequiredURL());
    }
    return Optional.empty();
  }

  public URL asRequiredURL() {
    try {
      URL url = new URL(asRequiredString());
      // more validation
      url.toURI();
      return url;
    } catch (MalformedURLException e) {
      throw new XmlException(this.element, "Invalid URL.");
    } catch (URISyntaxException e) {
      throw new XmlException(this.element, "Invalid URI.");
    }
  }
}
