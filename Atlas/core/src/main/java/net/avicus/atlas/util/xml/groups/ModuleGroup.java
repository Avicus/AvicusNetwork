package net.avicus.atlas.util.xml.groups;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import net.avicus.atlas.match.registry.RegisterableObject;
import org.jdom2.Element;

public class ModuleGroup implements RegisterableObject<ModuleGroup> {

  @Getter
  private final String id;
  @Getter
  private final Element loadTag;
  @Getter
  private final List<Element> elements;
  private final boolean shouldBeParent;

  public ModuleGroup(String id, Element loadTag, List<Element> elements, boolean shouldBeParent) {
    this.id = id;
    this.loadTag = loadTag;
    this.elements = elements;
    this.shouldBeParent = shouldBeParent;
  }

  public boolean shouldBeParent() {
    return shouldBeParent;
  }

  @Override
  public ModuleGroup getObject() {
    return this;
  }

  @Override
  protected ModuleGroup clone() {
    return new ModuleGroup(this.id, this.loadTag,
        this.getElements().stream().map(Element::clone).collect(Collectors.toList()),
        this.shouldBeParent);
  }
}
