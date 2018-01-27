package net.avicus.atlas.documentation;

import com.google.common.collect.Lists;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import net.avicus.atlas.module.ModuleFactory;
import org.jdom2.Element;

@Getter
public class ModuleDocumentation {

  private ModuleCategory category;
  private String name;
  private LinkedList<String> tagNames;
  private Function<Element, Boolean> tagFilter;
  private LinkedList<String> description;
  private LinkedList<FeatureDocumentation> features;
  private LinkedList<Class<? extends ModuleFactory>> requirements;
  private LinkedList<InfoTable> tables;
  private SpecInformation specInformation;

  @Builder
  public ModuleDocumentation(ModuleCategory category,
      String name,
      @Singular List<String> tagNames,
      Function<Element, Boolean> tagFilter,
      LinkedList<String> descriptions,
      @Singular List<FeatureDocumentation> features,
      @Singular List<Class<? extends ModuleFactory>> requirements,
      @Singular List<InfoTable> tables,
      SpecInformation specInformation) {
    this.category = category;
    this.name = name;
    if (tagNames != null) {
      this.tagNames = Lists.newLinkedList(tagNames);
    }
    this.tagFilter = tagFilter;
    this.description = descriptions;
    if (features != null) {
      this.features = Lists.newLinkedList(features);
    }
    if (requirements != null) {
      this.requirements = Lists.newLinkedList(requirements);
    }
    if (tables != null) {
      this.tables = Lists.newLinkedList(tables);
    }
    this.specInformation = specInformation;

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
    if (this.features != null) {
      this.features.sort(Comparator.comparing(FeatureDocumentation::getName));
    }
    if (this.requirements != null) {
      this.requirements.sort(Comparator.comparing(Class::getSimpleName));
    }
    if (this.tables != null) {
      this.tables.sort(Comparator.comparing(InfoTable::getTitle));
    }
  }

  public String getLink() {
    return "/modules/" + getSafeName();
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

  public List<SpecInformation> getSpecInfo() {
    List<SpecInformation> info = Lists.newArrayList();

    if (this.getSpecInformation() != null) {
      info.add(this.getSpecInformation());
    }

    this.getFeatures().forEach(f -> info.addAll(getSpecInfo(f)));

    return info;
  }

  private List<SpecInformation> getSpecInfo(FeatureDocumentation documentation) {
    List<SpecInformation> info = Lists.newArrayList();

    if (documentation.getSpecInformation() != null) {
      info.add(documentation.getSpecInformation());
    }

    if (documentation.getSubFeatures() != null) {
      documentation.getSubFeatures().forEach(f -> info.addAll(getSpecInfo(f)));
    }

    return info;
  }

  public enum ModuleCategory {
    CORE("Core Modules"),
    COMPONENTS("General Game Components"),
    MISC("Miscellaneous Modules & Features"),
    OBJECTIVES("Objectives"),
    SPECIAL("Special Game Types"),
    ARCADE("Arcade Games"),
    ADVANCED("Advanced Modules");

    @Getter
    private final String human;

    ModuleCategory(String human) {
      this.human = human;
    }
  }

  public static class ModuleDocumentationBuilder {

    public ModuleDocumentationBuilder description(String s) {
      if (this.descriptions == null) {
        this.descriptions = Lists.newLinkedList();
      }

      this.descriptions.add(s);
      return this;
    }
  }
}
