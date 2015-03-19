/**
 * TestCollectorProcessDefinition.java This file is part of WattDepot.
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
package org.wattdepot.common.domainmodel;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.wattdepot.common.exception.BadSlugException;

import java.util.HashSet;

/**
 * Tests the CollectorProcessDefinition class.
 * 
 * @author Cam Moore
 * 
 */
public class TestCollectorProcessDefinition {

  /**
   * Test method for
   * {@link org.wattdepot.common.domainmodel.CollectorProcessDefinition#CollectorProcessDefinition()}
   * .
   */
  @Test
  public void testCollectorProcessDefinition() {
    CollectorProcessDefinition cpd = new CollectorProcessDefinition("Name 1",
        "sensorId", 10L, "depositoryId", "ownerId");
    assertNotNull(cpd);
    assertTrue("Name 1".equals(cpd.getName()));
    assertTrue("name-1".equals(cpd.getId()));
    assertTrue("sensorId".equals(cpd.getSensorId()));
    assertTrue(10 == cpd.getPollingInterval());
    assertTrue("depositoryId".equals(cpd.getDepositoryId()));
    assertTrue("ownerId".equals(cpd.getOrganizationId()));
  }

  /**
   * Test method for
   * {@link org.wattdepot.common.domainmodel.CollectorProcessDefinition#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEquals() {
    CollectorProcessDefinition cpd1 = InstanceFactory.getCollectorProcessDefinition();
    assertTrue(cpd1.equals(cpd1));
    assertFalse(cpd1.equals(null));
    assertFalse(cpd1.equals(""));
    assertFalse(cpd1.equals(new Object()));
    CollectorProcessDefinition cpd2 = new CollectorProcessDefinition();
    assertFalse(cpd2.hashCode() == cpd1.hashCode());
    assertFalse(cpd2.equals(cpd1));
    cpd2.setDepositoryId("foo");
    assertFalse(cpd2.hashCode() == cpd1.hashCode());
    assertFalse(cpd2.equals(cpd1));
    cpd2.setDepositoryId(cpd1.getDepositoryId());
    assertFalse(cpd2.hashCode() == cpd1.hashCode());
    assertFalse(cpd2.equals(cpd1));
    try {
      cpd2.setId("foo");
    }
    catch (BadSlugException e) {
      fail("Should not happen. " + e.getMessage());
    }
    assertFalse(cpd2.hashCode() == cpd1.hashCode());
    assertFalse(cpd2.equals(cpd1));
    try {
      cpd2.setId("bogus id $%#@!)");
      fail("Should not happen. ");
    }
    catch (BadSlugException e) {
      // expected
    }
    assertFalse(cpd2.hashCode() == cpd1.hashCode());
    assertFalse(cpd2.equals(cpd1));
    try {
      cpd2.setId(cpd1.getId());
    }
    catch (BadSlugException e) {
      fail("Should not happen. " + e.getMessage());
    }
    assertFalse(cpd2.hashCode() == cpd1.hashCode());
    assertFalse(cpd2.equals(cpd1));
    cpd2.setOrganizationId("foo");
    assertFalse(cpd2.hashCode() == cpd1.hashCode());
    assertFalse(cpd2.equals(cpd1));
    cpd2.setOrganizationId(cpd1.getOrganizationId());
    assertFalse(cpd2.hashCode() == cpd1.hashCode());
    assertFalse(cpd2.equals(cpd1));
    cpd2.setPollingInterval(-100l);
    assertFalse(cpd2.hashCode() == cpd1.hashCode());
    assertFalse(cpd2.equals(cpd1));
    cpd2.setPollingInterval(cpd1.getPollingInterval());
    assertFalse(cpd2.hashCode() == cpd1.hashCode());
    assertFalse(cpd2.equals(cpd1));
    cpd2.setProperties(new HashSet<Property>());
    assertFalse(cpd2.hashCode() == cpd1.hashCode());
    assertFalse(cpd2.equals(cpd1));
    Property p = new Property("key", "value");
    cpd2.addProperty(p);
    assertFalse(cpd2.hashCode() == cpd1.hashCode());
    assertFalse(cpd2.equals(cpd1));
    assertNotNull(cpd2.getProperty("key"));
    cpd2.removeProperty(p);
    assertNull(cpd2.getProperty("key"));
    cpd2.setProperties(cpd1.getProperties());
    assertFalse(cpd2.hashCode() == cpd1.hashCode());
    assertFalse(cpd2.equals(cpd1));
    cpd2.setSensorId("foo");
    assertFalse(cpd2.hashCode() == cpd1.hashCode());
    assertFalse(cpd2.equals(cpd1));
    cpd2.setSensorId(cpd1.getSensorId());
    assertFalse(cpd2.hashCode() == cpd1.hashCode());
    assertFalse(cpd2.equals(cpd1));
    cpd2.setName("foo");
    assertFalse(cpd2.hashCode() == cpd1.hashCode());
    assertFalse(cpd2.equals(cpd1));
    cpd2.setName(cpd1.getName());
    assertTrue(cpd2.hashCode() == cpd1.hashCode());
    assertTrue(cpd2.equals(cpd1));
  }

}
