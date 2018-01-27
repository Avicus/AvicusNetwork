package net.avicus.hook.utils;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class Commands {

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
}
