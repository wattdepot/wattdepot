/**
 * WattDepotTestHelper.java This file is part of WattDepot.
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
package org.wattdepot.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.wattdepot.server.WattDepotServer;

/**
 * WattDepotTestHelper - Provides helpful utility methods to WattDepot server
 * resource test classes, which will normally want to extend this class.
 * 
 * @author Cam Moore
 * 
 */
public class WattDepotTestHelper {

  /** The WattDepotServer used in these tests. */
  protected static WattDepotServer server = null;

  /**
   * Starts the server going for these tests.
   * 
   * @throws Exception
   *           If problems occur setting up the server.
   */
  @BeforeClass
  public static void setupServer() throws Exception {
    WattDepotTestHelper.server = WattDepotServer.newTestInstance();
  }

  /**
   * Tears down the server resources.
   * 
   * @throws Exception if there is a problem
   */
  @AfterClass
  public static void stopServer() throws Exception {
    WattDepotTestHelper.server.stop();
  }
}
