package net.avicus.atlas.external;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.UnknownDependencyException;

/**
 * Class to handle the loading and unloading of external modules.
 */
public class SetLoader {

  private File directory;

  @Getter
  private List<ExternalModuleInfo> loadedModules;

  public SetLoader(File directory) {
    this.directory = directory;
    this.loadedModules = Lists.newArrayList();

    if (!directory.exists()) {
      if (directory.mkdirs()) {
        Bukkit.getLogger()
            .info("Modules folder did not exist, created one at " + directory.getPath());
      } else {
        Bukkit.getLogger().info("Failed to create modules folder at " + directory.getPath());
      }
    }
  }

  /**
   * Loads the modules contained within the specified directory
   */
  public void loadModules() {
    Validate.notNull(directory, "Directory cannot be null");
    Validate.isTrue(directory.isDirectory(), "Directory must be a directory");

    List<ExternalModuleInfo> result = new ArrayList<ExternalModuleInfo>();

    Map<String, File> modules = new HashMap<String, File>();
    Set<String> loadedModules = new HashSet<String>();
    Map<String, Collection<String>> dependencies = new HashMap<String, Collection<String>>();

    // This is where it figures out all possible modules
    for (File file : directory.listFiles()) {
      if (file.getName().startsWith(".")) {
        continue;
      }
      JarFile jar;
      try {
        jar = new JarFile(file, true);
      } catch (IOException ex) {
        Bukkit.getLogger().severe(
            "Could not load module at " + file.getPath() + ": " + ExceptionUtils.getMessage(ex));
        ex.printStackTrace();
        continue;
      }

      ZipEntry entry = jar.getEntry("module-set.yml");
      SetDescriptionFile descriptionFile;
      try {
        InputStream stream = jar.getInputStream(entry);
        descriptionFile = new SetDescriptionFile(stream);
      } catch (Exception ex) {
        Bukkit.getLogger().severe(
            "Could not load module at " + file.getPath() + ": " + ExceptionUtils.getMessage(ex));
        ex.printStackTrace();
        continue;
      }

      File replacedFile = modules.put(descriptionFile.getName(), file);
      if (replacedFile != null) {
        Bukkit.getLogger().severe(String.format(
            "Ambiguous module name `%s' for files `%s' and `%s' in `%s'",
            descriptionFile.getName(),
            file.getPath(),
            replacedFile.getPath(),
            directory.getPath()
        ));
        continue;
      }

      Collection<String> dependencySet = descriptionFile.getDepend();
      if (dependencySet != null && !dependencySet.isEmpty()) {
        dependencies.put(descriptionFile.getName(), new LinkedList<String>(dependencySet));
      }
    }

    while (!modules.isEmpty()) {
      boolean missingDependency = true;
      Iterator<String> moduleIterator = modules.keySet().iterator();

      while (moduleIterator.hasNext()) {
        String module = moduleIterator.next();

        if (dependencies.containsKey(module)) {
          Iterator<String> dependencyIterator = dependencies.get(module).iterator();

          while (dependencyIterator.hasNext()) {
            String dependency = dependencyIterator.next();

            // Dependency loaded
            if (loadedModules.contains(dependency)) {
              dependencyIterator.remove();

              // We have a dependency not found
            } else if (!modules.containsKey(dependency)) {
              missingDependency = false;
              File file = modules.get(module);
              moduleIterator.remove();
              dependencies.remove(module);

              Bukkit.getLogger().log(
                  Level.SEVERE,
                  "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "'",
                  new UnknownDependencyException(dependency));
              break;
            }
          }

          if (dependencies.containsKey(module) && dependencies.get(module).isEmpty()) {
            dependencies.remove(module);
          }
        }
        if (!dependencies.containsKey(module) && modules.containsKey(module)) {
          // We're clear to load, no more soft or hard dependencies left
          File file = modules.get(module);
          moduleIterator.remove();
          missingDependency = false;

          try {
            result.add(loadModule(file));
            loadedModules.add(module);
            continue;
          } catch (Exception ex) {
            Bukkit.getLogger().log(Level.SEVERE,
                "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "'",
                ex);
          }
        }
      }

      if (missingDependency) {
        // We now iterate over modules until something loads
        // This loop will ignore soft dependencies
        moduleIterator = modules.keySet().iterator();

        while (moduleIterator.hasNext()) {
          String module = moduleIterator.next();

          if (!dependencies.containsKey(module)) {
            missingDependency = false;
            File file = modules.get(module);
            moduleIterator.remove();

            try {
              result.add(loadModule(file));
              loadedModules.add(module);
              break;
            } catch (Exception ex) {
              Bukkit.getLogger().log(Level.SEVERE,
                  "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "'",
                  ex);
            }
          }
        }
        // We have no modules left without a depend
        if (missingDependency) {
          dependencies.clear();
          Iterator<File> failedModuleIterator = modules.values().iterator();

          while (failedModuleIterator.hasNext()) {
            File file = failedModuleIterator.next();
            failedModuleIterator.remove();
            Bukkit.getLogger().log(Level.SEVERE,
                "Could not load '" + file.getPath() + "' in folder '" + directory.getPath()
                    + "': circular dependency detected");
          }
        }
      }
    }
  }

