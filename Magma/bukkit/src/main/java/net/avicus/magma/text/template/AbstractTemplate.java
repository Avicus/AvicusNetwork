package net.avicus.magma.text.template;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * An abstract implementation of a template.
 *
 * @param <C> the component type
 * @param <B> the component builder type
 * @param <S> the type of this template
 */
public abstract class AbstractTemplate<C, B, S extends AbstractTemplate<C, B, S>> {

  /**
   * A pattern to find template arguments.
   */
  private static final Pattern ARG_PATTERN = Pattern.compile("\\{(\\d*)\\}");
  /**
   * An array of argument positions to children positions.
   */
  @Nonnull
  protected final int[] positions;
  /**
   * A list of children components.
   */
  @Nonnull
  protected final List<C> children = Lists.newArrayList();

  /**
   * Parse a string into a template.
   *
   * @param string the string to parse
   */
  protected AbstractTemplate(@Nonnull final String string) {
    final Matcher matcher = ARG_PATTERN.matcher(string);
    int[] positions = new int[0];
    int position = 0;

    while (matcher.find(position)) {
      final int start = matcher.start();
      if (start > position) {
        this.children.add(this.createComponent(string.substring(position, start)));
      }

      position = matcher.end();

      final int index = Integer.parseInt(matcher.group(1));
      if (index + 1 > positions.length) {
        positions = Arrays.copyOf(positions, index + 1);
      }

      positions[index] = this.children.size();

      this.children.add(this.createComponent(string.substring(start, position)));
    }

    if (position < string.length()) {
      this.children.add(this.createComponent(string.substring(position)));
    }

    this.positions = positions;
  }

  /**
   * A constructor to create a copy of a template.
   *
   * @param template the template to copy
   */
  protected AbstractTemplate(@Nonnull final S template) {
    this.positions = template.positions;
    this.children.addAll(template.children);
  }

  /**
   * Create a copy of this template.
   *
   * @return a copy of this template
   */
  @Nonnull
  protected abstract S copy();

  /**
   * Create an argument.
   *
   * @param object the argument content
   * @return the argument
   */
  @Nonnull
  protected abstract C createComponent(@Nullable final Object object);

  /**
   * Populate this template with arguments.
   *
   * @param args the arguments
   * @return a copy of this template, populated with the arguments
   */
  @Nonnull
  public S with(@Nonnull final Object... args) {
    final S copy = this.copy();

    // Allow more than the required amount of arguments to be supplied, but ignore them. This allows the developer to pass optional
    // information which may be used in a template.
    if (copy.positions.length > args.length) {
      throw new IllegalArgumentException(String
          .format("Wrong number of arguments supplied. Expected %d, got %d", copy.positions.length,
              args.length));
    }

    for (int i = 0; i < copy.positions.length; i++) {
      copy.children.set(copy.positions[i], this.createComponent(args[i]));
    }

    return copy;
  }

  /**
   * Build this template.
   *
   * @return the built template
   */
  @Nonnull
  public abstract C build();

  /**
   * Build this template.
   *
   * @param builder the builder
   * @return the built template
   */
  @Nonnull
  public abstract C build(@Nonnull final B builder);

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("positions", this.positions)
        .add("children", this.children)
        .toString();
  }
}
