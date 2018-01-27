package net.avicus.atlas.documentation;

import com.google.common.collect.Lists;
import java.util.LinkedList;
import lombok.Getter;

public class InfoTable {

  @Getter
  private final String title;
  @Getter
  private final String[] header;
  @Getter
  private final LinkedList<String[]> rows = Lists.newLinkedList();

  public InfoTable(String title, String... header) {
    this.title = title;
    this.header = header;
  }

  public InfoTable row(String... data) {
    rows.add(data);
    return this;
  }
}
