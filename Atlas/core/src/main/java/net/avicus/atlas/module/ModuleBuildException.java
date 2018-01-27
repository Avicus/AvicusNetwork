package net.avicus.atlas.module;

public class ModuleBuildException extends Exception {

  public ModuleBuildException(ModuleFactory factory, Exception cause) {
    super(factory.getClass().getSimpleName() + " failed: " + cause.getMessage(), cause);
  }

  public ModuleBuildException(ModuleFactory factory, String reason) {
    super(factory.getClass().getSimpleName() + " failed: " + reason);
  }
}
