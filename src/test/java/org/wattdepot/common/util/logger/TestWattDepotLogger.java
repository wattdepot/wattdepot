package org.wattdepot.common.util.logger;

import static org.junit.Assert.assertEquals;

import java.util.logging.Logger;

import org.junit.Test;
import org.wattdepot.common.util.UserHome;
import org.wattdepot.common.util.logger.WattDepotLogger;

/**
 * Tests the WattDepotLogger class. Portions of this code are adapted from
 * http://hackystat-utilities.googlecode.com/
 * 
 * @author Philip Johnson
 * @author Robert Brewer
 */

public class TestWattDepotLogger {

  /**
   * Tests the logger. Instantiates the logger and writes a test message.
   * 
   */
  @Test
  public void testLogging() {
    Logger logger =
        WattDepotLogger.getLogger("org.wattdepot.nestedlogger.test", UserHome
            .getHomeString()
            + "/.wattdepot/" + "testlogging");
    logger.fine("(Test message)");
    WattDepotLogger.setLoggingLevel(logger, "FINE");
    logger.fine("(Test message2)");
    assertEquals("Checking identity", "org.wattdepot.nestedlogger.test", logger.getName());
  }
}
