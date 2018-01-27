package net.avicus.atlas.module.checks;

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.avicus.atlas.module.checks.variable.AttackerVariable;
import net.avicus.atlas.module.checks.variable.DamageVariable;
import net.avicus.atlas.module.checks.variable.EntityVariable;
import net.avicus.atlas.module.checks.variable.GroupVariable;
import net.avicus.atlas.module.checks.variable.ItemVariable;
import net.avicus.atlas.module.checks.variable.LocationVariable;
import net.avicus.atlas.module.checks.variable.MaterialVariable;
import net.avicus.atlas.module.checks.variable.PlayerVariable;
import net.avicus.atlas.module.checks.variable.SpawnReasonVariable;
import net.avicus.atlas.module.checks.variable.VictimVariable;
import net.avicus.compendium.StringUtil;
import net.avicus.compendium.locale.text.Localizable;
import net.avicus.compendium.locale.text.LocalizedNumber;
import net.avicus.compendium.locale.text.UnlocalizedFormat;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class VariableUtils {

  public static BaseComponent replaceString(Localizable original, Locale locale,
      CheckContext context) {
    String translated = original.translate(locale).toLegacyText();
    for (Map.Entry<String, Localizable> entry : createContextAttributes("", context).entrySet()) {
      translated = translated
          .replace("[" + entry.getKey() + "]", entry.getValue().translate(locale).toLegacyText());
    }
    return new TextComponent(translated);
  }

  private static HashMap<String, Localizable> createContextAttributes(String prefix,
      CheckContext context) {
    HashMap<String, Localizable> result = Maps.newHashMap();
    context.getFirst(DamageVariable.class).ifPresent(v -> {
      result.put(prefix + "damage",
          new UnlocalizedText(v.getCause().name().toLowerCase().replace("_", " ")));
    });
    context.getFirst(EntityVariable.class).ifPresent(v -> {
      result.put(prefix + "entity-name", new UnlocalizedText(v.getEntity().getName()));
      result.putAll(createVectorAttributes(prefix + "entity-location-",
          v.getEntity().getLocation().toVector()));
      result
          .putAll(createVectorAttributes(prefix + "entity-velocity-", v.getEntity().getVelocity()));
      result.put(prefix + "entity-type",
          new UnlocalizedText(v.getEntity().getType().name().toLowerCase().replace("_", " ")));
    });
    context.getFirst(GroupVariable.class).ifPresent(v -> {
      result.put(prefix + "group-name", v.getGroup().getName().toText());
      result
          .put(prefix + "group-color", new UnlocalizedText(v.getGroup().getChatColor().toString()));
      result.put(prefix + "group-min", new LocalizedNumber(v.getGroup().getMinPlayers()));
      result.put(prefix + "group-max", new LocalizedNumber(v.getGroup().getMaxPlayers()));
      result.put(prefix + "group-overfill", new LocalizedNumber(v.getGroup().getMaxOverfill()));
      result.put(prefix + "group-size", new LocalizedNumber(v.getGroup().size()));
    });
    context.getFirst(ItemVariable.class).ifPresent(v -> {
      ItemStack stack = v.getItemStack().getItemStack();

      if (stack == null || stack.getType() == Material.AIR) {
        return;
      }

      result.putAll(createItemAttributes(prefix + "item-", stack));
    });
    context.getFirst(LocationVariable.class).ifPresent(v -> {
      result.putAll(createVectorAttributes(prefix + "location", v.getLocation().toVector()));
    });
    context.getFirst(MaterialVariable.class).ifPresent(v -> {
      result.put(prefix + "material",
          new UnlocalizedText(v.getData().getItemType().name().toLowerCase().replace("_", " ")));
    });
    context.getFirst(PlayerVariable.class).ifPresent(v -> {
      result.put(prefix + "player-name", new UnlocalizedText(v.getPlayer().getName()));
      result.putAll(createVectorAttributes(prefix + "player-location-",
          v.getPlayer().getLocation().toVector()));
      result
          .putAll(createVectorAttributes(prefix + "player-velocity-", v.getPlayer().getVelocity()));
      if (v.getPlayer().getItemInHand() != null
          && v.getPlayer().getItemInHand().getType() != Material.AIR) {
        result.putAll(
            createItemAttributes(prefix + "player-holding-", v.getPlayer().getItemInHand()));
      }
      result.put(prefix + "player-health", new LocalizedNumber(v.getPlayer().getHealth()));
      result.put(prefix + "player-max-health", new LocalizedNumber(v.getPlayer().getMaxHealth()));
      result.put(prefix + "player-food", new LocalizedNumber(v.getPlayer().getFoodLevel()));
      result.put(prefix + "player-saturation", new LocalizedNumber(v.getPlayer().getSaturation()));
      result.put(prefix + "player-exp", new LocalizedNumber(v.getPlayer().getExp()));
      result.put(prefix + "player-walk-speed", new LocalizedNumber(v.getPlayer().getWalkSpeed()));
      result.put(prefix + "player-fly-speed", new LocalizedNumber(v.getPlayer().getFlySpeed()));
    });
    context.getFirst(SpawnReasonVariable.class).ifPresent(v -> {
      result.put(prefix + "spawn-reason",
          new UnlocalizedText(v.getReason().name().toLowerCase().replace("_", " ")));
    });
    context.getFirst(AttackerVariable.class)
        .ifPresent(v -> result.putAll(createContextAttributes("attacker-", v)));
    context.getFirst(VictimVariable.class)
        .ifPresent(v -> result.putAll(createContextAttributes("victim-", v)));

    return result;
  }

  private static HashMap<String, Localizable> createItemAttributes(String prefix, ItemStack stack) {
    HashMap<String, Localizable> result = Maps.newHashMap();

    result.put(prefix + "type",
        new UnlocalizedText(stack.getType().name().toLowerCase().replace("_", "")));
    result.put(prefix + "amount", new LocalizedNumber(stack.getAmount()));
    result.put(prefix + "damage", new LocalizedNumber(stack.getDurability()));
    ItemMeta meta = stack.getItemMeta();
    if (meta != null) {
      if (meta.hasDisplayName()) {
        result.put(prefix + "name", new UnlocalizedText(meta.getDisplayName()));
      }
      if (meta.hasLore()) {
        result.put(prefix + "lore",
            new UnlocalizedText(StringUtil.listToEnglishCompound(meta.getLore())));
      }
    }

    return result;
  }

  private static HashMap<String, Localizable> createVectorAttributes(String prefix, Vector vector) {
    HashMap<String, Localizable> result = Maps.newHashMap();

    result.put(prefix + "X", new LocalizedNumber(vector.getBlockX(), 0, 0));
    result.put(prefix + "Y", new LocalizedNumber(vector.getBlockY(), 0, 0));
    result.put(prefix + "Z", new LocalizedNumber(vector.getBlockZ(), 0, 0));
    result.put(prefix + "X-precise", new LocalizedNumber(vector.getX()));
    result.put(prefix + "Y-precise", new LocalizedNumber(vector.getY()));
    result.put(prefix + "Z-precise", new LocalizedNumber(vector.getZ()));
    UnlocalizedFormat fullFormat = new UnlocalizedFormat("{0}, {1}, {2}");
    result.put(prefix + "full", fullFormat.with(new LocalizedNumber(vector.getBlockX(), 0, 0),
        new LocalizedNumber(vector.getBlockY(), 0, 0),
        new LocalizedNumber(vector.getBlockZ(), 0, 0)));
    result.put(prefix + "full-precise", fullFormat
        .with(new LocalizedNumber(vector.getBlockX()), new LocalizedNumber(vector.getBlockY()),
            new LocalizedNumber(vector.getBlockZ())));

    return result;
  }
}
