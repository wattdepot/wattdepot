/**
 * TestWattDepotImpl.java This file is part of WattDepot 3.
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
package org.wattdepot.server.depository.impl.hibernate;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wattdepot.server.ServerProperties;
import org.wattdepot.server.depository.impl.hibernate.WattDepotImpl;

/**
 * TestWattDepotImpl - Test cases for the WattDepotImpl.
 * 
 * @author Cam Moore
 * 
 */
public class TestWattDepotImpl {

  private WattDepotImpl impl;

  /**
   * @throws java.lang.Exception
   *           if there is a problem.
   */
  @Before
  public void setUp() throws Exception {
    impl = new WattDepotImpl(new ServerProperties());
  }

  /**
   * @throws java.lang.Exception
   *           if there is a problem.
   */
  @After
  public void tearDown() throws Exception {
  }

  /**
   * Test method for
   * {@link org.wattdepot.server.depository.impl.hibernate.WattDepotImpl#getUserGroups()}
   * .
   */
  @Test
  public void testGetUserGroups() {
//    List<UserGroup> groups = impl.getUserGroups();
//    assertNotNull(groups);
//    assertTrue(1 == groups.size());
//    UserGroup one = groups.get(0);
//    assertTrue(UserGroup.ADMIN_GROUP.equals(one));
  }

  /**
   * Test method for
   * {@link org.wattdepot.server.depository.impl.hibernate.WattDepotImpl#getUsers()}
   * .
   */
  @Test
  public void testGetUsers() {
//    List<UserInfo> users = impl.getUsers();
//    assertNotNull(users);
//    assertTrue(1 == users.size());
//    UserInfo one = users.get(0);
//    assertTrue(UserInfo.ADMIN.equals(one));
  }

}
