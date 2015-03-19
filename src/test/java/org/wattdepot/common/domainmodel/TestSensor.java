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

package org.wattdepot.common.domainmodel;

import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.*;

public class TestSensor {

  @Test
  public void testEquals() {
    Sensor s1 = InstanceFactory.getSensor();
    assertTrue(s1.equals(s1));
    assertFalse(s1.equals(null));
    assertFalse(s1.equals("foo"));
    assertFalse(s1.equals(new Object()));
    Sensor s2 = new Sensor();
    assertFalse(s2.hashCode() == s1.hashCode());
    assertFalse(s2.equals(s1));
    s2.setId("foo");
    assertFalse(s2.hashCode() == s1.hashCode());
    assertFalse(s2.equals(s1));
    s2.setId(s1.getId());
    assertFalse(s2.hashCode() == s1.hashCode());
    assertFalse(s2.equals(s1));
    s2.setName("foo");
    assertFalse(s2.hashCode() == s1.hashCode());
    assertFalse(s2.equals(s1));
    s2.setName(s1.getName());
    assertFalse(s2.hashCode() == s1.hashCode());
    assertFalse(s2.equals(s1));
    s2.setModelId("foo");
    assertFalse(s2.hashCode() == s1.hashCode());
    assertFalse(s2.equals(s1));
    s2.setModelId(s1.getModelId());
    assertFalse(s2.hashCode() == s1.hashCode());
    assertFalse(s2.equals(s1));
    s2.setOrganizationId("foo");
    assertFalse(s2.hashCode() == s1.hashCode());
    assertFalse(s2.equals(s1));
    s2.setOrganizationId(s1.getOrganizationId());
    assertFalse(s2.hashCode() == s1.hashCode());
    assertFalse(s2.equals(s1));
    s2.setProperties(new HashSet<Property>());
    assertFalse(s2.hashCode() == s1.hashCode());
    assertFalse(s2.equals(s1));
    Property p = new Property("key", "value");
    s2.addProperty(p);
    assertNotNull(s2.getProperty("key"));
    s2.removeProperty(p);
    assertNull(s2.getProperty("key"));
    s2.setProperties(s1.getProperties());
    assertFalse(s2.hashCode() == s1.hashCode());
    assertFalse(s2.equals(s1));
    s2.setUri("foo");
    assertFalse(s2.hashCode() == s1.hashCode());
    assertFalse(s2.equals(s1));
    s2.setUri(s1.getUri());
    assertTrue(s2.hashCode() == s1.hashCode());
    assertTrue(s2.equals(s1));
  }

}