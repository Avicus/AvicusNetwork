package net.avicus.hook;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class CustomFormatter extends Formatter {

  private static final String format = "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL [%4$s] [%3$s] %5$s %6$s%n";
  private final Date dat = new Date();

  /**
   * Formats a log message (taken from SimpleFormatter, but better formatting).
   */
  @Override
  public String format(LogRecord record) {
    dat.setTime(record.getMillis());
    String source;
    if (record.getSourceClassName() != null) {
      source = record.getSourceClassName();
      if (record.getSourceMethodName() != null) {
        source += " " + record.getSourceMethodName();
      }
    } else {
      source = record.getLoggerName();
    }
    String message = formatMessage(record);
    String throwable = "";
    if (record.getThrown() != null) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      pw.println();
      record.getThrown().printStackTrace(pw);
      pw.close();
      throwable = sw.toString();
    }
    return String.format(format,
        dat,
        source,
        record.getLoggerName(),
        record.getLevel().getLocalizedName(),
        message,
        throwable);
  }
}
