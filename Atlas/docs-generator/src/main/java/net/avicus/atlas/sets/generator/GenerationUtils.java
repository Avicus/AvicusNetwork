package net.avicus.atlas.sets.generator;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hp.gagawa.java.Node;
import com.hp.gagawa.java.elements.Br;
import com.hp.gagawa.java.elements.H1;
import com.hp.gagawa.java.elements.H3;
import com.hp.gagawa.java.elements.Li;
import com.hp.gagawa.java.elements.Small;
import com.hp.gagawa.java.elements.Span;
import com.hp.gagawa.java.elements.Strong;
import com.hp.gagawa.java.elements.Text;
import com.hp.gagawa.java.elements.Ul;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.SpecificationVersionHistory;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.ModuleDocumentation;
import net.avicus.atlas.documentation.SpecInformation;
import net.avicus.magma.util.Version;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jdom2.Comment;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.yaml.snakeyaml.Yaml;

public class GenerationUtils {

  public static void generateConfig(File where) throws IOException {
    HashMap<String, Object> data = Maps.newHashMap();

    data.put("spec", SpecificationVersionHistory.CURRENT.toString());

    HashMap<ModuleDocumentation.ModuleCategory, List<ModuleDocumentation>> byCat = Maps
        .newHashMap();
    Atlas.get().getMatchFactory().getDocumentation().forEach(d -> {
      if (d.getCategory() == null) {
        Bukkit.getConsoleSender()
            .sendMessage(ChatColor.RED + d.getName() + " does not have a module category!");
        return;
      }

      byCat.putIfAbsent(d.getCategory(), Lists.newArrayList());
      byCat.get(d.getCategory()).add(d);
    });
    List<Object> content = Lists.newArrayList();
    byCat.entrySet().stream()
        .sorted(
            new Comparator<Map.Entry<ModuleDocumentation.ModuleCategory, List<ModuleDocumentation>>>() {
              @Override
              public int compare(
                  Map.Entry<ModuleDocumentation.ModuleCategory, List<ModuleDocumentation>> o1,
                  Map.Entry<ModuleDocumentation.ModuleCategory, List<ModuleDocumentation>> o2) {
                return o1.getKey().ordinal() - o2.getKey().ordinal();
              }
            })
        .forEach((c) -> {
          content.add(c.getKey().getHuman());
          c.getValue().stream().sorted(new Comparator<ModuleDocumentation>() {
            @Override
            public int compare(ModuleDocumentation o1, ModuleDocumentation o2) {
              return o1.getName().compareTo(o2.getName());
            }
          }).forEach((d) -> {
            HashMap<String, String> module = Maps.newHashMap();
            module.put("name", d.getName());
            module.put("path", d.getLink());
            content.add(module);
          });
        });
    data.put("content", content);
    Yaml yaml = new Yaml();
    FileWriter out = new FileWriter(where);

    yaml.dump(data, out);
  }

  public static void writeFile(File modules, ModuleDocumentation documentation,
      CommandSender sender, HashMap<FeatureDocumentation, Element> ex) throws IOException {
    File html = new File(modules, documentation.getSafeName() + ".html");
    sender.sendMessage(ChatColor.GREEN + "Writing html file at " + html.getPath());
    html.createNewFile();

    FileWriter writer = new FileWriter(html);
    writer.append("---\n" +
        "layout: module\n" +
        "title: " + documentation.getName() + "\n" +
        "permalink: " + documentation.getLink() + "\n" +
        "---");
    writer.append("\n\n");
    writer.append(HTMLUtils.generateModule(documentation, ex).write());
    writer.close();
    sender.sendMessage(ChatColor.GREEN + "Wrote html file at " + html.getPath());
  }

  public static void populateExamples(File examples, List<ModuleDocumentation> documentations)
      throws IOException {
    examples.createNewFile();
    Element root = new Element("examples");

    documentations.forEach(d -> {
      Element documentation = new Element(d.getSafeName());
      d.getFeatures().forEach(f -> {
        GenerationUtils.createExamplePaths(f, documentation);
      });
      root.addContent(documentation);
    });

    Document document = new Document(root);
    XMLOutputter xmlOutput = new XMLOutputter();

    // display nice nice
    xmlOutput.setFormat(Format.getPrettyFormat());
    xmlOutput.output(document, new FileWriter(examples));
  }

