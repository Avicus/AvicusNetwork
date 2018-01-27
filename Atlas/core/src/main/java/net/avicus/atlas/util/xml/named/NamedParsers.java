package net.avicus.atlas.util.xml.named;

import com.google.common.collect.Table;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.atlas.util.xml.XmlException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NamedParsers {

  public static Map<String, Method> methods(final Class<?> clazz) {
    final Map<String, Method> result = new HashMap<>();
    for (final Method method : clazz.getDeclaredMethods()) {
      @Nullable final NamedParser parser = method.getAnnotation(NamedParser.class);
      if (parser != null) {
        method.setAccessible(true);
        for (String name : parser.value()) {
          Method old;
          if ((old = result.put(name, method)) != null) {
            throw new IllegalStateException(String
                .format("Attempted to replace %s parser %s with %s", clazz.getName(), old.getName(),
                    method.getName()));
          }
        }
      }
    }
    return result;
  }

  public static <T> T invokeMethod(Table<Object, String, Method> parsers, XmlElement element,
      String notFoundMessage, Object[] methodArgs) throws XmlException {
    for (Table.Cell<Object, String, Method> cell : parsers.cellSet()) {
      if (!cell.getColumnKey().equals(element.getName())) {
        continue;
      }

      try {
        return (T) cell.getValue().invoke(cell.getRowKey(), methodArgs);
      } catch (Exception e) {
        if (e.getCause() != null) {
          if (e.getCause() instanceof XmlException) {
            throw (XmlException) e.getCause();
          }
          throw new XmlException(element, e.getCause());
        }
        throw new XmlException(element, e);
      }
    }

    throw new XmlException(element, notFoundMessage);
  }
}
