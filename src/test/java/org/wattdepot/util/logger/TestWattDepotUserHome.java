package org.wattdepot.util.logger;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;
import org.wattdepot.util.UserHome;

import static org.wattdepot.util.UserHome.USER_HOME_PROPERTY;

/**
 * Tests the WattDepotUserHome class. Portions of this code are adapted from
 * http://hackystat-utilities.googlecode.com/
 * 
 * @author Philip Johnson
 * @author Robert Brewer
 */

public class TestWattDepotUserHome {

  /**
   * Tests the WattDepot user home definition facility.
   * 
   */
  @Test
  public void testHome() {
    String wattdepotUserHome = System.getProperty(USER_HOME_PROPERTY);
    File home = UserHome.getHome();

    if (wattdepotUserHome == null) {
      File userHomeFile = new File(System.getProperty("user.home"));
      assertEquals("Checking default home", userHomeFile, home);
    }
    else {
      File userHomeFile = new File(System.getProperty(USER_HOME_PROPERTY));
      assertEquals("Checking overridden home", userHomeFile, home);
    }
  }
}
