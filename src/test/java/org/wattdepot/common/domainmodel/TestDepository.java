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
    assertTrue("test-user-group".equals(depo.getOwnerId()));
  }

  /**
   * Test method for
   * {@link org.wattdepot.common.domainmodel.Depository#equals(java.lang.Object)}
   * .
   */
  @Test
  public void testEqualsObject() {
    Depository depo1 = new Depository();
    Depository depo2 = InstanceFactory.getDepository();
    int hashCode1 = depo1.hashCode();
    int hashCode2 = depo2.hashCode();
    assertFalse(hashCode1 == hashCode2);
    assertFalse(depo1.equals(depo2));
    assertFalse(depo2.equals(depo1));
    assertFalse(depo1.equals(null));
    assertFalse(depo1.equals(new Object()));
    depo1.setName(depo2.getName());
    assertTrue(depo1.getId().equals("test-depository"));
  }

}
