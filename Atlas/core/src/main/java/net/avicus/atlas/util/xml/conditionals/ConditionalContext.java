package net.avicus.atlas.util.xml.conditionals;


import java.util.ArrayList;
import java.util.List;
import net.avicus.atlas.module.checks.CheckContext;
import org.jdom2.Element;

/**
 * A single if/unless/else context.
 */
public class ConditionalContext {

  private final Conditional conditional;
  private final List<Element> elseElements;

  public ConditionalContext(Conditional conditional, List<Element> elseElements) {
    this.conditional = conditional;
    this.elseElements = elseElements;
  }

  public List<Element> getPassingElements(CheckContext context) {
    List<Element> result = new ArrayList<>();
    if (!this.conditional.shouldExclude(context)) {
      result.addAll(this.conditional.getElements());
    }

    // Didn't pass, add else
    if (result.isEmpty()) {
      result.addAll(this.elseElements);
    }

    return result;
  }
}
