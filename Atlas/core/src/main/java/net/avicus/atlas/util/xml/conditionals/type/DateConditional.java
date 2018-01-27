package net.avicus.atlas.util.xml.conditionals.type;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import net.avicus.atlas.module.checks.CheckResult;
import net.avicus.atlas.module.checks.StaticResultCheck;
import net.avicus.atlas.util.xml.conditionals.Conditional;
import org.jdom2.Element;

public class DateConditional extends Conditional {

  private LocalDate localDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
  private int month = localDate.getMonthValue();

  public DateConditional(String def, String value, List<Element> elements) {
    super(new StaticResultCheck(CheckResult.IGNORE), elements);
    super.setCheck(new StaticResultCheck(getResult(def, value)));
  }

  private CheckResult getResult(String def, String query) {
    switch (def) {
      case "season":
        return determineSeason(query);
      case "holiday":
        return determineHoliday(query);
      case "month":
        return determineMonth(query);
    }

    return CheckResult.DENY;
  }

  private CheckResult determineMonth(String month) {
    return CheckResult
        .valueOf(Month.of(this.month).name().toLowerCase().startsWith(month.toLowerCase()));
  }

  private CheckResult determineSeason(String season) {
    switch (season) {
      case "winter":
        return CheckResult.valueOf((month == 12) || (month == 1) || (month == 2) || (month == 3));
      case "spring":
        return CheckResult.valueOf((month == 4) || (month == 5));
      case "summer":
        return CheckResult.valueOf((month == 6) || (month == 7) || (month == 8) || (month == 9));
      case "fall":
        return CheckResult.valueOf((month == 10) || (month == 11));
    }

    return CheckResult.DENY;
  }

  private CheckResult determineHoliday(String holiday) {
    int day = localDate.getDayOfMonth();
    switch (holiday) {
      case "christmas":
        return CheckResult.valueOf(month == 12 && day > 15 && day < 26);
      case "independence-day":
        return CheckResult.valueOf(month == 7 && day == 4);
      case "april-fools":
        return CheckResult.valueOf(month == 4 && day == 1);
      case "new-years":
        return CheckResult.valueOf((month == 1 && day == 1) || (month == 12
            && localDate.lengthOfMonth() - localDate.getDayOfMonth() == 0));
    }

    return CheckResult.DENY;
  }
}
