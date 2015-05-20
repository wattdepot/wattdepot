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
import org.wattdepot.common.exception.BadSlugException;

import java.util.HashSet;

import static org.junit.Assert.*;

public class TestSensorGroup {

  @Test
  public void testEquals() {
    SensorGroup group = InstanceFactory.getSensorGroup();
    assertTrue(group.equals(group));
    assertFalse(group.equals(null));
    assertFalse(group.equals(""));
    SensorGroup group2 = new SensorGroup();
    assertFalse(group2.hashCode() == group.hashCode());
    assertFalse(group2.equals(group));
    try {
      group2.setId("foo");
    }
    catch (BadSlugException e) {
      fail("Should not happen. " + e.getMessage());
    }
    try {
      group2.setId("This is a bogus id #$@!~");
      fail("Should have thrown BadSlugException");
    }
    catch (BadSlugException e) { // NOPMD
      // expected
    }
    assertFalse(group2.hashCode() == group.hashCode());
    assertFalse(group2.equals(group));
    try {
      group2.setId(group.getId());
    }
    catch (BadSlugException e) {
      fail("This should not happen. " + e.getMessage());
    }
    assertFalse(group2.hashCode() == group.hashCode());
    assertFalse(group2.equals(group));
    group2.setName("foo");
    assertFalse(group2.hashCode() == group.hashCode());
    assertFalse(group2.equals(group));
    group2.setName(group.getName());
    assertFalse(group2.hashCode() == group.hashCode());
    assertFalse(group2.equals(group));
    group2.setOrganizationId("foo");
    assertFalse(group2.hashCode() == group.hashCode());
    assertFalse(group2.equals(group));
    group2.setOrganizationId(group.getOrganizationId());
    assertFalse(group2.hashCode() == group.hashCode());
    assertFalse(group2.equals(group));
    group2.setSensors(new HashSet<String>());
    assertFalse(group2.hashCode() == group.hashCode());
    assertFalse(group2.equals(group));
    group2.add("foo");
    assertFalse(group2.hashCode() == group.hashCode());
    assertFalse(group2.equals(group));
    assertTrue(group2.contains("foo"));
    group2.remove("foo");
    assertFalse(group2.hashCode() == group.hashCode());
    assertFalse(group2.equals(group));
    group2.setSensors(group.getSensors());
    assertTrue(group2.hashCode() == group.hashCode());
    assertTrue(group2.equals(group));
  }

}