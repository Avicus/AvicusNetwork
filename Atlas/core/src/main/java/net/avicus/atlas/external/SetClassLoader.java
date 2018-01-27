package net.avicus.atlas.external;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import net.avicus.atlas.Atlas;

public class SetClassLoader {

  public static URLClassLoader addFile(String s) throws IOException {
    File f = new File(s);
    return addFile(f);
  }

  public static URLClassLoader addFile(File f) throws IOException {
    return addURL(f.toURI().toURL());
  }

  public static URLClassLoader addURL(URL u) throws IOException {
    URLClassLoader sysloader = ((URLClassLoader) Atlas.class.getClassLoader());
    Class sysclass = URLClassLoader.class;

    try {
      Method method = sysclass.getDeclaredMethod("addURL", URL.class);
      method.setAccessible(true);
      method.invoke(sysloader, u);
    } catch (Throwable t) {
      t.printStackTrace();
      throw new IOException("Error, could not add URL to system classloader");
    }

    return sysloader;
  }
}
