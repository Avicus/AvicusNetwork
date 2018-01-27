package net.avicus.atlas.util.xml.conditionals;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.modifiers.NotCheck;
import org.jdom2.Element;

public abstract class Conditional {

  @Getter
  private final List<Element> elements;
  @Getter
  @Setter
  private Check check;

  public Conditional(Check check, List<Element> elements) {
    this.check = check;
    this.elements = elements;
  }

  public Conditional inverse() {
    this.check = new NotCheck(this.check);
    return this;
  }

  public boolean shouldExclude(CheckContext context) {
    return this.check.test(context).fails();
  }
}
