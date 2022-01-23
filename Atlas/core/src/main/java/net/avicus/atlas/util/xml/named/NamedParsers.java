package net.avicus.atlas.util.xml.named;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.avicus.atlas.util.xml.XmlElement;
import net.avicus.atlas.util.xml.XmlException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NamedParsers {

  public static Map<Method, Collection<String>> methods(final Class<?> clazz) {
    final Multimap<Method, String> result = HashMultimap.create();
    for (final Method method : clazz.getDeclaredMethods()) {
      @Nullable final NamedParser parser = method.getAnnotation(NamedParser.class);
      if (parser != null) {
        method.setAccessible(true);
        for (String name : parser.value()) {
          if (result.containsValue(name)) {
            throw new IllegalStateException(String
                .format("Attempted to register %s identifier twice in %s", name, clazz.getName()));
          }
          result.put(method, name);
        }
      }
    }
    return result.asMap();
  }

  public static <T> T invokeMethod(Table<Object, Method, Collection<String>> parsers, XmlElement element,
      String notFoundMessage, Object[] methodArgs) throws XmlException {
    for (Table.Cell<Object, Method, Collection<String>> cell : parsers.cellSet()) {
      if (!cell.getValue().contains(element.getName())) {
        continue;
      }

      try {
        return (T) cell.getColumnKey().invoke(cell.getRowKey(), methodArgs);
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
