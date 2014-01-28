/**
 * TestWattDepotAdminClient.java This file is part of WattDepot.
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
package org.wattdepot.client.http.api;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.wattdepot.client.ClientProperties;
import org.wattdepot.common.domainmodel.Organization;
import org.wattdepot.common.domainmodel.OrganizationList;
import org.wattdepot.common.domainmodel.Property;
import org.wattdepot.common.domainmodel.UserInfo;
import org.wattdepot.common.domainmodel.UserInfoList;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.util.logger.WattDepotLogger;
import org.wattdepot.server.WattDepotServer;

/**
 * TestWattDepotAdminClient tests for the WattDepotAdminClient.
 * 
 * @author Cam Moore
 * 
 */
public class TestWattDepotAdminClient {

  protected static WattDepotServer server;

  /** The handle on the client. */
  private static WattDepotAdminClient admin;

  /** The logger. */
  private Logger logger = null;
  /** The serverUrl. */
  private String serverURL = null;

  /**
   * Starts up a WattDepotServer to start the testing.
   * 
   * @throws Exception if there is a problem starting the server.
   */
  @BeforeClass
  public static void setupServer() throws Exception {
    server = WattDepotServer.newTestInstance();
  }

  /**
   * Shuts down the WattDepotServer.
   * 
   * @throws Exception if there is a problem.
   */
  @AfterClass
  public static void stopServer() throws Exception {
    if (admin != null) {
      try {
        admin.deleteOrganization("organization-one");
      }
      catch (Exception e) {
        e.printStackTrace();
      }
      try {
        admin.deleteOrganization("organization-two");
      }
      catch (Exception e) {
        e.printStackTrace();
      }
      try {
        admin.deleteOrganization("organization-three");
      }
      catch (Exception e) {
        // e.printStackTrace();
      }
    }
    server.stop();
  }

  /**
   * Setup the logging and create the WattDepotAdminClient admin.
   */
  @Before
  public void setUp() {
    try {
      ClientProperties props = new ClientProperties();
      props.setTestProperties();
      this.logger = WattDepotLogger.getLogger("org.wattdepot.client",
          props.get(ClientProperties.CLIENT_HOME_DIR));
      WattDepotLogger.setLoggingLevel(logger, props.get(ClientProperties.LOGGING_LEVEL_KEY));
      logger.finest("setUp()");
      this.serverURL = "http://" + props.get(ClientProperties.WATTDEPOT_SERVER_HOST) + ":"
          + props.get(ClientProperties.PORT_KEY) + "/";
      logger.finest(serverURL);
      if (admin == null) {
        try {
          admin = new WattDepotAdminClient(serverURL, props.get(ClientProperties.USER_NAME),
              "admin", props.get(ClientProperties.USER_PASSWORD));
        }
        catch (Exception e) {
          System.out.println("Failed with " + props.get(ClientProperties.USER_NAME) + " and "
              + props.get(ClientProperties.USER_PASSWORD));
          e.printStackTrace();
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }

  /**
   * 
   */
  @After
  public void tearDown() {
    logger.finest("tearDown()");
  }

  /**
   * Test method for Organizations.
   */
  @Test
  public void testOrganizations() {
    OrganizationList list = admin.getOrganizations();
    int numOrganizations = list.getOrganizations().size();
    // create two different organizations.
    Organization one = new Organization("Organization one");
    admin.putOrganization(one);
    Organization two = new Organization("Organization two");
    admin.putOrganization(two);
    Organization three = new Organization("Organization three");
    admin.putOrganization(three);
    list = admin.getOrganizations();
    assertNotNull(list);
    assertTrue("Expecting " + (numOrganizations + 3) + " got " + list.getOrganizations().size(),
        (numOrganizations + 3) == list.getOrganizations().size());
    try {
      admin.deleteOrganization(three.getId());
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    list = admin.getOrganizations();
    assertNotNull(list);
    assertTrue(numOrganizations + 2 == list.getOrganizations().size());
    two.setName("New organization name");
    admin.updateOrganization(two);
    try {
      Organization defined = admin.getOrganization(two.getId());
      assertNotNull(defined);
      assertTrue(defined.equals(two));
    }
    catch (IdNotFoundException e) {
      e.printStackTrace();
      fail(e.getMessage() + " should not happen");
    }
    // create two users with same id different organizations.
    UserInfo u1 = new UserInfo("id", "firstName", "lastName", "email", one.getId(),
        new HashSet<Property>(), "secret1");
    admin.putUser(u1);
    UserInfo u2 = new UserInfo("id", "firstName", "lastName", "email", two.getId(),
        new HashSet<Property>(), "secret2");
    admin.putUser(u2);
    UserInfoList ulist = admin.getUsers(one.getId());
    assertNotNull(ulist);
    assertTrue("Expecting 1 got " + ulist.getUsers().size(), ulist.getUsers().size() == 1);
    ulist = admin.getUsers(two.getId());
    assertNotNull(ulist);
    assertTrue(ulist.getUsers().size() == 1);
  }

}
