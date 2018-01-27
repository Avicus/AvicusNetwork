package net.avicus.magma.database.model.impl;

import com.lambdaworks.com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.ToString;
import net.avicus.magma.database.table.impl.AchievementTable;
import net.avicus.quest.annotation.Column;
import net.avicus.quest.annotation.Id;
import net.avicus.quest.model.Model;

@ToString
@Getter
public class Achievement extends Model {

  @Id
  @Column
  private int id;

  @Column
  private String slug;

  @Column
  private String name;

  @Column
  private String description;

  public Achievement() {

  }

  public Achievement(String slug) {
    this.slug = slug;
    this.name = slug;
  }

  public int numberLoc() {
    String[] slug = getSlug().split("-");
    for (int i = 0; i < slug.length; i++) {
      try {
        Integer.parseInt(slug[i]);
        return i;
      } catch (NumberFormatException e) {
      }
    }

    return -1;
  }

  public String getRawSlug() {
    int num = getNum();
    if (num != 0) {
      String[] nameRaw = getSlug().split("-");
      String[] name;
      int loc = numberLoc();
      if (loc > -1) {
        if (loc > 0) {
          name = Arrays.copyOfRange(nameRaw, 0, loc);
        } else {
          name = Arrays.copyOfRange(nameRaw, loc + 1, nameRaw.length);
        }
        return String.join("-", name);
      }
      return getSlug();
    }
    return getSlug();
  }

  public int getNum() {
    Pattern pattern = Pattern.compile("[0-9]+");
    Matcher matcher = pattern.matcher(getSlug());

    if (matcher.find()) {
      return Integer.parseInt(matcher.group());
    }
    return 0;
  }

  public List<Achievement> getLower(AchievementTable table) {
    List<Achievement> res = Lists.newArrayList();

    int num = getNum();
    if (num != 0) {
      String slug = getRawSlug();
      table.softFind(slug).forEach(ac -> {
        int thisNum = ac.getNum();
        if (thisNum > 0 && num > thisNum) {
          res.add(ac);
        }
      });
    }

    return res;
  }
}
