package net.avicus.atlas.util.xml;

import java.util.Optional;

public class GenericXmlString extends ParsableXmlString {

  private final String name;

  public GenericXmlString(XmlElement element, String name, String value) {
    super(element, Optional.of(value));
    this.name = name;
  }

  @Override
  public String asRequiredString() {
    Optional<String> optional = asString();
    if (optional.isPresent()) {
      return optional.get();
    }
    throw new XmlException(getElement(), "Value missing for \"" + this.name + "\".");
  }
}
