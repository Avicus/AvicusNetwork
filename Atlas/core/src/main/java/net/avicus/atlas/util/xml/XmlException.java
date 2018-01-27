package net.avicus.atlas.util.xml;

import javax.annotation.Nonnull;
import org.jdom2.located.LocatedElement;

public class XmlException extends RuntimeException {

  public XmlException(@Nonnull XmlElement element, @Nonnull String message) {
    super(generateMessage(element, message));
  }

  public XmlException(@Nonnull ParsableXmlString element, @Nonnull String message) {
    super(generateMessage(element, message));
  }

  public XmlException(@Nonnull XmlElement element, @Nonnull Throwable cause) {
    this(element, cause.getMessage());
  }

  public XmlException(@Nonnull ParsableXmlString string, @Nonnull Throwable cause) {
    this(string, cause.getMessage());
  }

  public XmlException(@Nonnull LocatedElement element, @Nonnull String message) {
    super(generateMessage(element, message));
  }

  public XmlException(@Nonnull LocatedElement element, @Nonnull Throwable cause) {
    this(element, cause.getMessage());
  }

  private static String generateMessage(@Nonnull ParsableXmlString string, String message) {
    XmlElement element = string.getElement();

    StringBuilder builder = new StringBuilder();
    builder.append("Unable to parse <");
    builder.append(element.getName());
    builder.append("> (line #");
    builder.append(element.getLine());
    builder.append("): ");
    builder.append(message);
    builder.append(" (for ");
    builder.append(string.getClass().getSimpleName());
    if (string.isValuePresent()) {
      builder.append(" value \"");
      builder.append(string.asRequiredString());
      builder.append("\"");
    }
    builder.append(")");
    return builder.toString();
  }

  private static String generateMessage(@Nonnull XmlElement element, String message) {
    String builder = "Unable to parse <" +
        element.getName() +
        "/> (line #" +
        element.getLine() +
        "): " +
        message;
    return builder;
  }

  private static String generateMessage(@Nonnull LocatedElement element, String message) {
    String builder = "Unable to parse <" +
        element.getName() +
        "/> (line #" +
        element.getLine() +
        "): " +
        message;
    return builder;
  }
}
