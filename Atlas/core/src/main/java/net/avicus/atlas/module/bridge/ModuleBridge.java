package net.avicus.atlas.module.bridge;

import net.avicus.atlas.module.Module;

public abstract class ModuleBridge<T extends Module> {

  public abstract void onInitialize(T module);

  public abstract void onOpen(T module);

  public abstract void onClose(T module);
}
