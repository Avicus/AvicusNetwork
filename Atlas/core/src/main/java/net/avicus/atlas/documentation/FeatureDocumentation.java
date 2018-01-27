package net.avicus.atlas.documentation;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import net.avicus.atlas.documentation.attributes.Attribute;
import net.avicus.atlas.module.ModuleFactory;
import org.apache.commons.lang3.tuple.Pair;
import org.jdom2.Element;

@Getter
public class FeatureDocumentation {

  private final String name;
  private Function<Element, Boolean> tagFilter;
  private LinkedList<String> tagNames;
  private List<String> description;
  private Pair<Attribute, Object> text;
  private LinkedHashMap<String, Pair<Attribute, Object>> attributes;
  private SpecInformation specInformation;
  private LinkedList<InfoTable> tables;
  private LinkedList<Class<? extends ModuleFactory>> requirements;
  private LinkedList<FeatureDocumentation> subFeatures;

  @Builder
  public FeatureDocumentation(String name,
      Function<Element, Boolean> tagFilter,
      @Singular List<String> tagNames,
      @Singular List<String> descriptions,
      Pair<Attribute, Object> text,
      LinkedHashMap<String, Pair<Attribute, Object>> attributes,
      SpecInformation specInformation,
      @Singular List<InfoTable> tables,
      @Singular List<Class<? extends ModuleFactory>> requirements,
      @Singular List<FeatureDocumentation> subFeatures) {
    Preconditions.checkNotNull(name);
    this.name = name;
    this.tagFilter = tagFilter;
    if (tagNames != null) {
      this.tagNames = Lists.newLinkedList(tagNames);
    }
    this.description = descriptions;
    this.text = text;
    if (attributes != null) {
      this.attributes = Maps.newLinkedHashMap(attributes);
    }
    this.specInformation = specInformation;
    if (tables != null) {
      this.tables = Lists.newLinkedList(tables);
    }
    if (requirements != null) {
      this.requirements = Lists.newLinkedList(requirements);
    }
    if (subFeatures != null) {
      this.subFeatures = Lists.newLinkedList(subFeatures);
    }

    if (this.tagFilter == null && tagNames != null) {
      this.tagFilter = (e) -> tagNames.contains(e.getName());
    }

    if (this.specInformation != null) {
      this.specInformation.setName(this.name);
    }

    sort();
  }

  private void sort() {
    if (this.tagNames != null) {
      this.tagNames.sort(String::compareTo);
    }
    if (this.subFeatures != null) {
      this.subFeatures.sort(Comparator.comparing(FeatureDocumentation::getName));
    }
    if (this.requirements != null) {
      this.requirements.sort(Comparator.comparing(Class::getSimpleName));
    }
    if (this.tables != null) {
      this.tables.sort(Comparator.comparing(InfoTable::getTitle));
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FeatureDocumentation that = (FeatureDocumentation) o;
    return Objects.equal(getName(), that.getName());
  }

  public String getSafeName() {
    return this.name.toLowerCase()
        .replace(" ", "-")
        .replace(",", "")
        .replace("/", "-")
        .replace("&", "-")
        .replace(")", "")
        .replace("(", "");
  }

  public static class FeatureDocumentationBuilder {

    public FeatureDocumentationBuilder attributes(Map<String, Pair<Attribute, Object>> toAdd) {
      if (attributes == null) {
        attributes = Maps.newLinkedHashMap();
      }
      attributes.putAll(toAdd);

      return this;
    }

    public FeatureDocumentationBuilder attribute(String name, Attribute attribute) {
      if (attributes == null) {
        attributes = Maps.newLinkedHashMap();
      }
      attributes.put(name, Pair.of(attribute, null));
      return this;
    }

    public FeatureDocumentationBuilder attribute(String name, Attribute attribute, Object def) {
      if (attributes == null) {
        attributes = Maps.newLinkedHashMap();
      }
      attributes.put(name, Pair.of(attribute, def));
      return this;
    }

    public FeatureDocumentationBuilder text(Attribute id, Object def) {
      text = Pair.of(id, def);
      return this;
    }

    public FeatureDocumentationBuilder text(Attribute id) {
      text = Pair.of(id, null);
      return this;
    }
  }
}
