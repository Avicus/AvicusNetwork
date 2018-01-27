package net.avicus.magma.util;

import com.google.common.base.Joiner;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import net.avicus.compendium.locale.LocaleBundle;
import net.avicus.compendium.locale.LocaleStrings;
import net.avicus.compendium.locale.text.LocalizedFormat;
import org.bukkit.plugin.Plugin;
import org.jdom2.JDOMException;

public class TranslationProvider {

  public static final LocalizedFormat $NULL$ = new LocalizedFormat(null, null);
  private static final Field MODIFIERS_FIELD;
  private static final Joiner JOINER = Joiner.on(".");

  static {
    try {
      MODIFIERS_FIELD = Field.class.getDeclaredField("modifiers");
      MODIFIERS_FIELD.setAccessible(true);
    } catch (NoSuchFieldException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  public static void map(Class<?> clazz, LocaleBundle bundle) {
    try {
      for (Field field : clazz.getFields()) {
        field.setAccessible(true);
        MODIFIERS_FIELD.set(field, field.getModifiers() & ~Modifier.FINAL);
        if (field.get(null) == $NULL$) {
          field.set(null, bundle.getFormat(JOINER.join(field.getName().toLowerCase().split("_"))));
        }
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public static LocaleBundle loadBundle(Plugin plugin, String... locales) {
    try {
      final List<LocaleStrings> list = new ArrayList<>();
      final LocaleStrings english = getLocaleStrings(plugin, "locale/en_US.xml");
      for (String locale : locales) {
        list.add(getLocaleStrings(plugin, String.format("locale/%s.xml", locale)));
      }
      return new LocaleBundle(list, english);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to load translations", e);
    }
  }

  private static LocaleStrings getLocaleStrings(Plugin plugin, String resource)
      throws JDOMException, IOException {
    try (InputStream input = plugin.getResource(resource)) {
      return LocaleStrings.fromXml(input);
    }
  }
}
