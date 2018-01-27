package net.avicus.atlas.util.xml;

import java.util.Optional;

public class XmlText extends ParsableXmlString {

  public XmlText(XmlElement element) {
    super(element, getValue(element));
  }

  private static Optional<String> getValue(XmlElement element) {
    String value = element.getJdomElement().getTextTrim();
    if (value.length() == 0) {
      return Optional.empty();
    }
    return Optional.of(value);
  }

  @Override
  public String asRequiredString() {
    Optional<String> optional = asString();
    if (optional.isPresent()) {
      return optional.get();
    }
    throw new XmlException(this.getElement(), "Missing required text.");
  }
}
