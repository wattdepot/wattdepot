/**
 * TestWattDepotClient.java This file is part of WattDepot.
 *
 * Copyright (C) 2013  Cam Moore
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.wattdepot.systest;

import com.thoughtworks.selenium.DefaultSelenium;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.wattdepot.client.ClientProperties;
import org.wattdepot.common.domainmodel.*;
import org.wattdepot.common.util.logger.LoggerUtil;
import org.wattdepot.server.WattDepotServer;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;


/**
 * TestWattDepotClient - Test cases for the WattDepotClient class.
 *
 * @author Cam Moore
 */
public class TestWattDepotAdminUI {

  private static WattDepotServer server;


  /**
   * The logger.
   */
  private static Logger logger = null;
  /**
   * The serverUrl.
   */
  private static String serverURL = null;

  /**
   * Starts up a WattDepotServer to start the testing.
   *
   * @throws Exception if there is a problem starting the server.
   */
  @BeforeClass
  public static void setupServer() throws Exception {
//    LoggerUtil.disableLogging();
    server = WattDepotServer.newTestInstance();
    // Set up the test instances.
    Set<Property> properties = new HashSet<Property>();
    properties.add(new Property("isAdmin", "no they are not"));

    // Set up the logging and clients.
    try {
      ClientProperties props = new ClientProperties();
      props.setTestProperties();
      logger = Logger.getLogger("org.wattdepot.client");
      LoggerUtil.setLoggingLevel(logger, props.get(ClientProperties.LOGGING_LEVEL_KEY));
      LoggerUtil.useConsoleHandler();
      serverURL = "http://" + props.get(ClientProperties.WATTDEPOT_SERVER_HOST) + ":"
        + props.get(ClientProperties.PORT_KEY) + "/";
      logger.finest("Using server " + serverURL);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Shuts down the WattDepotServer.
   *
   * @throws Exception if there is a problem.
   */
  @AfterClass
  public static void stopServer() throws Exception {
    logger.finest("tearDown()");
    logger.finest("Done tearDown()");
    server.stop();
  }

  protected DefaultSelenium createSeleniumClient(String url) throws Exception {
    return new DefaultSelenium("localhost", 4444, "*firefox", url);
  }

//  /**
//   * Test method for WattDepotClient constructors.
//   */
//  @Test
//  public void testWattDepotOrgAdmin() {
//    try {
//      DefaultSelenium selenium = createSeleniumClient("http://localhost:8194/");
//      selenium.start();
//      selenium.open("http://localhost:8194/wattdepot/admin/");
//      System.out.println(selenium.getTitle());
//      System.out.println(selenium.getBodyText());
//      //assertEquals("Geronimo Console", selenium.getTitle());
////      for (int i = 0; i < 60; i++) {
////        Thread.sleep(1000);
////      }
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//  }
}
