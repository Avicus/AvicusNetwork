package net.avicus.atlas.module.checks.types;

import java.util.Optional;
import lombok.ToString;
import net.avicus.atlas.module.checks.Check;
import net.avicus.atlas.module.checks.CheckContext;
import net.avicus.atlas.module.checks.CheckResult;
import net.avicus.atlas.module.checks.variable.ItemVariable;
import net.avicus.atlas.util.ScopableItemStack;

/**
 * An item check checks the type of item involved in the event.
 */
@ToString
public class ItemCheck implements Check {

  private final ScopableItemStack itemStack;

  public ItemCheck(ScopableItemStack itemStack) {
    this.itemStack = itemStack;
  }

  @Override
  public CheckResult test(CheckContext context) {
    Optional<ItemVariable> optional = context.getFirst(ItemVariable.class);

    if (!optional.isPresent()) {
      return CheckResult.IGNORE;
    }

    ScopableItemStack stack = optional.get().getItemStack();
    return CheckResult.valueOf(this.itemStack.equals(stack));
  }
}
