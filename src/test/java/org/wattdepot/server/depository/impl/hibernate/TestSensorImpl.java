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

import java.util.HashSet;

import static org.junit.Assert.*;

public class TestSensorImpl {

  @Test
  public void testEquals() throws Exception {
    SensorImpl impl1 = InstanceFactory.getSensor();
    assertTrue(impl1.equals(impl1));
    assertFalse(impl1.equals(null));
    assertFalse(impl1.equals(new Object()));
    assertFalse(impl1.equals("foo"));
    SensorImpl impl2 = new SensorImpl();
    assertFalse(impl2.hashCode() == impl1.hashCode());
    assertFalse(impl2.equals(impl1));
    impl2.setId("foo");
    assertFalse(impl2.hashCode() == impl1.hashCode());
    assertFalse(impl2.equals(impl1));
    impl2.setId(impl1.getId());
    assertFalse(impl2.hashCode() == impl1.hashCode());
    assertFalse(impl2.equals(impl1));
    impl2.setModel(new SensorModelImpl());
    assertFalse(impl2.hashCode() == impl1.hashCode());
    assertFalse(impl2.equals(impl1));
    impl2.setModel(impl1.getModel());
    assertFalse(impl2.hashCode() == impl1.hashCode());
    assertFalse(impl2.equals(impl1));
    impl2.setName("foo");
    assertFalse(impl2.hashCode() == impl1.hashCode());
    assertFalse(impl2.equals(impl1));
    impl2.setName(impl1.getName());
    assertFalse(impl2.hashCode() == impl1.hashCode());
    assertFalse(impl2.equals(impl1));
    impl2.setOrg(new OrganizationImpl());
    assertFalse(impl2.hashCode() == impl1.hashCode());
    assertFalse(impl2.equals(impl1));
    impl2.setOrg(impl1.getOrg());
    assertFalse(impl2.hashCode() == impl1.hashCode());
    assertFalse(impl2.equals(impl1));
    impl2.setProperties(new HashSet<PropertyImpl>());
    assertFalse(impl2.hashCode() == impl1.hashCode());
    assertFalse(impl2.equals(impl1));
    impl2.setProperties(impl1.getProperties());
    assertFalse(impl2.hashCode() == impl1.hashCode());
    assertFalse(impl2.equals(impl1));
    impl2.setUri("foo");
    assertFalse(impl2.hashCode() == impl1.hashCode());
    assertFalse(impl2.equals(impl1));
    impl2.setUri(impl1.getUri());
    assertTrue(impl2.hashCode() == impl1.hashCode());
    assertTrue(impl2.equals(impl1));
  }
}