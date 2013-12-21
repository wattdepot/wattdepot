/**
 * HTTPClientHelperFilter.java created on Dec 21, 2013 by Cam Moore.
 */
package org.wattdepot.common.util.logger;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

/**
 * HTTPClientHelperFilter -
 * 
 * @author Cam Moore
 * 
 */
public class HTTPClientHelperFilter implements Filter {

  /*
   * (non-Javadoc)
   * 
   * @see java.util.logging.Filter#isLoggable(java.util.logging.LogRecord)
   */
  @Override
  public boolean isLoggable(LogRecord rec) {
    String message = rec.getMessage();
    if (message.endsWith("HTTP client")) {
      return false;
    }
    return true;
  }

}
