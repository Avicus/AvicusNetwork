package net.avicus.magma.util;

import java.util.List;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.meta.ItemMeta;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public final class StringUtil {

  private StringUtil() {
  }

  private static final PeriodFormatter periodFormatter = new PeriodFormatterBuilder()
      .appendYears().appendSuffix("y")
      .appendMonths().appendSuffix("mo")
      .appendDays().appendSuffix("d")
      .appendHours().appendSuffix("h")
      .appendMinutes().appendSuffix("m")
      .appendSecondsWithOptionalMillis().appendSuffix("s")
      .appendDays()
      .toFormatter();

  public static Period parsePeriod(String text) {
    return periodFormatter.parsePeriod(text);
  }

  /**
   * Wrap a long {@link ItemMeta} lore line and append it to a list of lore with
   * last colors copied over.
   *
   * @param lore the result list
   * @param wrapLength the length to wrap on
   * @param string the string to wrap
   */
  public static void wrapLoreWithLastColors(final List<String> lore, final int wrapLength,
      final String string) {
    String lastColors = "";
    for (String wrapped : WordUtils.wrap(string, wrapLength).split(SystemUtils.LINE_SEPARATOR)) {
      if (!lastColors.isEmpty()) {
        lastColors = ChatColor.getLastColors(lastColors);
      }

      lore.add(lastColors + wrapped);
      lastColors = wrapped;
    }
  }

  /**
   * Convert a string to superscript.
   * NOTE: This only works for numbers and basic math symbols (and spaces/periods).
   *
   * @param text to format
   */
  public static String superScript(String text) {
    char[] chars = text.toCharArray();
    for (int i = 0; i < chars.length; i++) {
      switch (chars[i]) {
        // Ignored
        case '.':
        case ' ':
          break;
        // Math
        case '+':
          chars[i] = '\u207a';
          break;
        case '-':
          chars[i] = '\u207b';
          break;
        case '=':
          chars[i] = '\u207c';
          break;
        case '(':
          chars[i] = '\u207d';
          break;
        case ')':
          chars[i] = '\u207e';
          break;
        // Numbers
        case '2':
          chars[i] = '\u00b2';
          break;
        case '3':
          chars[i] = '\u00b3';
          break;
        default:
          chars[i] = (char) (chars[i] - '0' + '\u2070');
          break;
      }
    }
    return String.valueOf(chars);
  }

  /**
   * Convert a string to subscript.
   * NOTE: This only works for numbers and basic math symbols (and spaces/periods).
   *
   * @param text to format
   */
  public static String subScript(String text) {
    char[] chars = text.toCharArray();
    for (int i = 0; i < chars.length; i++) {
      switch (chars[i]) {
        // Ignored
        case '.':
        case ' ':
          break;
        // Math
        case '+':
          chars[i] = '\u208a';
          break;
        case '-':
          chars[i] = '\u208b';
          break;
        case '=':
          chars[i] = '\u208c';
          break;
        case '(':
          chars[i] = '\u208d';
          break;
        case ')':
          chars[i] = '\u208e';
          break;
        // Numbers
        default:
          chars[i] = (char) (chars[i] - '0' + '\u2080');
          break;
      }
    }
    return String.valueOf(chars);
  }
}
