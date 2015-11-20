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

import static org.junit.Assert.assertFalse;
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
import org.wattdepot.common.domainmodel.UserPassword;
import org.wattdepot.common.exception.IdNotFoundException;
import org.wattdepot.common.util.logger.LoggerUtil;
import org.wattdepot.server.StrongAES;
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
  private static Logger logger = null;
  /** The serverUrl. */
  private static String serverURL = null;

  /**
   * Starts up a WattDepotServer to start the testing.
   * 
   * @throws Exception if there is a problem starting the server.
   */
  @BeforeClass
  public static void setupServer() throws Exception {
    LoggerUtil.disableLogging();    
    server = WattDepotServer.newTestInstance();
    try {
      ClientProperties props = new ClientProperties();
      props.setTestProperties();
      logger = Logger.getLogger("org.wattdepot.client");
      LoggerUtil.setLoggingLevel(logger, props.get(ClientProperties.LOGGING_LEVEL_KEY));
      LoggerUtil.useConsoleHandler();
      logger.finest("setUp()");
      serverURL = "http://" + props.get(ClientProperties.WATTDEPOT_SERVER_HOST) + ":"
          + props.get(ClientProperties.PORT_KEY) + "/";
      logger.finest("Using " + serverURL);
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
   * Shuts down the WattDepotServer.
   * 
   * @throws Exception if there is a problem.
   */
  @AfterClass
  public static void stopServer() throws Exception {
    server.stop();
  }

  /**
   * Test method for Organizations.
   */
  @Test
  public void testOrganizations() {
    String orgName = "Organization ";
    Organization one = new Organization(orgName + 1);
    Organization two = new Organization(orgName + 2);
    Organization three = new Organization(orgName + 3);
    try {
      OrganizationList list = admin.getOrganizations();
      int numOrganizations = list.getOrganizations().size();
      admin.putOrganization(one);
      admin.putOrganization(two);
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
        fail(e.getMessage() + " should delete defined organization " + three.getName());
      }
      try {
        admin.deleteOrganization("bogus-org");
        fail("Should not be able to delete a bogus organization.");
      }
      catch (IdNotFoundException e) {
        // expected
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
        fail(e.getMessage() + " should be able to update " + two.getId());
      }

    }
    finally {
      // Clean up
      if (admin.isDefinedOrganization(one.getId())) {
        try {
          admin.deleteOrganization(one.getId());
        }
        catch (IdNotFoundException e) {
          e.printStackTrace();
        }
      }
      if (admin.isDefinedOrganization(two.getId())) {
        try {
          admin.deleteOrganization(two.getId());
        }
        catch (IdNotFoundException e) {
          e.printStackTrace();
        }
      }
      if (admin.isDefinedOrganization(three.getId())) {
        try {
          admin.deleteOrganization(three.getId());
        }
        catch (IdNotFoundException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Test for manipulating Users.
   */
  @Test
  public void testUsers() {
    String orgName = "Group ";
    Organization one = new Organization(orgName + 1);
    admin.putOrganization(one);
    Organization two = new Organization(orgName + 2);
    admin.putOrganization(two);
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
    u1.setEmail("new email");
    admin.updateUser(u1);
    ulist = admin.getUsers(one.getId());
    assertNotNull(ulist);
    assertTrue("Expecting 1 got " + ulist.getUsers().size(), ulist.getUsers().size() == 1);
    assertTrue("didn't change email", ulist.getUsers().get(0).getEmail().equals("new email"));
    // try to delete undefined user
    try {
      admin.deleteUser("bogus-id", u1.getOrganizationId());
      fail("Should not be able to delete a bogus user.");
    }
    catch (IdNotFoundException e) {
      // this is expected
    }
    assertFalse("isDefinedUser('bogus') should return false.", admin.isDefinedUserInfo("bogus-id", u1.getOrganizationId()));
    // passwords
    try {
      UserPassword pass1 = admin.getUserPassword(u1.getUid(), u1.getOrganizationId());
      assertNotNull(pass1);
      pass1.setEncryptedPassword(StrongAES.getInstance().encrypt("foooo"));
      try {
        admin.updateUserPassword(pass1);
      }
      catch (IdNotFoundException e) {
        e.printStackTrace();
        fail("problem updating password");
      }
    }
    catch (IdNotFoundException e) {
      fail("u1 is defined");
    }

    // Clean up
    if (admin.isDefinedUserInfo(u1.getUid(), u1.getOrganizationId())) {
      try {
        admin.deleteUser(u1.getUid(), u1.getOrganizationId());
      }
      catch (IdNotFoundException e) {
        e.printStackTrace();
      }
    }
    if (admin.isDefinedUserInfo(u2.getUid(), u2.getOrganizationId())) {
      try {
        admin.deleteUser(u2.getUid(), u2.getOrganizationId());
      }
      catch (IdNotFoundException e) {
        e.printStackTrace();
      }
    }
    if (admin.isDefinedOrganization(one.getId())) {
      try {
        admin.deleteOrganization(one.getId());
      }
      catch (IdNotFoundException e) {
        e.printStackTrace();
      }
    }
    if (admin.isDefinedOrganization(two.getId())) {
      try {
        admin.deleteOrganization(two.getId());
      }
      catch (IdNotFoundException e) {
        e.printStackTrace();
      }
    }

  }

}
