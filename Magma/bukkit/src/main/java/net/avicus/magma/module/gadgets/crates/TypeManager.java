package net.avicus.magma.module.gadgets.crates;

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Optional;
import lombok.Getter;
import net.avicus.compendium.WeightedRandomizer;
import net.avicus.magma.module.gadgets.Gadget;
import org.apache.commons.lang.StringUtils;

public class TypeManager {

  private static final HashMap<String, CrateType> types = Maps.newHashMap();

  public static void registerType(String id, WeightedRandomizer<Gadget> randomizer) {
    types.put(id.toLowerCase(), new CrateType(id, randomizer));
  }

  public static void unRegisterType(String id) {
    types.remove(id.toLowerCase());
  }

  public static boolean hasType(String id) {
    return types.keySet().contains(id.toLowerCase());
  }

  public static Optional<CrateType> getTypeOptional(String id) {
    return Optional.ofNullable(getType(id.toLowerCase()));
  }

  public static CrateType getType(String id) {
    return types.get(id.toLowerCase());
  }

  @Getter
  public static class CrateType {

    private final String id;
    private final String crateName;
    private final String keyName;
    private final WeightedRandomizer<Gadget> randomizer;

    CrateType(String name, WeightedRandomizer<Gadget> randomizer) {
      this.id = name.toLowerCase();
      this.crateName = StringUtils.capitalize(name) + " Crate";
      this.keyName = StringUtils.capitalize(name) + " Key";
      this.randomizer = randomizer;
    }
  }
}
