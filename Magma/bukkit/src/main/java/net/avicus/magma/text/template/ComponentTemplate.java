package net.avicus.magma.text.template;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

/**
 * A {@link BaseComponent} based template.
 */
public class ComponentTemplate extends
    AbstractTemplate<BaseComponent, TextComponent, ComponentTemplate> {

  public ComponentTemplate(@Nonnull String string) {
    super(string);
  }

  private ComponentTemplate(@Nonnull final ComponentTemplate template) {
    super(template);
  }

  @Nonnull
  @Override
  protected ComponentTemplate copy() {
    return new ComponentTemplate(this);
  }

  @Nonnull
  @Override
  protected BaseComponent createComponent(@Nullable Object object) {
    if (object instanceof BaseComponent) {
      return (BaseComponent) object;
    }

    return new TextComponent(String.valueOf(object));
  }

  @Nonnull
  @Override
  public BaseComponent build() {
    return this.build(new TextComponent(""));
  }

  @Nonnull
  @Override
  public BaseComponent build(@Nonnull TextComponent builder) {
    BaseComponent previous = builder;
    for (BaseComponent child : this.children) {
      if (previous != null && child instanceof TextComponent) {
        ((TextComponent) child)
            .setText(this.lastColorsOf(previous) + ((TextComponent) child).getText());
      }

      builder.addExtra(child);
      previous = child;
    }

    return builder;
  }

  private String lastColorsOf(BaseComponent component) {
    if (!(component instanceof TextComponent)) {
      return null;
    }

    return ChatColor.getLastColors(((TextComponent) component).getText());
  }
}
