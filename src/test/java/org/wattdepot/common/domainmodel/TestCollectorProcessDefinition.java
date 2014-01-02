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
import static org.junit.Assert.assertTrue;

import org.junit.Test;

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
    assertTrue("name-1".equals(cpd.getSlug()));
    assertTrue("sensorId".equals(cpd.getSensorId()));
    assertTrue(10 == cpd.getPollingInterval());
    assertTrue("depositoryId".equals(cpd.getDepositoryId()));
    assertTrue("ownerId".equals(cpd.getOwnerId()));
  }

  /**
   * Test method for
   * {@link org.wattdepot.common.domainmodel.CollectorProcessDefinition#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsObject() {
    CollectorProcessDefinition cpd1 = new CollectorProcessDefinition();
    CollectorProcessDefinition cpd2 = new CollectorProcessDefinition("Name 1",
        "sensorId", 10L, "depositoryId", "ownerId");
    int hashCode1 = cpd1.hashCode();
    int hashCode2 = cpd2.hashCode();
    assertFalse(hashCode1 == hashCode2);
    assertFalse(cpd1.equals(cpd2));
    assertFalse(cpd2.equals(cpd1));
    assertFalse(cpd1.equals(null));
    assertFalse(cpd1.equals(new Object()));
    cpd1.setName(cpd2.getName());
    assertTrue(cpd1.getSlug().equals("name-1"));
  }

}
