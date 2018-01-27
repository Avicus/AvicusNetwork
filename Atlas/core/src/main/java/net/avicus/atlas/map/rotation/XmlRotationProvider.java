package net.avicus.atlas.map.rotation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.avicus.atlas.AtlasConfig;
import net.avicus.atlas.map.MapManager;
import net.avicus.atlas.match.Match;
import net.avicus.atlas.match.MatchFactory;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class XmlRotationProvider extends AbstractFileRotationProvider {

  public XmlRotationProvider(File file, MapManager mm, final MatchFactory factory) {
    super(file, mm, factory);
  }

  @Override
  protected Rotation createRotation() {
    final Element root = this.createRootElement();
    final boolean shuffle = Boolean.valueOf(
        root.getAttributeValue("shuffle", Boolean.toString(AtlasConfig.isRotationRandomize())));
    final List<Match> maps = new ArrayList<>();
    for (Element element : root.getChildren("map")) {
      @Nullable final Match match = this.createMatch(element.getValue());
      if (match != null) {
        maps.add(match);
      }
    }

    final Rotation rotation = this.defineRotation(maps);
    if (shuffle) {
      Collections.shuffle(rotation.getMatches());
    }
    return rotation;
  }

  private Element createRootElement() {
    final SAXBuilder sax = new SAXBuilder();
    final Document doc;
    try {
      doc = sax.build(this.file);
    } catch (IOException | JDOMException e) {
      throw new RuntimeException(e);
    }

    return doc.getRootElement();
  }
}
