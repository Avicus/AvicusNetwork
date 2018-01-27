package net.avicus.atlas.documentation.attributes;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;

public class EnumAttribute implements Attribute {

  private final Class<? extends Enum> enumClazz;
  @Getter
  private final String[] description;
  @Getter
  private String[] values;
  @Getter
  private boolean required;

  public EnumAttribute(Class<? extends Enum> e, boolean required, String... description) {
    this.enumClazz = e;
    this.description = description;
    this.required = required;
    this.values = new String[]{};
    List<String> values = Lists.newArrayList();
    for (Enum anEnum : e.getEnumConstants()) {
      values.add(anEnum.name().toLowerCase().replace("_", " "));
    }
    this.values = values.toArray(this.values);
  }

  @Override
  public String getName() {
    return StringUtils
        .join(this.enumClazz.getSimpleName().split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])"),
            " ");
  }
}
