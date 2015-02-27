/*
 * This file is part of WattDepot.
 *
 *  Copyright (C) 2015  Cam Moore
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.wattdepot.server.depository.impl.hibernate;

import org.junit.Test;

import static org.junit.Assert.*;

public class SensorImplTest {

  /**
   * Test getting and setting the pk.
   * @throws Exception
   */
  @Test
  public void testPk() throws Exception {
    SensorImpl impl = new SensorImpl();
    Long l = impl.getPk();
    assertNull(l);
  }

  @Test
  public void testId() throws Exception {
    SensorImpl impl = new SensorImpl();
    assertNull(impl.getId());
    impl.setId("id1");
    assertNotNull(impl.getId());
    assertTrue("id1".equals(impl.getId()));
  }

  @Test
  public void testName() throws Exception {
    SensorImpl impl = new SensorImpl();
    assertNull(impl.getName());
    impl.setName("name1");
    assertNotNull(impl.getName());
    assertTrue("name1".equals(impl.getName()));
  }

  @Test
  public void testUri() throws Exception {
    SensorImpl impl = new SensorImpl();
    assertNull(impl.getUri());
    impl.setUri("uri1");
    assertNotNull(impl.getUri());
    assertTrue("uri1".equals(impl.getUri()));
  }

  @Test
  public void testModel() throws Exception {

  }

  @Test
  public void testHashCode() throws Exception {

  }

  @Test
  public void testEquals() throws Exception {

  }

  @Test
  public void testToSensor() throws Exception {

  }

  @Test
  public void testToString() throws Exception {
    SensorImpl impl = new SensorImpl();
    impl.setId("id1");
    impl.setName("name1");
    impl.setUri("uri1");
    assertNotNull(impl.toString());
  }
}