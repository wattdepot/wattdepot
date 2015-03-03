/*
 * This file is part of WattDepot.
 *
 *  Copyright (C) 2014  Cam Moore
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
import org.wattdepot.common.domainmodel.Property;

import static org.junit.Assert.*;

/**
 * Unit tests for the PropertyImpl class.
 */
public class PropertyImplTest {

  /**
   * Tests the .equals() method.
   * @throws Exception
   */
  @Test
  public void testEquals() throws Exception {
    PropertyImpl one = new PropertyImpl();
    assertFalse(one.equals(null));
    assertTrue(one.equals(one));
    one.setKey("key1");
    one.setValue("value1");
    assertTrue(one.equals(one));
    PropertyImpl two = new PropertyImpl();
    assertFalse(two.equals(one));
    two.setKey("key2");
    two.setValue("value2");
    assertFalse(two.equals(one));
    assertFalse(two.equals("hi"));
    two.setKey("key1");
    assertFalse(one.equals(two));
    two.setValue("value1");
    assertTrue(one.equals(two));
    one.setValue(null);
    assertFalse(one.equals(two));
  }

  @Test
  public void testKey() throws Exception {
    PropertyImpl one = new PropertyImpl();
    assertFalse("key".equals(one.getKey()));
    one.setKey("key");
    assertTrue("key".equals(one.getKey()));
  }

  @Test
  public void testPk() throws Exception {
    PropertyImpl one = new PropertyImpl();
    Long l = one.getPk();
    assertNull(l);
  }

  @Test
  public void testValue() throws Exception {
    PropertyImpl one = new PropertyImpl();
    assertFalse("value".equals(one.getValue()));
    one.setValue("value");
    assertTrue("value".equals(one.getValue()));
  }

  @Test
  public void testHashCode() throws Exception {
    PropertyImpl one = new PropertyImpl();
    PropertyImpl two = new PropertyImpl();
    assertTrue(one.hashCode() == two.hashCode());
    one.setKey("key");
    one.setValue("value");
    two.setKey("key2");
    two.setValue("value2");
    assertFalse(one.hashCode() == two.hashCode());
    two.setKey("key");
    two.setValue("value");
    assertTrue(one.hashCode() == two.hashCode());
  }

  @Test
  public void testToProperty() throws Exception {
    Property p = new Property();
    p.setKey("key");
    p.setValue("value");
    PropertyImpl one = new PropertyImpl();
    one.setKey("key");
    one.setValue("value");
    assertTrue(p.equals(one.toProperty()));
  }

  @Test
  public void testToString() throws Exception {
    PropertyImpl one = new PropertyImpl();
    one.setKey("key");
    one.setValue("value");
    assertNotNull(one.toString());
  }
}