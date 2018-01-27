package net.avicus.atlas.util.xml;

import java.util.Optional;
import javax.annotation.Nullable;
import org.jdom2.Attribute;

public class XmlAttribute extends ParsableXmlString {

  private final String name;

  public XmlAttribute(XmlElement element, String name) {
    super(element, getValue(element, name));
    this.name = name;
  }

  private static Optional<String> getValue(XmlElement element, String name) {
    Attribute jdom = element.getJdomElement().getAttribute(name);
    return jdom == null ? Optional.empty() : Optional.of(jdom.getValue());
  }

  @Nullable
  public Attribute getJdomAttribute() {
    return getElement().getJdomElement().getAttribute(this.name);
  }

  public String getName() {
    return this.name;
  }

  @Override
  public String asRequiredString() {
    Optional<String> optional = asString();
    if (optional.isPresent()) {
      return optional.get();
    }
    throw new XmlException(this.getElement(), "Missing required attribute \"" + this.name + "\"");
  }
}
