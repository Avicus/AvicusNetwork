package net.avicus.atlas.util.xml;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import lombok.Getter;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.located.LocatedElement;

/**
 * This is a wrapper of jdom2 Element that utilizes Java 8 Optional.
 */
public class XmlElement {

  @Getter
  private final LocatedElement jdomElement;

  public XmlElement(Element jdomElement) {
    Preconditions
        .checkArgument(jdomElement instanceof LocatedElement, "Element must be a LocatedElement");
    this.jdomElement = (LocatedElement) jdomElement;
  }

  public static XmlElement fromJdom(Element element) {
    return new XmlElement(element);
  }

  public static List<XmlElement> fromJdom(List<Element> elements) {
    List<XmlElement> list = elements.stream()
        .map(XmlElement::fromJdom)
        .collect(Collectors.toList());
    return list;
  }

  @Nonnull
  public String getName() {
    return this.jdomElement.getName();
  }

  @Nonnull
  public XmlAttribute getAttribute(String name) {
    return new XmlAttribute(this, name);
  }

  public boolean hasAttribute(String name) {
    return this.jdomElement.getAttribute(name) != null;
  }

  public boolean hasText() {
    return this.getText().isValuePresent();
  }

  @Nonnull
  public List<XmlAttribute> getAttributes() {
    List<XmlAttribute> list = this.jdomElement.getAttributes()
        .stream()
        .map(attribute -> new XmlAttribute(this, attribute.getName()))
        .collect(Collectors.toList());
    return list;
  }

  @Nonnull
  public Optional<XmlElement> getParent() {
    return Optional.of(new XmlElement(this.jdomElement.getParentElement()));
  }

  @Nonnull
  public XmlText getText() {
    return new XmlText(this);
  }

  public void inheritAttributes(String parentName) {
    if (!getParent().isPresent()) {
      return;
    }

    Optional<XmlElement> parent = getParent();
    while (parent.isPresent() && parent.get().getName().equals(parentName)) {
      parent.get()
          .getJdomElement()
          .getAttributes()
          .stream()
          .filter(attribute -> !getAttribute(attribute.getName()).isValuePresent())
          .forEach(attribute -> {
            getJdomElement().setAttribute(attribute.getName(), attribute.getValue());
          });
      parent = parent.get().getParent();
    }
  }

  public void inheritAttributes(String parentName, List<String> ignored) {
    if (!getParent().isPresent()) {
      return;
    }

    Optional<XmlElement> parent = getParent();
    while (parent.isPresent() && parent.get().getName().equals(parentName)) {
      parent.get()
          .getJdomElement()
          .getAttributes()
          .stream().filter(a -> !ignored.contains(a.getName()))
          .filter(attribute -> !getAttribute(attribute.getName()).isValuePresent())
          .forEach(attribute -> {
            getJdomElement().setAttribute(attribute.getName(), attribute.getValue());
          });
      parent = parent.get().getParent();
    }
  }

  @Nonnull
  public List<XmlElement> getDescendants() {
    List<Element> elements = new ArrayList<>();
    for (Content content : this.jdomElement.getDescendants()) {
      if (content instanceof Element) {
        elements.add((Element) content);
      }
    }
    return fromJdom(elements);
  }

  @Nonnull
  public List<XmlElement> getDescendants(String name) {
    List<XmlElement> elements = getDescendants();
    Iterator<XmlElement> iterator = elements.iterator();
    while (iterator.hasNext()) {
      if (!iterator.next().getName().equals(name)) {
        iterator.remove();
      }
    }
    return elements;
  }

  @Nonnull
  public List<XmlElement> getChildren() {
    return fromJdom(this.jdomElement.getChildren());
  }

  @Nonnull
  public List<XmlElement> getChildren(String name) {
    return fromJdom(this.jdomElement.getChildren(name));
  }

  @Nonnull
  public Optional<XmlElement> getChild(String name) {
    if (hasChild(name)) {
      return Optional.of(fromJdom(this.jdomElement.getChild(name)));
    }
    return Optional.empty();
  }

  @Nonnull
  public XmlElement getRequiredChild(String name) {
    Optional<XmlElement> child = getChild(name);
    if (child.isPresent()) {
      return child.get();
    }
    throw new XmlException(this, "Missing required child \"" + name + "\".");
  }

  public boolean hasChild(String name) {
    return this.jdomElement.getChild(name) != null;
  }

  public int getLine() {
    return this.jdomElement.getLine();
  }

  public int getColumn() {
    return this.jdomElement.getColumn();
  }

  public int getSize() {
    return this.jdomElement.getContentSize();
  }

  @Override
  public String toString() {
    return this.jdomElement.toString();
  }
}