  /**
   * Loads the module in the specified file
   *
   * @param file File containing the module to load
   * @return The Module loaded, or null if it was invalid
   * @throws Exception Thrown when the specified file is not a valid module
   */
  public synchronized ExternalModuleInfo loadModule(File file) throws Exception {
    Validate.notNull(file, "File cannot be null");

    ExternalModuleInfo result = load(file);

    if (result != null) {
      loadedModules.add(result);
    }

    return result;
  }

  private ExternalModuleInfo load(File file) {
    JarFile jar;
    try {
      jar = new JarFile(file, true);
    } catch (IOException ex) {
      Bukkit.getLogger().severe(
          "Could not load module at " + file.getPath() + ": " + ExceptionUtils.getMessage(ex));
      return null;
    }

    ZipEntry entry = jar.getEntry("module-set.yml");
    SetDescriptionFile descriptionFile;
    try {
      InputStream stream = jar.getInputStream(entry);

      descriptionFile = new SetDescriptionFile(stream);
    } catch (Exception ex) {
      Bukkit.getLogger().severe(
          "Could not load module at " + file.getPath() + ": " + ExceptionUtils.getMessage(ex));
      return null;
    }

    try {
      SetClassLoader.addFile(file);
    } catch (IOException ex) {
      Bukkit.getLogger().severe(
          "Invalid module-set.yml for " + file.getPath() + ": Could not load " + file.getName()
              + " into the classpath");
      return null;
    }

    Class<?> externalModule;
    try {
      externalModule = Class.forName(descriptionFile.getMain());
    } catch (ClassNotFoundException ex) {
      Bukkit.getLogger().severe(
          "Invalid module-set.yml for " + file.getPath() + ": " + descriptionFile.getMain()
              + " does not exist");
      return null;
    }

    if (!ModuleSet.class.isAssignableFrom(externalModule)) {
      Bukkit.getLogger().severe(
          "Invalid module-set.yml for " + file.getPath() + ": " + descriptionFile.getMain()
              + " is not assignable from " + ModuleSet.class.getSimpleName());
      return null;
    }

    try {
      ModuleSet externalModuleInstance = (ModuleSet) externalModule.newInstance();
      return new ExternalModuleInfo(externalModuleInstance, descriptionFile);
    } catch (Exception ex) {
      Bukkit.getLogger()
          .severe("Failed to load " + file.getPath() + ": " + ExceptionUtils.getMessage(ex));
    }

    return null;
  }

  public void disableAll() {
    for (ExternalModuleInfo module : loadedModules) {
      module.getModuleInstance().onDisable();
    }
  }

  public boolean hasModule(String id) {
    return this.loadedModules.stream()
        .anyMatch(i -> i.getDescriptionFile().getName().toLowerCase().replace(" ", "-").equals(id));
  }

  @Data
  public class ExternalModuleInfo {

    private final ModuleSet moduleInstance;
    private final SetDescriptionFile descriptionFile;
  }
}
