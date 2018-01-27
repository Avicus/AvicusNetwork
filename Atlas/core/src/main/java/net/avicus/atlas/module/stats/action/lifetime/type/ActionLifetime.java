package net.avicus.atlas.module.stats.action.lifetime.type;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.ToString;
import net.avicus.atlas.module.stats.action.base.Action;

@ToString
public abstract class ActionLifetime<T extends Action> {

  @Getter
  private final Instant start;
  private final List<T> actions = Lists.newArrayList();
  @Getter
  private Instant end;

  public ActionLifetime(Instant start) {
    this.start = start;
  }

  public void end() {
    this.end = Instant.now();
  }

  public
  @Nonnull
  List<T> getActions() {
    return Collections.unmodifiableList(this.actions);
  }

  public
  @Nonnull
  ListIterator<T> getActionsFirst() {
    return Collections.unmodifiableList(this.actions).listIterator();
  }

  public
  @Nonnull
  ListIterator<T> getActionsLast() {
    return Collections.unmodifiableList(this.actions).listIterator(this.actions.size());
  }

  public void addAction(T action) {
    this.actions.add(action);
  }

  @Nullable
  public T lastAction() {
    if (!this.actions.isEmpty()) {
      return this.actions.get(this.actions.size() - 1);
    } else {
      return null;
    }
  }

  @Nullable
  public T lastAction(Class<? extends T> actionClazz) {
    for (ListIterator<T> it = this.getActionsLast(); it.hasPrevious(); ) {
      T action = it.previous();
      if (actionClazz.isInstance(action)) {
        return action;
      }
    }

    return null;
  }

  @Nullable
  public T firstAction() {
    if (!this.actions.isEmpty()) {
      return this.actions.get(0);
    } else {
      return null;
    }
  }

  @Nullable
  public T firstAction(Class<? extends T> actionClazz) {
    for (ListIterator<T> it = this.getActionsFirst(); it.hasPrevious(); ) {
      T action = it.previous();
      if (actionClazz.isInstance(action)) {
        return action;
      }
    }

    return null;
  }

  @Nullable
  public <N, C extends Action> N mostCommonAttribute(Class<? extends C> actionClass,
      Function<C, N> refMethod) {
    Multiset<N> commons = HashMultiset.create();
    this.getActions().stream().filter(act -> act.getClass().equals(actionClass))
        .forEach(action -> commons.add(refMethod.apply((C) action)));

    return commons.entrySet()
        .stream()
        .max(Comparator.comparing(Multiset.Entry::getCount)).map(Multiset.Entry::getElement)
        .orElse(null);
  }
}
