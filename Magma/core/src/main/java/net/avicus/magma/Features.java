package net.avicus.magma;

import java.time.Month;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * A list of dates when features will be released.
 * This is so many features can be created at once and they can be strategically released over time.
 */
public class Features {

  private static Calendar CAL = Calendar.getInstance(TimeZone.getTimeZone("CST"));

  private static boolean check(Month m, int day, int year) {
    Calendar calendar = new GregorianCalendar(year, m.ordinal(), day);
    calendar.set(Calendar.HOUR_OF_DAY, 12); // Noon
    return CAL.getTime().after(calendar.getTime());
  }

  public static class Gadgets {

    public static boolean newGunsOne() {
      return check(Month.OCTOBER, 25, 2017);
    }

  }
}
