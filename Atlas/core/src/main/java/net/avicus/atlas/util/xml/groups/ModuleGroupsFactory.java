package net.avicus.atlas.util.xml.groups;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.util.xml.XmlException;
import org.apache.commons.lang3.tuple.Pair;
import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.jdom2.Text;
import org.jdom2.filter.ElementFilter;
import org.jdom2.located.LocatedElement;

public class ModuleGroupsFactory {

  public static List<ModuleGroup> loadGroups(Element element) throws XmlException {
    List<ModuleGroup> groups = Lists.newArrayList();

    element.getChildren("module-group").forEach(group -> {
      Attribute idAttr = group.getAttribute("id");
      if (idAttr == null) {
        throw new XmlException((LocatedElement) group, "Missing required attribute \"id\"");
      }
      String id = idAttr.getValue();
      Attribute parentAttr = group.getAttribute("parent");
      boolean parent = false;
      if (parentAttr != null) {
        parent = Boolean.valueOf(parentAttr.getValue());
      }
      groups.add(new ModuleGroup(id, group, group.getChildren(), parent));
    });

    return groups;
  }

  public static List<ModuleGroup> overrideAndReturn(List<ModuleGroup> allGroups) {
    HashMap<String, List<ModuleGroup>> byId = Maps.newHashMap();

    List<ModuleGroup> result = Lists.newArrayList();

    allGroups = Ordering.from((Comparator<ModuleGroup>) (f1, f2) -> Boolean
        .compare(f1.shouldBeParent(), f2.shouldBeParent())).sortedCopy(allGroups);

    allGroups.forEach(moduleGroup -> {
      byId.putIfAbsent(moduleGroup.getId(), Lists.newArrayList());
      byId.get(moduleGroup.getId()).add(moduleGroup);
    });

    byId.forEach((id, groups) -> {
      ModuleGroup parent = groups.remove(0);
      groups.forEach(group -> {
        List<Element> clones = group.getElements().stream().map(Element::clone)
            .collect(Collectors.toList());
        parent.getElements().addAll(clones);
      });

      result.add(parent);
    });

    return result;
  }

  public static List<Pair<Element, ModuleGroup>> allThatShouldLoad(Match match, Element element) {
    List<Pair<Element, ModuleGroup>> result = Lists.newArrayList();
    final ElementFilter filter = new ElementFilter("load-group");
    Iterator<Element> groups = element.getDescendants(filter).iterator();
    Random random = new Random();
    while (groups.hasNext()) {
      Element e = groups.next();
      Attribute idAttr = e.getAttribute("id");
      if (idAttr == null) {
        throw new XmlException((LocatedElement) e, "Missing required attribute \"id\"");
      }
      String id = idAttr.getValue();

      Optional<ModuleGroup> referenced = match.getRegistry().get(ModuleGroup.class, id, true);
      if (!referenced.isPresent()) {
        continue; // next
      }

      ModuleGroup group = referenced.get().clone();

      boolean add;

      if (e.getAttribute("chance") != null) {
        try {
          double chance = e.getAttribute("chance").getDoubleValue();

          add = random.nextDouble() <= chance;
        } catch (DataConversionException ex) {
          throw new XmlException((LocatedElement) e, ex);
        }
      } else {
        add = true;
      }

      HashMap<String, String> replacements = Maps.newHashMap();
      e.getAttributes().forEach(a -> {
        if (!(a.getName().equals("id") || a.getName().equals("chance"))) {
          replacements.put(a.getName(), a.getValue());
        }
      });

      if (!replacements.isEmpty()) {
        group.getElements().forEach(el -> replaceVariables(replacements, el));
      }

      if (add) {
        result.add(Pair.of(e, group));
      }
    }

    return result;
  }

  private static String replaceVariables(HashMap<String, String> toReplace, String format) {
    String res = format;
    for (Map.Entry<String, String> entry : toReplace.entrySet()) {
      if (res.contains("$" + entry.getKey())) {
        res = res.replaceAll("\\$" + entry.getKey(), entry.getValue());
      }
    }
    return res;
  }

  private static void replaceVariables(HashMap<String, String> toReplace, Content content) {
    if (content.getCType() == Content.CType.Element) {
      Element element = (Element) content;
      element.getAttributes()
          .forEach(at -> at.setValue(replaceVariables(toReplace, at.getValue())));
      element.getContent().forEach(c -> replaceVariables(toReplace, c));
    } else if (content.getCType() == Content.CType.Text) {
      Text text = (Text) content;
      text.setText(replaceVariables(toReplace, text.getText()));
    }
  }
}
