package net.avicus.atlas.util.xml.conditionals;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.atlas.util.xml.XmlException;
import net.avicus.atlas.util.xml.conditionals.type.ConfigValueConditional;
import net.avicus.atlas.util.xml.conditionals.type.DateConditional;
import org.jdom2.Attribute;
import org.jdom2.Element;

public class ConditionalsFactory {

  public static Optional<ConditionalContext> parseContext(Element element) throws XmlException {
    Optional<Conditional> conditional = Optional.empty();
    List<Element> elseElements = new ArrayList<>();
    for (Element child : element.getChildren()) {
      if (child.getName().equals("if")) {
        conditional = Optional.of(parseConditional(child));
      } else if (child.getName().equals("unless")) {
        conditional = Optional.of(parseConditional(child).inverse());
      } else if (child.getName().equals("else")) {
        elseElements.addAll(child.getChildren());
      }
    }

    if (!conditional.isPresent()) {
      throw new XmlException(new XmlElement(element),
          "Conditionals must have one if or unless statement");
    }

    return Optional.of(new ConditionalContext(conditional.get(), elseElements));
  }

  public static Conditional parseConditional(Element element) throws XmlException {
    List<Attribute> attributes = element.getAttributes();

    if (attributes.size() != 1) {
      throw new XmlException(new XmlElement(element),
          "Conditionals must have 1 and only 1 variable.");
    }

    Attribute variable = attributes.get(0);

    switch (variable.getName()) {
      case "season":
      case "month":
      case "holiday":
        return new DateConditional(variable.getName(), variable.getValue(), element.getChildren());

      default:
        return new ConfigValueConditional(variable.getName(), variable.getValue(),
            element.getChildren());
    }

  }
}
