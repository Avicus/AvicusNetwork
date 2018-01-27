package net.avicus.atlas.match;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import net.avicus.atlas.map.AtlasMap;
import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class IncludeProcessor {

  private final AtlasMap map;
  private final Document document;
  private final SAXBuilder sax;

  public IncludeProcessor(AtlasMap map, Document document, SAXBuilder sax) {
    this.map = map;
    this.document = document;
    this.sax = sax;
  }

  public boolean shouldProcess() {
    return this.document.getRootElement().getChild("include") != null;
  }

  public void process() throws JDOMException, IOException {
    List<Element> list = this.document.getRootElement().getChildren();
    List<Element> clone = new ArrayList<>(list);
    for (Element element : clone) {
      if (element.getName().equals("include")) {
        String src = element.getAttributeValue("src");
        boolean overwrite = element.getAttributeValue("overwrite", "false").equals("true");
        boolean local = element.getAttributeValue("local", "false").equals("true");

        InputStream file;
        if (local) {
          file = this.map.getSource().getFile(src);
        } else {
          file = this.map.getSource().getLibrary().getFileStream(src);
        }

        Document merge = this.sax.build(file);
        merge(merge, overwrite);

        this.document.getRootElement().removeContent(element);
      }
    }
  }

  private void merge(Document merge, boolean overwrite) {
    List<Content> content = merge.getRootElement().getContent();
    merge(this.document.getRootElement(), content, overwrite);
  }

  private void merge(Element current, List<Content> contents, boolean overwrite) {
    contents = new ArrayList<>(contents);

    for (Content content : contents) {
      if (content instanceof Element) {
        Element element = (Element) content;

        List<Element> children = current.getChildren();

        Element existing = null;
        for (Element child : children) {
          if (!child.getName().equals(element.getName())) {
            continue;
          }

          boolean sameAttributes = true;
          for (Attribute attribute : child.getAttributes()) {
            Attribute test = element.getAttribute(attribute.getName());

            if (test == null || !test.getValue().equals(attribute.getValue())) {
              sameAttributes = false;
              break;
            }
          }

          if (sameAttributes) {
            existing = child;
            break;
          }
        }

        if (overwrite) {
          if (existing != null) {
            current.removeChild(existing.getName());
          }
          current.addContent(element.detach());
        } else {
          // Add element if it isn't there, otherwise dive deeper
          if (existing == null) {
            current.addContent(element.detach());
          } else {
            merge(existing, element.getContent(), false);
          }
        }
      }
    }
  }
}
