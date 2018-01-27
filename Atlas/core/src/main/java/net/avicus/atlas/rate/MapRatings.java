package net.avicus.atlas.rate;

import java.util.Collection;
import net.avicus.compendium.locale.text.UnlocalizedComponent;
import net.avicus.compendium.locale.text.UnlocalizedText;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;

public class MapRatings {

  public static final String SEPARATOR = "\0";
  public static final int MIN_RATING = 1;
  public static final int MAX_RATING = 5;
  public static final int[] ALL_RATINGS = new int[]{
      MIN_RATING,
      2,
      3,
      4,
      MAX_RATING
  };
  public static final ChatColor[] COLORS = new ChatColor[]{
      ChatColor.RED,
      ChatColor.GOLD,
      ChatColor.YELLOW,
      ChatColor.GREEN,
      ChatColor.DARK_GREEN
  };
  public static final int CREDIT_REWARD = 4;

  public static double average(final Collection<Integer> values) {
    return values.stream().mapToInt(Integer::valueOf).average().orElse(0.0d);
  }

  public static UnlocalizedComponent createUnlocalizedBold(BaseComponent component) {
    final UnlocalizedComponent uc = new UnlocalizedComponent(component);
    uc.style().bold();
    return uc;
  }

  public static UnlocalizedText createUnlocalizedBold(String text) {
    final UnlocalizedText component = new UnlocalizedText(text);
    component.style().bold();
    return component;
  }

  public static String serialize(String slug, String name, String version) {
    return slug + SEPARATOR + name + SEPARATOR + version;
  }
}
