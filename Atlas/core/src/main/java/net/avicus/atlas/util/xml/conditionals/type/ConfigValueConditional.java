package net.avicus.atlas.util.xml.conditionals.type;

import java.util.List;
import net.avicus.atlas.Atlas;
import net.avicus.atlas.module.checks.CheckResult;
import net.avicus.atlas.module.checks.StaticResultCheck;
import net.avicus.atlas.util.xml.conditionals.Conditional;
import org.jdom2.Element;

public class ConfigValueConditional extends Conditional {

  public ConfigValueConditional(String query, String value, List<Element> elements) {
    super(new StaticResultCheck(CheckResult.IGNORE), elements);
    super.setCheck(new StaticResultCheck(getValue(query, value)));
  }

  private CheckResult getValue(String query, String value) {
    String configRes = Atlas.get().getConfig().getString("variables." + query);
    if (configRes != null && configRes.equals(value)) {
      return CheckResult.ALLOW;
    }

    return CheckResult.DENY;
  }
}
