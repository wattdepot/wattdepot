/**
 * TestClientProperties.java This file is part of WattDepot.
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
package org.wattdepot.client;



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wattdepot.common.util.UserHome;

/**
 * TestClientProperties - Test cases for the ClientProperties class.
 *
 * @author Cam Moore
 *
 */
public class TestClientProperties {

  /**
   * @throws java.lang.Exception if there is a problem.
   */
  @Before
  public void setUp() throws Exception {
  }

  /**
   * @throws java.lang.Exception if there is a problem.
   */
  @After
  public void tearDown() throws Exception {
  }

  /**
   * Test method for {@link org.wattdepot.client.ClientProperties#ClientProperties()}.
   */
  @Test
  public void testClientProperties() {
    ClientProperties props = new ClientProperties();
    assertNotNull(props);
    props = new ClientProperties("test");
    assertNotNull(props);    
  }

  /**
   * Test method for {@link org.wattdepot.client.ClientProperties#get(java.lang.String)}.
   */
  @Test
  public void testGet() {
    ClientProperties props = new ClientProperties();
    assertNotNull(props);
    String result = props.get(ClientProperties.CLIENT_HOME_DIR);
    assertNotNull(result);
    String expected = UserHome.getHomeString() + "/.wattdepot3/client";
    assertEquals(expected, result);
  }

  /**
   * Test method for {@link org.wattdepot.client.ClientProperties#setTestProperties()}.
   */
  @Test
  public void testSetTestProperties() {
    ClientProperties props = new ClientProperties();
    assertNotNull(props);
    String port = props.get(ClientProperties.PORT_KEY);
    props.setTestProperties();
    String testPort = props.get(ClientProperties.PORT_KEY);
    assertFalse(port.equals(testPort));
    String echo = props.echoProperties();
    assertNotNull(echo);
  }

}
