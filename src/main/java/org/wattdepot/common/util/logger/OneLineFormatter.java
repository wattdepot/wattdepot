package org.wattdepot.common.util.logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Provides a one line formatter for use with WattDepot logging. Supports optional date stamp prefix
 * and optional appending of a newline. Portions of this code are adapted from
 * http://hackystat-utilities.googlecode.com/
 * 
 * @author Philip Johnson
 * @author Robert Brewer
 */
public class OneLineFormatter extends Formatter {

  /** Whether or not to include the date stamp in the format string. */
  private boolean enableDateStamp = true;

  /** Whether or not to add a newline. */
  private boolean enableNewline = true;

  /**
   * Default constructor that enables the date stamp and new line.
   */
  public OneLineFormatter() {
    this(true, true);
  }

  /**
   * One line format string with optional date stamp. Always adds a newline.
   * 
   * @param enableDateStamp If true, a date stamp is inserted.
   */
  public OneLineFormatter(boolean enableDateStamp) {
    this(enableDateStamp, true);
  }

  /**
   * One line format string with optional date stamp and optional newline.
   * 
   * @param enableDateStamp If true, a date stamp is inserted.
   * @param enableNewline If true, a newline is always inserted.
   */
  public OneLineFormatter(boolean enableDateStamp, boolean enableNewline) {
    this.enableDateStamp = enableDateStamp;
    this.enableNewline = enableNewline;
  }

  /**
   * Formats the passed log string as a single line. Prefixes the log string with a date stamp if
   * enabled, and adds a newline if enabled.
   * 
   * @param record A log record.
   * @return The message string.
   */
  @Override
  public String format(LogRecord record) {
    StringBuffer buff = new StringBuffer();
    if (this.enableDateStamp) {
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
      buff.append(dateFormat.format(new Date()));
      buff.append("  ");
    }
    buff.append(record.getMessage());
    if (this.enableNewline) {
      buff.append(System.getProperty("line.separator"));
    }
    return buff.toString();
  }
}
