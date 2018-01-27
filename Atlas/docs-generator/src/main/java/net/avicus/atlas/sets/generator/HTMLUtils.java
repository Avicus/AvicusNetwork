package net.avicus.atlas.sets.generator;

import com.hp.gagawa.java.elements.A;
import com.hp.gagawa.java.elements.Br;
import com.hp.gagawa.java.elements.Code;
import com.hp.gagawa.java.elements.Div;
import com.hp.gagawa.java.elements.H1;
import com.hp.gagawa.java.elements.H2;
import com.hp.gagawa.java.elements.H3;
import com.hp.gagawa.java.elements.H4;
import com.hp.gagawa.java.elements.H5;
import com.hp.gagawa.java.elements.P;
import com.hp.gagawa.java.elements.Small;
import com.hp.gagawa.java.elements.Span;
import com.hp.gagawa.java.elements.Table;
import com.hp.gagawa.java.elements.Tbody;
import com.hp.gagawa.java.elements.Td;
import com.hp.gagawa.java.elements.Text;
import com.hp.gagawa.java.elements.Thead;
import com.hp.gagawa.java.elements.Tr;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.documentation.FeatureDocumentation;
import net.avicus.atlas.documentation.InfoTable;
import net.avicus.atlas.documentation.ModuleDocumentation;
import net.avicus.atlas.documentation.SpecInformation;
import net.avicus.atlas.documentation.attributes.Attribute;
import net.avicus.atlas.module.ModuleFactory;
import net.avicus.compendium.StringUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class HTMLUtils {

  private static final Text HILIGHT_INLINE = new Text("{% ihighlight xml %}");
  private static final Text HILIGHT_INLINE_END = new Text("{% endihighlight %}");
  private static final Text HILIGHT = new Text("{% highlight xml %}");
  private static final Text HILIGHT_END = new Text("{% endhighlight %}");

  public static Table createTable(String[] header, LinkedList<String[]> rows) {
    Table table = new Table();
    table.setCSSClass("table table-condensed");

    Thead head = new Thead();

    Tr row = new Tr();

    for (String s : header) {
      row.appendChild(new Td().appendText(s));
    }
    head.appendChild(row);

    Tbody tbody = new Tbody();

    rows.forEach(r -> {
      Tr tRow = new Tr();
      for (String s : r) {
        tRow.appendChild(new Td().appendText(s));
      }
      tbody.appendChild(tRow);
    });

    table.appendChild(head);
    table.appendChild(tbody);
    return table;
  }

  public static Span generateModule(ModuleDocumentation documentation,
      HashMap<FeatureDocumentation, Element> examples) {
    Span span = new Span();

    H1 title = new H1().appendText(documentation.getName());

    if (documentation.getTagNames() != null && !documentation.getTagNames().isEmpty()) {
      Small small = new Small();
      documentation.getTagNames().forEach(t -> {
        small.appendChild(HILIGHT_INLINE);
        small.appendChild(new Text("<" + t + ">"));
        small.appendChild(HILIGHT_INLINE_END);
      });
      title.appendChild(small);
    }
    span.appendChild(title);

    if (documentation.getDescription() != null && !documentation.getDescription().isEmpty()) {
      documentation.getDescription().forEach(d -> span.appendChild(new P().appendText(d)));
    }

    documentation.getRequirements().forEach(r -> {
      ModuleFactory f = Atlas.get().getMatchFactory().getFactory(r);
      span.appendChild(new Code().appendText("REQUIREMENT:"))
          .appendChild(new Text("This module requires the "))
          .appendChild(new A(f.getDocumentation().getLink())
              .appendText(f.getDocumentation().getName() + " Module"))
          .appendChild(new Text("."));
      span.appendChild(new Br());
    });

    if (!documentation.getRequirements().isEmpty()) {
      span.appendChild(new Br());
    }

    if (documentation.getSpecInformation() != null) {
      span.appendChild(specInfoTable(documentation.getSpecInformation()));
    }

    if (documentation.getTables() != null) {
      documentation.getTables().forEach(t -> span.appendChild(toHtml(t)));
    }

    documentation.getFeatures().forEach(f -> span.appendChild(generateSection(f, false, examples)));

    return span;
  }

  public static Div toHtml(InfoTable table) {
    Div div = new Div();
    div.setCSSClass("table-responsive");

    div.appendChild(new H5().appendText(table.getTitle()));
    div.appendChild(HTMLUtils.createTable(table.getHeader(), table.getRows()));

    return div;
  }

  public static Span generateSection(FeatureDocumentation documentation, boolean sub,
      HashMap<FeatureDocumentation, Element> examples) {
    Span span = new Span();

    Small small = new Small();
    if (documentation.getTagNames() != null && !documentation.getTagNames().isEmpty()) {
      documentation.getTagNames().forEach(t -> {
        small.appendChild(HILIGHT_INLINE);
        small.appendChild(new Text("<" + t + ">"));
        small.appendChild(HILIGHT_INLINE_END);
      });
    }

    if (sub) {
      H3 title = new H3().appendText(documentation.getName());
      title.appendChild(small);
      span.appendChild(title);
    } else {
      H2 title = new H2().appendText(documentation.getName());
      title.appendChild(small);
      span.appendChild(title);
    }

    if (documentation.getRequirements() != null) {
      documentation.getRequirements().forEach(r -> {
        ModuleFactory f = Atlas.get().getMatchFactory().getFactory(r);
        span.appendChild(new Code().appendText("REQUIREMENT:"))
            .appendChild(new Text("This feature requires the "))
            .appendChild(new A(f.getDocumentation().getLink())
                .appendText(f.getDocumentation().getName() + " Module"))
            .appendChild(new Text("."));
        span.appendChild(new Br());
      });
    }

    if (documentation.getRequirements() != null && !documentation.getRequirements().isEmpty()) {
      span.appendChild(new Br());
    }

    documentation.getDescription().forEach(d -> span.appendChild(new P().appendText(d)));

    if (documentation.getSpecInformation() != null) {
      span.appendChild(specInfoTable(documentation.getSpecInformation()));
    }

    Element example = examples.get(documentation);

    if (example != null) {
      XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
      span.appendChild(HILIGHT);
      span.appendChild(
          new Text(StringUtil.join(example.getContent(), "\n", new StringUtil.Stringify<Content>() {
            @Override
            public String on(Content object) {
              return outputter.outputString(Arrays.asList(object));
            }
          })));
      span.appendChild(HILIGHT_END);
    } else {
      Bukkit.getLogger().warning(documentation.getName() + " has no example.");
    }

    span.appendChild(new Br());

    if (documentation.getText() != null) {
      span.appendChild(new H4().appendText("Element Text"));
      span.appendChild(textTable(documentation.getText()));
    }

    if (documentation.getAttributes() != null && !documentation.getAttributes().isEmpty()) {
      span.appendChild(attributesTable(documentation));
    }

    if (documentation.getTables() != null && !documentation.getTables().isEmpty()) {
      span.appendChild(new Br());
      documentation.getTables().forEach(t -> span.appendChild(toHtml(t)));
      span.appendChild(new Br());
    }

    if (documentation.getSubFeatures() != null) {
      documentation.getSubFeatures()
          .forEach(f -> span.appendChild(generateSection(f, true, examples)));
    }

    return span;
  }

  public static Div attributesTable(FeatureDocumentation documentation) {
    Div div = new Div();
    div.setCSSClass("table-responsive");
    div.appendChild(new H5().appendText(documentation.getName() + " Attributes"));

    Table table = new Table();
    table.setCSSClass("table table-condensed");

    Thead head = new Thead();

    Tr row = new Tr();

    boolean hasDef = false;
    for (Pair<Attribute, Object> pair : documentation.getAttributes().values()) {
      if (pair.getValue() != null) {
        hasDef = true;
        break;
      }
    }
    row.appendChild(new Td().appendText("Attribute"));
    row.appendChild(new Td().appendText("Description"));
    row.appendChild(new Td().appendText("Type"));
    if (hasDef) {
      row.appendChild(new Td().appendText("Default"));
    }

    head.appendChild(row);
    table.appendChild(head);

    Tbody tbody = new Tbody();

    final boolean useDef = hasDef;

    documentation.getAttributes().entrySet().forEach(e -> tbody.appendChild(tableRow(e, useDef)));
    table.appendChild(tbody);
    div.appendChild(table);

    return div;
  }

  public static Table textTable(Pair<Attribute, Object> text) {
    Table table = new Table();
    table.setCSSClass("table table-condensed");

    Thead head = new Thead();

    Tr row = new Tr();

    row.appendChild(new Td().appendText("Description"));
    row.appendChild(new Td().appendText("Type"));
    if (text.getValue() != null) {
      row.appendChild(new Td().appendText("Default"));
    }
    head.appendChild(row);
    table.appendChild(head);

    Tbody tbody = new Tbody();

    Tr tr = new Tr();

    Td desc = new Td();
    desc.setCSSClass("width: 65%");
    for (String d : text.getKey().getDescription()) {
      desc.appendChild(new P().appendText(d));
    }
    tr.appendChild(desc);

    Td type = new Td();
    type.appendChild(
        new Span().setCSSClass("label label-primary").appendText(text.getKey().getName()));
    if (!text.getKey().getLink().isEmpty()) {
      type.appendChild(new Br());
      type.appendChild(new A(text.getKey().getLink()).appendText("Possible Values"));
    }
    if (text.getKey().getValues().length > 0) {
      type.appendChild(new Br()).appendChild(new Br());
      int done = 0;
      for (int i = 0; i < text.getKey().getValues().length; i++) {
        done++;
        boolean last = i == text.getKey().getValues().length - 1;
        String v = text.getKey().getValues()[i];
        type.appendChild(new Code().appendText(v));
        if (!last) {
          type.appendChild(new Text(","));
        }
        if (v.length() > 10 || done > 5) {
          type.appendChild(new Br());
          done = 0;
        }
      }
    }
    tr.appendChild(type);

    if (text.getValue() != null) {
      tr.appendChild(new Td().appendChild(new Code().appendText(text.getValue().toString())));
    }
    tbody.appendChild(tr);

    table.appendChild(tbody);

    return table;
  }

  public static Table specInfoTable(SpecInformation information) {
    Table table = new Table();
    table.setCSSClass("table table-condensed");
    table.appendChild(new Thead().appendChild(new Tr()
        .appendChild(new Td().appendText("Specification"))
        .appendChild(new Td().appendText("Changes"))
    ));
    Tbody tbody = new Tbody();

    if (information.getAdded() != null) {
      tbody.appendChild(new Tr().setCSSClass("bg-success").appendChild(
          new Td().appendText(information.getAdded().toString())
              .appendChild(new Td().appendText("ADDED"))));
    }

    if (information.getBreakingChanges() != null && !information.getBreakingChanges().isEmpty()) {
      information.getBreakingChanges().forEach((v, c) ->
          c.forEach(aC -> tbody.appendChild(new Tr().setCSSClass("bg-warning")
              .appendChild(new Td().appendText(v.toString()).appendChild(new Td().appendText(aC)))))
      );
    }

    if (information.getChanges() != null && !information.getChanges().isEmpty()) {
      information.getChanges().forEach((v, c) ->
          c.forEach(aC -> tbody.appendChild(new Tr().setCSSClass("bg-info")
              .appendChild(new Td().appendText(v.toString()).appendChild(new Td().appendText(aC)))))
      );
    }

    if (information.getDeprecated() != null) {
      tbody.appendChild(new Tr().setCSSClass("bg-warning").appendChild(
          new Td().appendText(information.getDeprecated().toString())
              .appendChild(new Td().appendText("DEPRECATED"))));
    }

    if (information.getRemoved() != null) {
      tbody.appendChild(new Tr().setCSSClass("bg-danger").appendChild(
          new Td().appendText(information.getRemoved().toString())
              .appendChild(new Td().appendText("REMOVED"))));
    }

    table.appendChild(tbody);

    return table;
  }

  public static Tr tableRow(Map.Entry<String, Pair<Attribute, Object>> row, boolean showDef) {
    Tr tr = new Tr();

    if (row.getValue().getKey().isRequired()) {
      tr.setCSSClass("info");
    }

    Attribute attribute = row.getValue().getKey();
    Object def = row.getValue().getValue();

    tr.appendChild(new Td().appendChild(new Code().appendText(row.getKey())));
    Td desc = new Td();
    desc.setCSSClass("width: 65%");
    for (String d : attribute.getDescription()) {
      desc.appendChild(new P().appendText(d));
    }
    tr.appendChild(desc);

    Td type = new Td();
    type.appendChild(new Span().setCSSClass("label label-primary").appendText(attribute.getName()));
    if (!attribute.getLink().isEmpty()) {
      type.appendChild(new Br());
      type.appendChild(new A(attribute.getLink()).appendText("Possible Values"));
    }
    if (attribute.getValues().length > 0) {
      type.appendChild(new Br()).appendChild(new Br());
      int done = 0;
      for (int i = 0; i < attribute.getValues().length; i++) {
        done++;
        boolean last = i == attribute.getValues().length - 1;
        String v = attribute.getValues()[i];
        type.appendChild(new Code().appendText(v));
        if (!last) {
          type.appendChild(new Text(","));
        }
        if (v.length() > 10 || done > 5) {
          type.appendChild(new Br());
          done = 0;
        }
      }
    }
    tr.appendChild(type);

    if (showDef) {
      Td td = new Td();
      if (def != null) {
        td.appendChild(new Code().appendText(def.toString()));
      }
      tr.appendChild(td);
    }

    return tr;
  }
}
