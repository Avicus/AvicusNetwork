package net.avicus.atlas.module;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import lombok.Getter;

public abstract class BridgeableModule<B extends ModuleBridge> {

  public static final HashMap<Class<? extends Module>, List<Class<? extends ModuleBridge>>> BRIDGES = Maps
      .newHashMap();

  @Getter
  private HashMap<Class<? extends B>, B> bridges = Maps.newHashMap();

  public void buildBridges(Module module) {
    BRIDGES.getOrDefault(module.getClass(), Collections.emptyList()).forEach(b -> {
      try {
        bridges.put((Class<? extends B>) b,
            (B) b.getConstructor(module.getClass()).newInstance(module));
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  public <T extends B> T getBridge(Class<? extends B> clazz) {
    return (T) bridges.get(clazz);
  }
}