  public static void writeHistory(File root, List<SpecInformation> info) throws IOException {
    File histFile = new File(root, "spec-history.html");
    histFile.createNewFile();

    FileWriter writer = new FileWriter(histFile);
    writer.append("---\n" +
        "layout: module\n" +
        "title: Version History" + "\n" +
        "permalink: " + "/spec-history" + "\n" +
        "---");
    writer.append("\n\n");

    HashMap<Version, List<String>> added = Maps.newHashMap();
    HashMap<Version, List<String>> removed = Maps.newHashMap();
    HashMap<Version, List<String>> deprecated = Maps.newHashMap();
    HashMap<Version, List<String>> changes = Maps.newHashMap();
    HashMap<Version, List<String>> breaking = Maps.newHashMap();

    info.forEach(i -> {
      if (i.getAdded() != null) {
        added.putIfAbsent(i.getAdded(), Lists.newArrayList());
        added.get(i.getAdded()).add(i.getName());
      }
      if (i.getDeprecated() != null) {
        deprecated.putIfAbsent(i.getDeprecated(), Lists.newArrayList());
        deprecated.get(i.getDeprecated()).add(i.getName());
      }
      if (i.getRemoved() != null) {
        removed.putIfAbsent(i.getRemoved(), Lists.newArrayList());
        removed.get(i.getRemoved()).add(i.getName());
      }
      if (i.getChanges() != null) {
        i.getChanges().forEach((v, c) -> {
          changes.putIfAbsent(v, Lists.newArrayList());
          c.forEach(aC -> changes.get(v).add(aC));
        });
      }
      if (i.getBreakingChanges() != null) {
        i.getBreakingChanges().forEach((v, c) -> {
          breaking.putIfAbsent(v, Lists.newArrayList());
          c.forEach(aC -> breaking.get(v).add(aC));
        });
      }
    });

    LinkedHashMap<Version, List<Node>> changeLog = Maps.newLinkedHashMap();
    added.forEach((v, c) -> {
      changeLog.putIfAbsent(v, Lists.newArrayList());
      c.stream().sorted(String::compareTo).forEach(aC -> {
        Li li = new Li().setCSSClass("list-group-item");
        li.appendChild(new Strong().appendText("ADDED: ").setStyle("color: green"));
        li.appendChild(new Text(aC));
        changeLog.get(v).add(li);
      });
    });
    deprecated.forEach((v, c) -> {
      changeLog.putIfAbsent(v, Lists.newArrayList());
      c.stream().sorted(String::compareTo).forEach(aC -> {
        Li li = new Li().setCSSClass("list-group-item");
        li.appendChild(new Strong().appendText("DEPRECATED: ").setStyle("color: orange"));
        li.appendChild(new Text(aC));
        changeLog.get(v).add(li);
      });
    });
    removed.forEach((v, c) -> {
      changeLog.putIfAbsent(v, Lists.newArrayList());
      c.stream().sorted(String::compareTo).forEach(aC -> {
        Li li = new Li().setCSSClass("list-group-item");
        li.appendChild(new Strong().appendText("REMOVED: ").setStyle("color: red"));
        li.appendChild(new Text(aC));
        changeLog.get(v).add(li);
      });
    });
    breaking.forEach((v, c) -> {
      changeLog.putIfAbsent(v, Lists.newArrayList());
      c.stream().sorted(String::compareTo).forEach(aC -> {
        Li li = new Li().setCSSClass("list-group-item");
        li.appendChild(new Strong().appendText("BREAKING: ").setStyle("color: maroon"));
        li.appendChild(new Text(aC));
        changeLog.get(v).add(li);
      });
    });
    changes.forEach((v, c) -> {
      changeLog.putIfAbsent(v, Lists.newArrayList());
      c.stream().sorted(String::compareTo).forEach(aC -> {
        Li li = new Li().setCSSClass("list-group-item");
        li.appendChild(new Strong().appendText("CHANGE: "));
        li.appendChild(new Text(aC));
        changeLog.get(v).add(li);
      });
    });

    LinkedHashMap<Version, List<Node>> sortedNodes = changeLog.entrySet()
        .stream().sorted(Map.Entry.comparingByKey())
        .collect(Collectors
            .toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1, LinkedHashMap::new));

    Span page = new Span();
    page.appendChild(new H1()
        .appendText("Specification Version History")
        .appendChild(new Br()).appendChild(new Small()
            .appendText("Track all the changes that have happened with each specification update."))
    );
    page.appendChild(new Br()).appendChild(new Br());
    sortedNodes.forEach((v, n) -> {
      H3 h3 = new H3().appendText(v.toString() + " ");
      if (v == SpecificationVersionHistory.CURRENT) {
        h3.appendChild(new Span().setCSSClass("label label-success").appendText("LATEST"));
      }
      page.appendChild(h3);
      Ul ul = new Ul().setCSSClass("list-group");
      n.forEach(ul::appendChild);
      page.appendChild(ul);
    });

    writer.append(page.write());
    writer.close();
  }

  public static void createExamplePaths(FeatureDocumentation documentation, Element parent) {
    Element main = new Element(documentation.getSafeName());

    Element example = new Element("example");
    example.addContent(new Comment("TODO: Add example for " + documentation.getName()));
    main.addContent(example);

    documentation.getSubFeatures().forEach(f -> createExamplePaths(f, main));

    parent.addContent(main);
  }

  public static HashMap<FeatureDocumentation, Element> mapExamples(
      FeatureDocumentation documentation, Element parent) {
    HashMap<FeatureDocumentation, Element> res = Maps.newHashMap();

    if (parent == null) {
      throw new RuntimeException("Could not find parent element for " + documentation.getName());
    }

    Element example = parent.getChild("example");
    if (example == null) {
      Bukkit.getLogger().warning(documentation.getName() + " does not have an example!");
    } else {
      res.put(documentation, example);
    }

    if (documentation.getSubFeatures() != null) {
      documentation.getSubFeatures().forEach(f -> {
        Element child = parent.getChild(f.getSafeName());
        if (child == null) {
          throw new RuntimeException("Could not find child element for " + f.getSafeName());
        }
        res.putAll(mapExamples(f, child));
      });
    }

    return res;
  }
}
