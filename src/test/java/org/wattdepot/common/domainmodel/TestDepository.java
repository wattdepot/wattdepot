/**
 * TestDepository.java This file is part of WattDepot.
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
 * Tests the Depository class.
 * 
 * @author Cam Moore
 * 
 */
public class TestDepository {

  /**
   * Test method for
   * {@link org.wattdepot.common.domainmodel.Depository#Depository()}
   * .
   */
  @Test
  public void testDepository() {
    Depository depo = InstanceFactory.getDepository();
    assertNotNull(depo);
    assertTrue("Test Depository".equals(depo.getName()));
    assertTrue("test-depository".equals(depo.getId()));
    assertTrue("test-user-group".equals(depo.getOrganizationId()));
    assertTrue(depo.isOwner(InstanceFactory.getOrganization()));
    assertFalse(depo.isOwner(InstanceFactory.getOrganization2()));
  }

  /**
   * Test method for
   * {@link org.wattdepot.common.domainmodel.Depository#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsObject() {
    Depository depo1 = InstanceFactory.getDepository();
    assertFalse(depo1.equals(null));
    assertFalse(depo1.equals(""));
    Depository depo2 = new Depository();
    assertFalse(depo1.hashCode() == depo2.hashCode());
    assertFalse(depo2.equals(depo1));
    depo2.setMeasurementType(InstanceFactory.getMeasurementType2());
    assertFalse(depo2.equals(depo1));
    assertFalse(depo2.hashCode() == depo1.hashCode());
    depo2.setMeasurementType(depo1.getMeasurementType());
    assertFalse(depo2.equals(depo1));
    assertFalse(depo2.hashCode() == depo1.hashCode());
    depo2.setName("foo");
    assertFalse(depo2.equals(depo1));
    assertFalse(depo2.hashCode() == depo1.hashCode());
    depo2.setName(depo1.getName());
    assertTrue(depo2.equals(depo1));
    assertTrue(depo2.hashCode() == depo1.hashCode());

  }

}
